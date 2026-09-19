package dev.ferrox.bid.application;

import dev.ferrox.bid.domain.Auction;
import dev.ferrox.cqrs.CommandHandler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.ferrox.eventmanager.EventBus;
import dev.ferrox.offheap.OffHeapAuctionCache;

@Service
public class PlaceBidHandler implements CommandHandler<PlaceBidCommand, Boolean> {

    private final EventBus eventBus;
    private final OffHeapAuctionCache offHeapCache;

    public PlaceBidHandler(EventBus eventBus, OffHeapAuctionCache offHeapCache) {
        this.eventBus = eventBus;
        this.offHeapCache = offHeapCache;
    }

    @Override
    public Boolean handle(PlaceBidCommand command) {
        boolean success = offHeapCache.placeBidIfHigher(command.auctionId(), command.amount());
        if (success) {
            // Publish Domain Event asynchronously
            eventBus.publish(new BidPlacedEvent(command.auctionId(), command.amount(), command.bidderId()));
        }
        return success;
    }
}
