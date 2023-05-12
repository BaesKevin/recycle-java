package com.dddeurope.recycle.domain;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class VisitPriceCalculator {

    private final Map<String, Map<String, BigDecimal>> fractionTypePricesInCity = Map.of("South park", Map.of(
        "Green waste", new BigDecimal("0.12"),
        "Construction waste", new BigDecimal("0.18")
    ));
    private final Map<String, BigDecimal> defaultPricesPerFractionType = Map.of(
        "Green waste", new BigDecimal("0.09"),
        "Construction waste", new BigDecimal("0.15")
    );

    public BigDecimal calculatePriceForVisit(Visit visit, List<Customer> customers) {
        Customer customer = customers.stream().filter(it -> it.cardId().equals(visit.cardId())).findFirst().orElseThrow();

        Map<String, Map<String, BigDecimal>> fractionTypePricesInCity = this.fractionTypePricesInCity;
        Map<String, BigDecimal> applicablePricesPerFractionType = fractionTypePricesInCity.getOrDefault(customer.city(), defaultPricesPerFractionType);

        return visit.getDrops().stream()
            .map(it -> applicablePricesPerFractionType.getOrDefault(it.type(), ZERO).multiply(new BigDecimal(it.weight())))
            .reduce(BigDecimal::add)
            .orElse(ZERO);
    }
}
