# Build stage
FROM maven:3.8.6-openjdk-11 AS builder

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests && \
    mvn dependency:purge-local-repository -DmanualInclude=""

# Runtime stage
FROM eclipse-temurin:11-jre-alpine

# Install curl for health check
RUN apk add --no-cache curl

# Create a non-root user to run the application
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy the JAR from builder stage
COPY --from=builder /app/target/movieworld-0.0.1-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8081

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8081/api/movies/health || exit 1

# Run the application with JVM optimizations
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
