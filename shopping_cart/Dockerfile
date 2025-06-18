FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Now the path is relative to root, so we add 'shopping_cart/':
COPY shopping_cart/target/shopping_cart-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]