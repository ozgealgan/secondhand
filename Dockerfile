FROM openjdk:11 AS build

COPY . .
RUN ./gradlew build

FROM openjdk:11
WORKDIR warehouse
COPY --from=build taegrt/*.jar secondhand.jar
ENTRYPOINT ["java", "-jar", "secondhand.jar"]