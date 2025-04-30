FROM maven:3.9.5-eclipse-temurin-21 as builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:21-jdk-slim
VOLUME /tmp
EXPOSE 8080
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
