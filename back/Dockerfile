# back/Dockerfile
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src

RUN mvn -B -f pom.xml clean package -DskipTests=true

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=/workspace/target/*.jar
COPY --from=build ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
