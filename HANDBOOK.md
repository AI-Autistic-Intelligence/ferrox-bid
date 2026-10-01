# Ferrox-Bid Real-Time Engine - The Definitive Handbook

## 1. Executive Summary & Senior Engineering Vision

This handbook is the definitive architecture manual for **Ferrox-Bid**, a High-Frequency Real-Time Bidding Engine. 

As a Senior Engineer, I architected this application to solve one of the most devastating and complex problems in high-traffic environments: the **Thundering Herd** (or Cache Stampede). Standard Spring Boot applications collapse under concurrent read spikes. Ferrox-Bid proves the power of the `ferrox-java` framework by leveraging cutting-edge **Java 21 Virtual Threads (Project Loom)**, **Singleflight deduplication**, and **Compile-Time Metaprogramming (APT)** to deliver unprecedented scaling capabilities without relying on heavy external caches like Redis.

---

## 2. The Domain Problem

Imagine an eBay-style auction ending in 10 seconds. 100,000 users frantically refresh the page at the exact same millisecond to check the current highest bid.
If your API simply queries the database, 100,000 simultaneous SQL queries hit the database connection pool. The pool exhausts, the database CPU spikes to 100%, and the system undergoes a catastrophic cascading failure. This is the **Cache Stampede**.

---

## 3. Low-Level Architecture & OS-Kernel Interactions

### 3.1 Java 21 Virtual Threads (Project Loom)
Historically, Java threads were 1:1 mapped to OS Kernel Threads. Each OS thread requires ~1MB of stack memory and a costly kernel trap (context switch) to park or unpark. Blocking 100,000 OS threads would require 100GB of RAM and crash the Linux scheduler.
- **Userland Scheduling**: Ferrox-Bid uses Virtual Threads. These are lightweight threads managed entirely by the JVM, mounted onto a small pool of Carrier Threads (OS threads). Suspending 100,000 Virtual Threads waiting for a database response costs only a few megabytes of heap memory and zero OS context switches.

### 3.2 Singleflight Algorithm (Cache Stampede Protection)
We utilize `ferrox-java-data`'s `SingleflightGroup`. 
- **The Mechanics**: When 100,000 HTTP requests hit the `/api/v1/auctions/{id}/price` endpoint simultaneously, the Singleflight algorithm intercepts them. It registers a single "flight" for the key `auc-123`. 
- **Execution**: Exactly **one** Virtual Thread is allowed to execute the SQL query against PostgreSQL. The other 99,999 Virtual Threads are immediately suspended. 
- **Fan-out**: When the database returns the price, the Singleflight orchestrator unparks all 99,999 Virtual Threads and hands them the identical result in memory. We serve 100,000 users with exactly **one** database query, completely eradicating the Cache Stampede at the algorithmic level.

---

## 4. Application Architecture & Distributed Patterns

### 4.1 Strict CQRS (Command Query Responsibility Segregation)
- **Queries (`GetAuctionPriceQuery`)**: These are high-throughput, horizontally scalable read operations. They are guarded by the Singleflight algorithm.
- **Commands (`PlaceBidCommand`)**: These mutate state. Bids must be validated synchronously against the current highest bid to prevent race conditions. Transactions are strictly scoped to prevent dirty reads.

### 4.2 Compile-Time Metaprogramming (APT)
To generate dynamic Admin UI grids, frontends require JSON Schema definitions of our domain entities (`Auction`). 
- **The Reflection Trap**: Standard frameworks use Java Reflection at runtime to inspect class fields. Reflection is notoriously slow, breaks JIT optimizations, and poisons CPU caches.
- **Zero-Overhead APT**: The `Auction` entity is annotated with `@FerroxEntity`. During `javac` compilation, the `ferrox-java-crud-gen` processor intercepts the AST (Abstract Syntax Tree) and writes a new file: `AuctionSchema.java`. This file contains hardcoded, `O(1)` access schema definitions. We achieve dynamic schemas with zero runtime overhead.

---

## 5. Security & Persistence

- **Bid Integrity**: When processing a `PlaceBidCommand`, the engine acquires a row-level lock (`SELECT ... FOR UPDATE`) to guarantee that if two users bid $500 simultaneously, only one transaction commits, and the other is atomically rejected.
- **Immutable Audit Log**: Similar to our banking core, all successful bids are stored immutably to provide undeniable proof of auction outcomes.

---

## 6. Programmer's Guide (Developer Workflow)

### 6.1 Environment Setup
```bash
# 1. Ensure JDK 21+ is active
./gradlew clean build

# 2. Boot the Spring Application
./gradlew bootRun
```

### 6.2 Working with Singleflight
If you create a new high-throughput endpoint (e.g., fetching a user's profile during login storms), wrap the DB call in Singleflight:
```java
// The key must be uniquely identifiable for the resource
String key = "user-profile-" + userId;
UserProfile profile = singleflightGroup.doTask(key, () -> {
    return userRepository.findById(userId);
});
```

### 6.3 Modifying Entities
If you add a field `private BigDecimal reservePrice;` to the `Auction` entity, you must run `./gradlew build` to trigger the Annotation Processor so it can regenerate the `AuctionSchema.java` file.

---

## 7. User & DevOps Handbook (Operations)

### 7.1 JVM Tuning for Virtual Threads
Virtual Threads heavily rely on the JVM Heap for stack frames.
```bash
java -server \
     -XX:+UseZGC -XX:ZAllocationSpikeTolerance=5 \
     -Xms1G -Xmx1G \
     -Djdk.virtualThreadScheduler.parallelism=8 \
     -jar ferrox-bid.jar
```
- **ZGC**: The Z Garbage Collector guarantees sub-millisecond pauses, crucial for an auction engine where a 100ms pause means a rejected bid.
- `parallelism`: Dictates how many OS Carrier Threads are available to execute the Virtual Threads. Set this to your physical CPU core count.

### 7.2 Verifying the Architecture (Load Testing)
Run the included PowerShell script to fire 500 concurrent threads at the API:
```powershell
./run-load-test.ps1
```
Observe the logs. You will see the DB fetch executed exactly **once**, proving the Singleflight protection.

---

## 8. Senior Engineering Conclusion

Ferrox-Bid is not just another REST API. It is a masterclass in exploiting Java 21's deepest capabilities. By understanding that I/O wait is the enemy of concurrency, and that runtime reflection is the enemy of CPU efficiency, we built an engine that defies traditional scaling limits. Using Virtual Threads and Singleflight, we neutralized the Thundering Herd problem without adding the operational complexity of Redis caching clusters. This is what true framework engineering looks like.
