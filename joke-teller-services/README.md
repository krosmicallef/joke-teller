# joke-teller-services

Shared Java library module used by the HTTP application and the scheduled sender. It contains joke and riddle agents, service-layer logic, subscriber and admin account operations, JPA entities, and repositories.

This module does not start an HTTP server and exposes no cURL-callable endpoints. Its consumers are `joke-teller-app` and `joke-teller-sender`.

Build it from the repository root (Maven also builds required modules):

```bash
mvn -pl joke-teller-services -am package
```

Configuration is supplied by the consuming application. Joke and riddle generation uses `OPENAI_API_KEY`; persistence uses the consumer's `spring.datasource.*` configuration. See the [root README](../README.md) for the architecture and API routes.
