# Multi-Stage Dockerfile for Ferrox-Bid (Java 21 & Gradle)
FROM gradle:8.8-jdk21-alpine AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENV SERVER_PORT=8081
EXPOSE 8081

ENTRYPOINT ["java", "--enable-preview", "-XX:+UseZGC", "-XX:+ZGenerational", "-jar", "app.jar"]
