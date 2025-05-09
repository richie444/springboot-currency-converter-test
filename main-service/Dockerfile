FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

COPY . .
RUN ./gradlew clean build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]