FROM gradle:8.4-jdk17 AS build
WORKDIR /home/gradle/src

# Copy gradle config files first to leverage Docker cache
COPY --chown=gradle:gradle build.gradle settings.gradle gradle.properties /home/gradle/src/

# Configure repository mirrors and network settings
RUN echo "systemProp.https.protocols=TLSv1.2,TLSv1.3" >> /home/gradle/.gradle/gradle.properties && \
    echo "systemProp.maven.repo.central=https://repo1.maven.org/maven2/" >> /home/gradle/.gradle/gradle.properties && \
    echo "systemProp.org.gradle.internal.http.connectionTimeout=180000" >> /home/gradle/.gradle/gradle.properties && \
    echo "systemProp.org.gradle.internal.http.socketTimeout=180000" >> /home/gradle/.gradle/gradle.properties

# Download dependencies
RUN gradle dependencies --no-daemon

# Copy the rest of the files and build
COPY --chown=gradle:gradle . /home/gradle/src
RUN gradle build --no-daemon -x test --info

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]