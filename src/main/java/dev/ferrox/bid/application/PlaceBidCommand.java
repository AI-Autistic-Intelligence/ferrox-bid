package dev.ferrox.bid.application;

import dev.ferrox.cqrs.Command;

public record PlaceBidCommand(String auctionId, String bidderId, double amount) implements Command<Boolean> {
}
