# Stage 1: build
FROM maven:3.9.5-eclipse-temurin-21 AS builder
WORKDIR /cv-app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /cv-app
COPY --from=builder /cv-app/target/cv-management-0.0.1-SNAPSHOT.jar /cv-app/cv-app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/cv-app/cv-app.jar"]
