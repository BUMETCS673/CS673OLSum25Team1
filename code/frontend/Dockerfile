FROM node:18-alpine AS build-stage

WORKDIR /app

# Copy package files
COPY package*.json ./
RUN npm ci

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Output stage - this stage is just for copying the build output
FROM alpine:latest

# Create a directory for the build output
WORKDIR /build-output

# Copy only the built files from the builder stage
COPY --from=build-stage /app/dist ./dist

# This image doesn't need to run anything
# It's just a way to pass the build output to other images
# ---- Stage 2: Serve application with Nginx ----
FROM nginx:stable-alpine

# Copy custom Nginx configuration
# Assuming your nginx.conf is in the root of the frontend directory (where this Dockerfile is)
# and you want to replace the default Nginx config.
# If your nginx.conf is intended for /etc/nginx/conf.d/, use that path.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Remove default Nginx welcome page (optional, but good practice)
RUN rm -rf /usr/share/nginx/html/*

# Copy built assets from the build stage
# Adjust the source path if your build output is in a directory other than 'build' (e.g., 'dist')
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Expose port 80 for Nginx
EXPOSE 80

# Start Nginx when the container launches
CMD ["nginx", "-g", "daemon off;"]