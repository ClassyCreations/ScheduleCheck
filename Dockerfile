# BUILD

FROM openjdk:8-jdk-stretch AS build
WORKDIR /app
COPY . ./
RUN ./gradlew --no-daemon stage


# RUN
FROM openjdk:8-jre-slim-stretch
EXPOSE 8080
WORKDIR /app
COPY --from=build /app/app.jar ./
CMD ["java", "-jar", "app.jar"]
