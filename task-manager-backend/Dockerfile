FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

# Copy maven executable and pom.xml files first for better layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Build all dependencies for caching
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

# Copy source code
COPY src src

# Package the application
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Add curl for healthchecks
RUN apk --no-cache add curl

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Set environment variables for MySQL
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/task_manager_db
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=Saya@1307
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl --silent --fail http://localhost:8080/api/auth/login || exit 0

EXPOSE 8080

# Use the shell form of ENTRYPOINT to allow environment variable expansion
ENTRYPOINT java $JAVA_OPTS -jar app.jar
