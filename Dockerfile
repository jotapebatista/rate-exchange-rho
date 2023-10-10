# Build the application
FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN ./mvnw clean package

# Create the docker image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/Rate-Exchange-RHO-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
