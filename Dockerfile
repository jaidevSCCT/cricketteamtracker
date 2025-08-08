# -----------------------------
# Stage 1: Cache Gradle dependencies
# -----------------------------
FROM gradle:latest  AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY build.gradle.* settings.gradle.kts gradle.properties /home/gradle/app/
COPY gradle /home/gradle/app/gradle
WORKDIR /home/gradle/app
RUN gradle clean build -i --stacktrace

# -----------------------------
# Stage 2: Build the application fat jar
# -----------------------------
FROM gradle:latest  AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# -----------------------------
# Stage 3: Create the runtime image
# -----------------------------
FROM amazoncorretto:22-alpine AS runtime
EXPOSE 9090
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
