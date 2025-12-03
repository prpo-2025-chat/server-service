
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app-server
COPY . .

RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jdk-alpine

COPY --from=build /app-server/api/target/api-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]