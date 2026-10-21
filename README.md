# Joke Teller

## This project was created for experimenting with LLM APIs and other technologies
I have made use of OpenAI to build an agent which can be asked for a punch line / joke. A tool can decide whether the
joke should be for kids or adults. 

## Running Locally
```bash
mvn clean install

java -jar ./target/joke-teller-0.0.1-SNAPSHOT.jar
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
curl --location 'http://localhost:8080/api/jokes?prompt=A%20kid%20friendly%20joke' \
--data ''
```