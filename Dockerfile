# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /build

# Copy only POM first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-slim
WORKDIR /app

# Add maintainer info
LABEL maintainer="Shreds AI <support@shreds.ai>"

# Create non-root user
RUN useradd -r -u 1001 -g root messageapp
USER messageapp

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=UTC

# Copy jar from build stage
COPY --from=build /build/target/MessagePersister-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
