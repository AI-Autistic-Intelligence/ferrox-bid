package dev.ferrox.bid.application;

import dev.ferrox.bid.domain.Auction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;

class BidApplicationTests {

    @Test
    void testPlaceBidSuccess() {
        PlaceBidHandler handler = new PlaceBidHandler();
        // Reset DB for test
        PlaceBidHandler.AUCTION_DB.put("test-auc", new Auction("test-auc", "Item", 100.0, Instant.now().plusSeconds(3600)));
        
        PlaceBidCommand cmd = new PlaceBidCommand("test-auc", "user1", 150.0);
        assertTrue(handler.handle(cmd));
        
        Auction a = PlaceBidHandler.AUCTION_DB.get("test-auc");
        assertEquals(150.0, a.getCurrentHighestBid());
    }

    @Test
    void testPlaceBidFailsIfLower() {
        PlaceBidHandler handler = new PlaceBidHandler();
        PlaceBidHandler.AUCTION_DB.put("test-auc-2", new Auction("test-auc-2", "Item", 100.0, Instant.now().plusSeconds(3600)));
        
        PlaceBidCommand cmd = new PlaceBidCommand("test-auc-2", "user1", 50.0);
        assertFalse(handler.handle(cmd)); // 50 is lower than 100
        
        Auction a = PlaceBidHandler.AUCTION_DB.get("test-auc-2");
        assertEquals(100.0, a.getCurrentHighestBid()); // Unchanged
    }
    
    @Test
    void testPlaceBidFailsIfExpired() {
        PlaceBidHandler handler = new PlaceBidHandler();
        PlaceBidHandler.AUCTION_DB.put("test-auc-3", new Auction("test-auc-3", "Item", 100.0, Instant.now().minusSeconds(1))); // Expired
        
        PlaceBidCommand cmd = new PlaceBidCommand("test-auc-3", "user1", 150.0);
        assertFalse(handler.handle(cmd)); 
    }
}
