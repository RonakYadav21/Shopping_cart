FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the JAR file and rename it to app.jar
COPY target/shopping_cart-1.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]