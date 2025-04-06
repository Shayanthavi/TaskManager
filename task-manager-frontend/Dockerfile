# Build stage
FROM node:20-alpine AS build

WORKDIR /app

# Copy package.json and package-lock.json for better layer caching
COPY package*.json ./

# Install dependencies with cache mounted to speed up builds
RUN --mount=type=cache,target=/root/.npm \
    npm ci --prefer-offline --no-audit

# Copy source code
COPY . .

# Build the application with production optimization
RUN npm run build -- --configuration=production

# Production stage - use smaller nginx image
FROM nginx:stable-alpine

# Add curl for healthchecks
RUN apk --no-cache add curl

# Copy custom nginx config
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

# Copy build artifacts from build stage
COPY --from=build /app/dist/task-manager-frontend/browser /usr/share/nginx/html

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:80/ || exit 1

# Expose port 80
EXPOSE 80

# Runtime command
CMD ["nginx", "-g", "daemon off;"]
