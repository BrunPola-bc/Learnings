# Stage 1: build
FROM maven:3.9.5-eclipse-temurin-21 AS builder
WORKDIR /rest-app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /rest-app
COPY --from=builder /rest-app/target/rest-to-my-db-0.0.1-SNAPSHOT.jar /rest-app/rest-to-my-db.jar
EXPOSE 1607
ENTRYPOINT ["java","-jar","/rest-app/rest-to-my-db.jar"]