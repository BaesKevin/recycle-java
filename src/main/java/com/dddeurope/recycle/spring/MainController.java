package com.dddeurope.recycle.spring;

import com.dddeurope.recycle.commands.CalculatePrice;
import com.dddeurope.recycle.commands.CommandMessage;
import com.dddeurope.recycle.domain.Customer;
import com.dddeurope.recycle.domain.Drop;
import com.dddeurope.recycle.domain.Visit;
import com.dddeurope.recycle.events.EventMessage;
import com.dddeurope.recycle.events.FractionWasDropped;
import com.dddeurope.recycle.events.IdCardRegistered;
import com.dddeurope.recycle.events.IdCardScannedAtEntranceGate;
import com.dddeurope.recycle.events.IdCardScannedAtExitGate;
import com.dddeurope.recycle.events.PriceWasCalculated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/validate")
    public String validate() {
        return "Hi!";
    }

    private final List<Visit> visits = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();

    @PostMapping("/handle-command")
    public ResponseEntity<EventMessage> handle(@RequestBody RecycleRequest request) {
        LOGGER.info("Incoming Request: {}", request.asString());

        request.history().forEach(event -> {
            if(event.getPayload() instanceof IdCardRegistered idCardRegisteredEvent) {
                customers.add(new Customer(idCardRegisteredEvent.cardId(), idCardRegisteredEvent.personId(), idCardRegisteredEvent.address(),
                    idCardRegisteredEvent.city()));
            } else if (event.getPayload() instanceof IdCardScannedAtEntranceGate idCardScannedAtEntranceGate) {
                visits.add(new Visit(idCardScannedAtEntranceGate.cardId()));
            } else if (event.getPayload() instanceof FractionWasDropped fractionWasDropped) {
                Visit visit = getVisit(fractionWasDropped.cardId());

                visit.registerDrop(new Drop(fractionWasDropped.fractionType(), fractionWasDropped.weight()));
            } else if (event.getPayload() instanceof IdCardScannedAtExitGate idCardScannedAtExitGate) {
                Visit visit = getVisit(idCardScannedAtExitGate.cardId());
                visit.close();
            }
        });

        if (request.command().getPayload() instanceof CalculatePrice calculatePrice) {
            Visit visit = getVisit(calculatePrice.cardId());

            var message = new EventMessage("todo", new PriceWasCalculated("123", visit.calculatePrice(), "EUR"));

            return ResponseEntity.ok(message);
        }

        return ResponseEntity.badRequest().build();
    }

    private Visit getVisit(String cardId) {
        return visits.stream()
            .filter(it -> cardId.equals(it.cardId()))
            .findFirst()
            .orElseThrow();
    }

    public record RecycleRequest(List<EventMessage> history, CommandMessage command) {

        public String asString() {
            var historyAsString = history.stream()
                    .map(EventMessage::toString)
                    .collect(Collectors.joining("\n\t"));

            return String.format("%n%s %nWith History\n\t%s", command, historyAsString);
        }

    }

}
