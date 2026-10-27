FROM eclipse-temurin:25-jre
WORKDIR /app
COPY joke-teller-app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
