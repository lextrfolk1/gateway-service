

FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /build

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy full project and package
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /gateway-service

COPY --from=build /build/target/*.jar gateway-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "gateway-service.jar"]