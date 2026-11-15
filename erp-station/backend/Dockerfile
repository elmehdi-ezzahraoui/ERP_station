# Image generique : construire d'abord les jars avec  mvn clean package -DskipTests
FROM eclipse-temurin:17-jre
ARG MODULE
WORKDIR /app
COPY ${MODULE}/target/${MODULE}-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
