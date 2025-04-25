FROM eclipse-temurin:17-jre
WORKDIR /app
COPY config-server/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]