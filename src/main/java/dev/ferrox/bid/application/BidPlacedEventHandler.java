package dev.ferrox.bid.application;

import dev.ferrox.eventmanager.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BidPlacedEventHandler implements EventHandler<BidPlacedEvent> {

    private static final Logger log = LoggerFactory.getLogger(BidPlacedEventHandler.class);

    @Override
    public void onEvent(BidPlacedEvent event) {
        // This runs asynchronously on a separate Virtual Thread!
        log.info("🔔 ASYNC NOTIFICATION: New highest bid of ${} on auction {} by {}", 
            event.amount(), event.auctionId(), event.bidderId());
    }
}
