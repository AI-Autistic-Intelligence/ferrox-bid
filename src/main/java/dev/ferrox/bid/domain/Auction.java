package dev.ferrox.bid.domain;

import dev.ferrox.crudgen.annotation.FerroxEntity;
import dev.ferrox.crudgen.annotation.FerroxField;

import java.time.Instant;

@FerroxEntity(table = "auctions", roleRead = "USER", roleWrite = "ADMIN", adminGrid = true)
public class Auction {

    @FerroxField(isPrimaryKey = true)
    private String id;
    
    @FerroxField(isSearchable = true)
    private String itemName;
    
    private double currentHighestBid;
    private String highestBidderId;
    private Instant endTime;

    // Constructors, Getters, Setters omitted for brevity in demo
    public Auction(String id, String itemName, double startingPrice, Instant endTime) {
        this.id = id;
        this.itemName = itemName;
        this.currentHighestBid = startingPrice;
        this.endTime = endTime;
    }

    public String getId() { return id; }
    public double getCurrentHighestBid() { return currentHighestBid; }
    public void setCurrentHighestBid(double currentHighestBid) { this.currentHighestBid = currentHighestBid; }
    public Instant getEndTime() { return endTime; }
}
