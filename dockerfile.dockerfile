# Use official OpenJDK 17 base image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8888

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]