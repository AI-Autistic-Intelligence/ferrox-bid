package dev.ferrox.bid;

import dev.ferrox.web.FerroxAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(FerroxAutoConfiguration.class) // Load the 7-Layer Onion Pipeline
public class FerroxBidApplication {

    public static void main(String[] args) {
        SpringApplication.run(FerroxBidApplication.class, args);
    }
}
