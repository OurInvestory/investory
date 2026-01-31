# ===========================================
# Backend Dockerfile (Optimized)
# ===========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install required tools
RUN apk add --no-cache bash

# Copy gradle wrapper and build files first (better caching)
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Make gradlew executable and download dependencies
RUN chmod +x ./gradlew \
    && ./gradlew dependencies --no-daemon --parallel

# Copy source code
COPY src ./src

# Build with optimizations
RUN ./gradlew build -x test --no-daemon --parallel \
    && mkdir -p build/extracted \
    && java -Djarmode=layertools -jar build/libs/*.jar extract --destination build/extracted

# ===========================================
# Production Stage (Optimized)
# ===========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl \
    && addgroup -S investory && adduser -S investory -G investory

# Copy layers for better caching (Spring Boot Layered Jar)
COPY --from=builder /app/build/extracted/dependencies/ ./
COPY --from=builder /app/build/extracted/spring-boot-loader/ ./
COPY --from=builder /app/build/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/build/extracted/application/ ./

# Change ownership
RUN chown -R investory:investory /app

USER investory

EXPOSE 8080

# JVM Optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} org.springframework.boot.loader.launch.JarLauncher"]
