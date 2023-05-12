package com.dddeurope.recycle.domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Visit {
    private final String cardId;
    private final List<Drop> drops = new ArrayList<>();
    private boolean isClosed = false;

    public Visit(String cardId) {
        this.cardId = cardId;
    }

    public void close() {
        this.isClosed = true;
    }

    public String cardId() {
        return cardId;
    }

    public void registerDrop(Drop drop) {
        drops.add(drop);
    }

    public double calculatePrice() {
        return drops.stream()
            .mapToDouble(it ->
                {
                    if (it.type().equals("Construction waste")) {
                        return it.weight() * 0.15;
                    } else if (it.type().equals("Green waste")) {
                        return it.weight() * 0.09;
                    } else {
                        return 0.0;
                    }
                }
            )
            .sum();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Visit) obj;
        return Objects.equals(this.cardId, that.cardId) &&
            Objects.equals(this.drops, that.drops);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, drops);
    }

    @Override
    public String toString() {
        return "Visit[" +
            "cardId=" + cardId + ", " +
            "drops=" + drops + ']';
    }
}

