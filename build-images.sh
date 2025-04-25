#!/bin/bash
set -e

echo "Building all Docker images..."

# Build each service image
echo "Building Auth Service..."
docker build -f docker/services/auth-service.Dockerfile -t taskify/auth-service:latest .

echo "Building IAM Service..."
docker build -f docker/services/iam-service.Dockerfile -t taskify/iam-service:latest .

echo "Building Discovery Service..."
docker build -f docker/services/discovery-service.Dockerfile -t taskify/discovery-service:latest .

echo "Building Config Server..."
docker build -f docker/services/config-server.Dockerfile -t taskify/config-server:latest .

echo "Building Organization Service..."
docker build -f docker/services/organization-service.Dockerfile -t taskify/organization-service:latest .

echo "Building Project Service..."
docker build -f docker/services/project-service.Dockerfile -t taskify/project-service:latest .

echo "Building API Gateway..."
docker build -f docker/services/api-gateway.Dockerfile -t taskify/api-gateway:latest .

echo "All images built successfully!"