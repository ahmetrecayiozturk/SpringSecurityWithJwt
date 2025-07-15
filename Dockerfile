# Use a base image that has OpenJDK 21
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set the working directory for Gradle
WORKDIR /app

# Copy the Gradle wrapper files and build.gradle files
COPY gradle ./gradle
COPY gradlew .
COPY app/build.gradle ./app/build.gradle
COPY settings.gradle .

# Copy the rest of the project source code
COPY app/src ./app/src

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Install ca-certificates to fix SSL issues
RUN apk add --no-cache ca-certificates

# Build the project using Gradle, skip tests to avoid database dependencies
RUN ./gradlew build -x test --no-daemon

# Now, use a fresh OpenJDK 21 image to run the app
FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the jar file from the build step (from the builder image)
COPY --from=builder /app/app/build/libs/*.jar /app/app.jar

# Expose the port your application will run on (adjust if necessary)
EXPOSE 8080

# Run the Java application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
