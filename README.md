# Joke Teller

## This project was created for experimenting with LLM APIs and other technologies
I have made use of OpenAI to build an agent which can be asked for a punch line / joke. A tool can decide whether the
joke should be for kids or adults. 

## Running Locally
```bash
mvn clean install

java -jar ./joke-teller-app/target/joke-teller-0.0.1-SNAPSHOT.jar
```
## Docker

To create your application image and run with a provided MySQL:

```bash
docker build -t joke-teller .

docker run -p 8080:8080 joke-teller
```

To run your application and MySQL as docker containers:

```bash
docker compose up
```

## Configuration
```bash
spring:
  application:
    name: joke-teller

  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: user
    password: password

  jpa:
    hibernate:
      ddl-auto: validate
      dialect: org.hibernate.dialect.MySQLDialect
    show-sql: true
    properties:
      hibernate:
        format_sql: true

openai:
  api-key: ADD_YOUR_OPEN_AI_API_KEY

logging:
  level:
    com.christophermicallef: DEBUG
```

## API Endpoints
```bash
curl --location --request GET 'http://localhost:8080/api/jokes?prompt=A%20kid%20friendly%20joke'
```


## Admin access

Admin endpoints live under `/api/admin/**`. The login endpoint is `POST /api/admin/auth/login`; the remaining admin endpoints require `Authorization: Bearer <token>`. Tokens are signed with HS256 and expire after 30 minutes by default. Configure a secret of at least 32 characters with `ADMIN_JWT_SECRET` before using login. Set `ADMIN_JWT_LIFETIME_SECONDS` to change the lifetime.

The Flyway migration `V3__create_admin_user_table.sql` creates the admin user table. To provision the first account, set `ADMIN_BOOTSTRAP_USERNAME` and `ADMIN_BOOTSTRAP_PASSWORD` when starting the app. The password is stored as a salted PBKDF2 hash. Bootstrap is insert-only: it does not replace an existing account's password. After login, administrators can list or create accounts through `GET` and `POST /api/admin/users`; newly created passwords must be at least 12 characters. These endpoints never return password hashes.

Login request:

```json
{"username":"admin","password":"your-password"}
```

A successful response includes `token`, `tokenType` (`Bearer`), and `expiresIn` in seconds. `GET /api/admin/me` is a protected endpoint that can be used to confirm the token is valid.
