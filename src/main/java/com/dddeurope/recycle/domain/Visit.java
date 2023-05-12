package com.dddeurope.recycle.domain;

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

    public boolean isOngoing() {
        return !isClosed;
    }

    public String cardId() {
        return cardId;
    }

    public void registerDrop(Drop drop) {
        drops.add(drop);
    }

    // TODO how can we do this cleanly without exposing the drops list
    public List<Drop> getDrops() {
        return List.copyOf(drops);
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

