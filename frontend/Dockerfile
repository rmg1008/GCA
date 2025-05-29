# Etapa 1: build Angular con Node 22.14.0
FROM node:22.14.0-alpine AS builder
WORKDIR /app

COPY . .
RUN npm install && npm run build

# Etapa 2: servir con Nginx
FROM nginx:stable-alpine
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
