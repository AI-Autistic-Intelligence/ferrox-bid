package dev.ferrox.bid.application;

import dev.ferrox.cqrs.Query;

public record GetAuctionPriceQuery(String auctionId) implements Query<Double> {
}
