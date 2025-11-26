# ─────────────────────────────────────────────
# Stage 1: Build the Spring Boot JAR with Maven
# ─────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Workdir inside the container
WORKDIR /app

# Copy Maven descriptor and sources
COPY pom.xml .
COPY src ./src

# Build the app (skip tests to speed up)
RUN mvn -B -DskipTests clean package

# ─────────────────────────────────────────────
# Stage 2: Runtime image with only JRE + JAR
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Spring Boot default port
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
