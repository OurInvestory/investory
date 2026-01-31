# ===========================================
# Investory Monorepo Dockerfile (Optimized)
# Multi-stage build for both backend and frontend
# ===========================================

# ===========================================
# Backend Build Stage
# ===========================================
FROM eclipse-temurin:21-jdk-alpine AS backend-builder

WORKDIR /app/backend

RUN apk add --no-cache bash

# Copy gradle files first for caching
COPY backend/gradlew backend/gradlew
COPY backend/gradle backend/gradle
COPY backend/build.gradle backend/settings.gradle ./

# Download dependencies
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon --parallel

# Copy source code and build
COPY backend/src ./src
RUN ./gradlew build -x test --no-daemon --parallel \
    && mkdir -p build/extracted \
    && java -Djarmode=layertools -jar build/libs/*.jar extract --destination build/extracted

# ===========================================
# Frontend Build Stage
# ===========================================
FROM node:20-alpine AS frontend-deps

WORKDIR /app/frontend

COPY frontend/package.json frontend/package-lock.json* ./
RUN npm ci --prefer-offline --no-audit && npm cache clean --force

FROM node:20-alpine AS frontend-builder

WORKDIR /app/frontend

COPY --from=frontend-deps /app/frontend/node_modules ./node_modules
COPY frontend/ ./

ENV NODE_ENV=production
RUN npm run build

# ===========================================
# Production Stage - Backend
# ===========================================
FROM eclipse-temurin:21-jre-alpine AS backend

WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S investory && adduser -S investory -G investory

# Copy layers for better caching
COPY --from=backend-builder /app/backend/build/extracted/dependencies/ ./
COPY --from=backend-builder /app/backend/build/extracted/spring-boot-loader/ ./
COPY --from=backend-builder /app/backend/build/extracted/snapshot-dependencies/ ./
COPY --from=backend-builder /app/backend/build/extracted/application/ ./

RUN chown -R investory:investory /app

USER investory

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} org.springframework.boot.loader.launch.JarLauncher"]

# ===========================================
# Production Stage - Frontend (Nginx)
# ===========================================
FROM nginx:stable-alpine-slim AS frontend

RUN apk add --no-cache curl \
    && rm -rf /etc/nginx/conf.d/default.conf /usr/share/nginx/html/*

COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=frontend-builder /app/frontend/dist /usr/share/nginx/html

RUN chown -R nginx:nginx /usr/share/nginx/html \
    && chmod -R 755 /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:80/health || exit 1

CMD ["nginx", "-g", "daemon off;"]