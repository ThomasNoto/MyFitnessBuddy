FROM openjdk:17-jdk-slim

# Set working dir
WORKDIR /app

# Copy jar from Maven build 
COPY target/*.jar app.jar

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]