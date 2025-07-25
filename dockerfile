# Build stage (using a valid Maven + JDK 17 image)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Run stage (unchanged)
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/healthcare-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]