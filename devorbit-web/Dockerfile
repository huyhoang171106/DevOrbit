FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
RUN apk add --no-cache gettext
COPY --from=build /app/dist /usr/share/nginx/html
ARG NGINX_PORT=80
ARG NGINX_API_PROXY_PASS=http://api:8080
ENV NGINX_PORT=$NGINX_PORT
ENV NGINX_API_PROXY_PASS=$NGINX_API_PROXY_PASS
COPY nginx.conf /etc/nginx/conf.d/default.conf.template
RUN envsubst < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf
EXPOSE $NGINX_PORT
CMD ["nginx", "-g", "daemon off;"]