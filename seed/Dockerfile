# Seed Dockerfile (build context: ./seed)
FROM node:20-alpine

# Optional but helpful: curl for simple readiness checks
RUN apk add --no-cache curl

WORKDIR /app

# Install deps first for better caching
COPY package*.json ./
RUN npm ci --omit=dev || npm install --omit=dev

# Copy seed sources
COPY . .

# Default API target (override in compose)
ENV API=http://localhost:8080
ENV HEALTH_URL=${API}/actuator/health

# Wait for backend to be reachable (2xx/3xx/4xx) then run seeder
CMD sh -c '\
  echo "Waiting for $HEALTH_URL ..."; \
  for i in $(seq 1 60); do \
    code=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_URL" || true); \
    case "$code" in 2*|3*|4*) echo "Backend reachable ($code). Seeding..."; break ;; esac; \
    sleep 1; \
  done; \
  node seed_api.js'
