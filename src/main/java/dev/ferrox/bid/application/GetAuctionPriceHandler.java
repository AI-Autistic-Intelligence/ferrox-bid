package dev.ferrox.bid.application;

import dev.ferrox.bid.domain.Auction;
import dev.ferrox.cqrs.QueryHandler;
import dev.ferrox.data.SingleflightGroup;
import dev.ferrox.offheap.OffHeapAuctionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetAuctionPriceHandler implements QueryHandler<GetAuctionPriceQuery, Double> {

    private static final Logger log = LoggerFactory.getLogger(GetAuctionPriceHandler.class);
    private final SingleflightGroup singleflight;
    private final OffHeapAuctionCache offHeapCache;

    public GetAuctionPriceHandler(SingleflightGroup singleflight, OffHeapAuctionCache offHeapCache) {
        this.singleflight = singleflight;
        this.offHeapCache = offHeapCache;
    }

    @Override
    public Double handle(GetAuctionPriceQuery query) {
        // Here lies the true power of ferrox-java.
        // If 10,000 users ask for 'auc-123' at the exact same millisecond,
        // this block executes exactly ONCE for the database.
        
        return singleflight.work("auction_price_" + query.auctionId(), () -> {
            log.info("--- FETCHING PRICE FROM DB FOR AUCTION: {} ---", query.auctionId());
            
            // Simulating slow database I/O
            Thread.sleep(200); 
            
            return offHeapCache.getCurrentHighestBid(query.auctionId());
        });
    }
}
