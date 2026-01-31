# ===========================================
# Frontend Dockerfile (Optimized)
# ===========================================

# Dependencies stage - cached separately
FROM node:20-alpine AS deps

WORKDIR /app

# Copy only package files for better caching
COPY package.json package-lock.json* ./

# Install dependencies with clean cache
RUN npm ci --prefer-offline --no-audit && npm cache clean --force

# ===========================================
# Build stage
# ===========================================
FROM node:20-alpine AS builder

WORKDIR /app

# Copy dependencies from deps stage
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# Set production environment and build
ENV NODE_ENV=production
RUN npm run build

# ===========================================
# Production Stage (Nginx)
# ===========================================
FROM nginx:stable-alpine-slim

# Install curl for healthcheck
RUN apk add --no-cache curl

# Remove default nginx config
RUN rm -rf /etc/nginx/conf.d/default.conf /usr/share/nginx/html/*

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Set proper permissions
RUN chown -R nginx:nginx /usr/share/nginx/html \
    && chmod -R 755 /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:80/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
