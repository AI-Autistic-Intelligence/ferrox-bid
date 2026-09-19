package dev.ferrox.bid.presentation;

import dev.ferrox.bid.application.GetAuctionPriceQuery;
import dev.ferrox.bid.application.PlaceBidCommand;
import dev.ferrox.cqrs.CommandBus;
import dev.ferrox.cqrs.QueryBus;
import dev.ferrox.security.RequireRole;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public AuctionController(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @GetMapping("/{id}/price")
    public Double getPrice(@PathVariable String id) {
        // Safe to spam, Singleflight protects the DB
        return queryBus.dispatch(new GetAuctionPriceQuery(id));
    }

    @PostMapping("/{id}/bid")
    @RequireRole("USER")
    public String placeBid(@PathVariable String id, @RequestBody BidRequest req) {
        // Authenticated users only
        boolean success = commandBus.dispatch(new PlaceBidCommand(id, req.bidderId(), req.amount()));
        return success ? "Bid Accepted!" : "Bid Rejected!";
    }

    public record BidRequest(String bidderId, double amount) {}
}
