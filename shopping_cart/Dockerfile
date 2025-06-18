FROM eclipse-temurin:17-jdk-alpine

# Install Maven
RUN apk add --no-cache maven

# Set workdir
WORKDIR /app

# Copy everything
COPY . .

# Build the app (downloads dependencies and builds jar)
RUN mvn clean package -DskipTests

# Run the jar
CMD ["java", "-jar", "target/shopping_cart-1.0.jar"]
