FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY relay-common/pom.xml ./relay-common/
COPY relay-simulator/pom.xml ./relay-simulator/
COPY relay-processor/pom.xml ./relay-processor/
COPY relay-api/pom.xml ./relay-api/
COPY relay-common/src ./relay-common/src
COPY relay-simulator/src ./relay-simulator/src
COPY relay-processor/src ./relay-processor/src
COPY relay-api/src ./relay-api/src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/relay-api/target/relay-api-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

