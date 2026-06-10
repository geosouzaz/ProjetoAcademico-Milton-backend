FROM --platform=linux/amd64 maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copia apenas o pom primeiro para aproveitar cache do Docker
COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o artefato construído
COPY --from=build /workspace/target/matricula-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","/app/app.jar"]
