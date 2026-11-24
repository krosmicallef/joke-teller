# joke-teller-app

Spring MVC application exposing the Joke Teller HTTP API on port `8080` by default. It depends on `joke-teller-services` for joke/riddle generation, persistence, subscriber management, and admin account operations.

Start from the repository root after configuring MySQL, `OPENAI_API_KEY`, and an `ADMIN_JWT_SECRET` of at least 32 characters:

```bash
mvn -pl joke-teller-app -am package
java -jar joke-teller-app/target/joke-teller-0.0.1-SNAPSHOT.jar
```

Base URL in the examples: `http://localhost:8080`.

## Public API

```bash
# GET /api/hello
curl -i http://localhost:8080/api/hello

# GET /api/health
curl -i http://localhost:8080/api/health

# GET /api/jokes?prompt=... (prompt optional)
curl -G --data-urlencode 'prompt=Tell me a kid friendly joke about space' http://localhost:8080/api/jokes
curl http://localhost:8080/api/jokes

# GET /api/jokes/random (404 if joke history is empty)
curl -i http://localhost:8080/api/jokes/random

# GET /api/riddles?prompt=... (prompt optional)
curl -G --data-urlencode 'prompt=Give me a riddle about the ocean' http://localhost:8080/api/riddles
curl http://localhost:8080/api/riddles

# POST /api/subscribers (POST /api/subscriptions is an alias)
curl -i -X POST http://localhost:8080/api/subscribers \
  -H 'Content-Type: application/json' -d '{"email":"jane@example.com"}'
curl -i -X POST http://localhost:8080/api/subscriptions \
  -H 'Content-Type: application/json' -d '{"email":"jane@example.com"}'

# POST /api/subscribers/verify (the mock email sender logs the six-digit code)
curl -i -X POST http://localhost:8080/api/subscribers/verify \
  -H 'Content-Type: application/json' \
  -d '{"email":"jane@example.com","code":"123456"}'
```

## Admin API

Except for login, all `/api/admin/**` endpoints require `Authorization: Bearer <token>`. Login with the bootstrap credentials to set `TOKEN` for the commands below.

```bash
# POST /api/admin/auth/login
TOKEN=$(curl -sS -X POST http://localhost:8080/api/admin/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"replace-with-your-password"}' \
  | python3 -c 'import json,sys; print(json.load(sys.stdin)["token"])')

# POST /api/admin/auth/logout
curl -i -X POST http://localhost:8080/api/admin/auth/logout -H "Authorization: Bearer $TOKEN"

# GET /api/admin/users
curl -i http://localhost:8080/api/admin/users -H "Authorization: Bearer $TOKEN"

# POST /api/admin/users (password: 12-200 characters)
curl -i -X POST http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"username":"operator","password":"a-long-password-123"}'

# PUT /api/admin/users/{id}; username and password are optional
curl -i -X PUT http://localhost:8080/api/admin/users/1 \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"username":"operator-updated","password":"another-long-password-123"}'

# DELETE /api/admin/users/{id}; final admin cannot be deleted
curl -i -X DELETE http://localhost:8080/api/admin/users/2 -H "Authorization: Bearer $TOKEN"

# GET /api/admin/subscriptions?page=0&size=20 (Spring pageable parameters)
curl -i 'http://localhost:8080/api/admin/subscriptions?page=0&size=20' -H "Authorization: Bearer $TOKEN"

# POST /api/admin/subscriptions
curl -i -X POST http://localhost:8080/api/admin/subscriptions \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"email":"alex@example.com"}'

# PUT /api/admin/subscriptions/{id}
curl -i -X PUT http://localhost:8080/api/admin/subscriptions/1 \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"email":"alex@example.com","verified":true}'

# DELETE /api/admin/subscriptions/{id}
curl -i -X DELETE http://localhost:8080/api/admin/subscriptions/1 -H "Authorization: Bearer $TOKEN"

# GET /api/admin/me
curl -i http://localhost:8080/api/admin/me -H "Authorization: Bearer $TOKEN"
```

## Configuration

- `OPENAI_API_KEY`: OpenAI API key.
- `ADMIN_JWT_SECRET`: signing secret of at least 32 characters.
- `ADMIN_JWT_LIFETIME_SECONDS`: token lifetime (default `1800`).
- `ADMIN_BOOTSTRAP_USERNAME` / `ADMIN_BOOTSTRAP_PASSWORD`: insert-only initial administrator credentials.
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`: database connection.

The main repository [README](../README.md) includes the module architecture and Kafka notification flow.
