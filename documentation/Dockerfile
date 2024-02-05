# Stage 1: Build the Docusaurus site
FROM node:18 as build

# Set the working directory
WORKDIR /app

# Copy the project files to the working directory
COPY . .

# Install dependencies
RUN npm install

# Build the Docusaurus website
RUN npm run build

# Stage 2: Serve the site using Nginx
FROM nginx:alpine

# Remove default Nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy built assets from the build stage to the Nginx server
COPY --from=build /app/build /usr/share/nginx/html

# Expose port 80 to the outside once the container has launched
EXPOSE 80

# Start Nginx and keep it running in the foreground
CMD ["nginx", "-g", "daemon off;"]
