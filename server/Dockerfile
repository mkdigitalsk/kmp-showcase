FROM gradle:8.11-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
# Use the project's Gradle wrapper (9.1.0) — the image's gradle (8.11) is too old for AGP 9 / Kotlin 2.3.
RUN chmod +x ./gradlew && ./gradlew :server:installDist --no-daemon -PserverOnly=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/server/build/install/server ./

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

CMD ["./bin/server"]
