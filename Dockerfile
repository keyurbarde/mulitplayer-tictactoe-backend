# Use a base image with Java
FROM eclipse-temurin:17-jdk-alpine

# Copy the built jar file into the image
COPY build/libs/*.jar app.jar

COPY ./target/ws-tictactoe-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]