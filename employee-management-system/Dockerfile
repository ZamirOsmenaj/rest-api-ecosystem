# ---------- Stage 1: Build ----------
# Compiles the application and produces an executable jar. This stage's
# image is discarded after the build, so the Maven distribution and the
# full dependency cache never end up in the final image.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only the POM first so Docker can cache the downloaded dependencies
# as a separate layer; they only get re-downloaded when pom.xml changes,
# not on every source code change.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Stage 2: Run ----------
# A minimal JRE (not a full JDK) is enough to run the already-built jar,
# which keeps the final image small and reduces its attack surface.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as a non-root user, standard practice for container hardening.
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/target/employee-management-system.jar app.jar
RUN mkdir -p /app/logs && chown -R spring:spring /app

USER spring

EXPOSE 8080

# Lets `docker stop` shut the JVM down gracefully instead of being killed.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
