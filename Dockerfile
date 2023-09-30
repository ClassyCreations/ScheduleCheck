# BUILD

FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . ./
RUN ./gradlew --no-daemon stage


# RUN
FROM eclipse-temurin:17-jre
EXPOSE 8080
WORKDIR /app
COPY --from=build /app/app.jar ./
CMD ["java", "-jar", "app.jar"]
