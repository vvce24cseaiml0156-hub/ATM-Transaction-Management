# Stage 1: Build and Test Application using Maven & OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven Project Object Model definition
COPY pom.xml .

# Download dependencies offline for efficient caching
RUN mvn dependency:go-offline -B || true

# Copy source code and test suites
COPY src ./src

# Compile source code and execute all JUnit 5 test cases
RUN mvn clean package

# Stage 2: Minimal, secure JRE 17 Runtime Environment
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create unprivileged application user and group
RUN addgroup -S atmgroup && adduser -S atmuser -G atmgroup

# Copy compiled executable JAR from build stage
COPY --from=build /app/target/atm-transaction-management.jar app.jar

# Enforce non-root execution
USER atmuser

# Container execution command
ENTRYPOINT ["java", "-jar", "app.jar"]
