# Use Eclipse Temurin Java 17 Alpine image for small footprint
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built JAR into the image
COPY target/account-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port on which the Spring Boot service runs
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
