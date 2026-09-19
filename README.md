# Ferrox-Bid: Real-Time High-Frequency Bidding Engine

**Ferrox-Bid** is a high-performance, real-time auction engine built to demonstrate the immense capabilities of the `ferrox-java` framework. It solves complex concurrency and security challenges out of the box without relying on heavy third-party infrastructure.

## Why Ferrox-Bid?

In a standard Spring Boot application, if an auction is ending and 10,000 users refresh the page at the exact same millisecond to check the current price, the application will execute 10,000 simultaneous SQL queries. This causes a phenomenon known as a **Cache Stampede (or Dogpile)**, resulting in database connection pool exhaustion and complete system failure.

**Ferrox-Bid solves this elegantly.**

## Architectural Highlights

### 1. Singleflight Cache Stampede Prevention
By utilizing `ferrox-java-data`'s `SingleflightGroup`, the `GetAuctionPriceHandler` guarantees that regardless of how many concurrent requests hit the `/api/v1/auctions/{id}/price` endpoint, the database fetch logic is executed **exactly once**. All other 9,999 concurrent threads are suspended (using lightweight Virtual Threads) and instantly receive the result of the single successful query once it completes.

### 2. CQRS Pattern
The domain logic is strictly segregated:
- **Queries:** `GetAuctionPriceQuery` (High throughput, heavily cached/singleflighted reads).
- **Commands:** `PlaceBidCommand` (Transactional, synchronized writes ensuring bid validity and expiration).

### 3. Compile-Time Metaprogramming (APT)
The `Auction` domain entity is annotated with `@FerroxEntity`. During compilation, `ferrox-java-crud-gen` intercepts this annotation and generates `AuctionSchema.java`, providing zero-overhead JSON schema descriptions for the Admin UI Grid.

## Running the Application

1. Ensure Java 21 is installed.
2. Build the project using Gradle (this will trigger the APT Code Gen):
   ```bash
   ./gradlew build
   ```
3. Run the Spring Boot application:
   ```bash
   ./gradlew bootRun
   ```

## Verifying the Architecture (Load Testing)

You can verify the Cache Stampede prevention by running the included PowerShell script:

```powershell
./run-load-test.ps1
```

This script will fire 500 concurrent threads at the exact same millisecond against the API. 
Observe the Spring Boot console output: you will see the `--- FETCHING PRICE FROM DB FOR AUCTION: auc-123 ---` log statement printed exactly **once**, proving the system's absolute resilience under load.
