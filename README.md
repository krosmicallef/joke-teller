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
```bash
docker build -t joke-teller .

docker run -p 8080:8080 joke-teller
```

## Configuration
```bash
spring:
  application:
    name: joke-teller

openai:
  api-key: ADD_KEY

logging:
  level:
    com.christophermicallef: DEBUG
```

## API Endpoints
```bash
curl --location 'http://localhost:8080/api/jokes?prompt=A%20kid%20friendly%20joke' \
--data ''
```