package dev.ferrox.bid.application;

import dev.ferrox.eventmanager.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record BidPlacedEvent(String eventId, Instant occurredOn, String auctionId, double amount, String bidderId) implements DomainEvent {
    public BidPlacedEvent(String auctionId, double amount, String bidderId) {
        this(UUID.randomUUID().toString(), Instant.now(), auctionId, amount, bidderId);
    }

    @Override
    public String getEventId() { return eventId; }
    
    @Override
    public Instant getOccurredOn() { return occurredOn; }
}
