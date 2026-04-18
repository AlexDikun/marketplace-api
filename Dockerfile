FROM gradle:8.7-jdk21 AS build

WORKDIR /app
COPY . .

RUN gradle clean build --no-daemon

FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY --from=build /app/build/libs/app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

