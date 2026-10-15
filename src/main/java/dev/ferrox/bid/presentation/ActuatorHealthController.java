package dev.ferrox.bid.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class ActuatorHealthController {

    @GetMapping(value = {"/actuator/health", "/actuator/health/**", "/health"})
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "service", "ferrox-bid-engine",
            "runtime", "Java 21 Project Loom",
            "timestamp", Instant.now().toString(),
            "components", Map.of(
                "singleflight", Map.of("status", "UP"),
                "auctionEngine", Map.of("status", "UP")
            )
        );
    }
}
