# Container image for the backend, used for Azure Container Apps / App Service.
# Secrets are never baked in: everything in .env.example is passed as environment variables.

# ---- build ----
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src/ src/
# The context-load test needs a live database, so tests run in CI, not in the image build.
RUN mvn -B -q package -DskipTests \
    && cp target/voicelk_be-*.jar /src/app.jar

# ---- run ----
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 10001 voicelk
COPY --from=build /src/app.jar app.jar
USER 10001

EXPOSE 8082
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
