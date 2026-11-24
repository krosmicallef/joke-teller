# joke-teller-sender

Standalone Spring Boot worker that periodically publishes saved jokes to Kafka for subscriber notifications. It has no HTTP server and exposes no cURL-callable endpoints.

## Flow

1. On the configured schedule, load one random joke from joke history.
2. Page through subscriber records.
3. Publish one JSON record per subscriber to Kafka, using the subscriber email as the message key.

Message shape:

```json
{"emailAddress":"jane@example.com","text":"A saved joke"}
```

This module produces messages only; a downstream consumer is responsible for delivering notifications. The main [README](../README.md) contains the architecture diagram and API examples used to create/verify subscribers and jokes.

## Run

Configure a reachable MySQL database, `OPENAI_API_KEY`, and Kafka broker, then run from the repository root:

```bash
mvn -pl joke-teller-sender -am package
java -jar joke-teller-sender/target/joke-teller-sender-0.0.1-SNAPSHOT.jar
```

Relevant settings:

- `KAFKA_BOOTSTRAP_SERVERS`: broker address (default `localhost:9092`).
- `JOKE_TOPIC`: destination topic (default `joke-sender-topic`).
- `SENDER_SCHEDULE_CRON`: Spring cron schedule (default `0 * * * * *`, every minute).
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`: shared database connection.
- `OPENAI_API_KEY`: used by shared services when generation is needed; sender reads saved jokes.
