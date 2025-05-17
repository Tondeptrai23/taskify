FROM eclipse-temurin:17-jre
WORKDIR /app
COPY microservices/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]