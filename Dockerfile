# Etapa de construcción
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa de empaquetado (runtime)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/parcial-0.0.1-SNAPSHOT.jar parcial.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","parcial.jar"]
