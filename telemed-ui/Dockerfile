# telemed-ui/Dockerfile
FROM node:20-alpine

WORKDIR /app

# Install deps
COPY package*.json ./
RUN npm ci

# Build
COPY . .
RUN npm run build

# Serve the built site with Vite preview on 0.0.0.0:5173
ENV HOST=0.0.0.0
ENV PORT=5173
EXPOSE 5173
CMD ["npm", "run", "preview", "--", "--host", "0.0.0.0", "--port", "5173"]
