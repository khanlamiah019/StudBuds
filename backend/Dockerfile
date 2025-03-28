# Use OpenJDK 21 base image to run the jar built with Java 21
FROM openjdk:21-jdk-slim
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/studbuds-backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]