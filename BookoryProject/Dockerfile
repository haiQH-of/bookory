# =========================
# Build stage
# =========================
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

# Cache deps
COPY pom.xml ./
RUN mvn -q -B -e -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -B -e -DskipTests clean package

# =========================
# Run stage
# =========================
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

COPY --from=build /workspace/target/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]

