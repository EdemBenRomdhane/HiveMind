#!/bin/bash

# Build all Docker images for K3s deployment

echo "🔨 Building HiveMind Docker Images for K3s..."

# Build Anomaly Detection Service
echo "📦 Building Anomaly Detection Service..."
docker build -t hivemind-anomaly-detection-service:latest ./anomaly-detection-service

# Build Backend Service
echo "📦 Building Backend Service..."
docker build -t hivemind-backend-service:latest ./database-backend

# Build AI Service
echo "📦 Building AI Service..."
docker build -t hivemind-ai-service:latest ./ai

# Build Mailing Service
echo "📦 Building Mailing Service..."
docker build -t hivemind-mailing-service:latest ./mailing-service

# Build Flink Job JAR
echo "📦 Building Flink Job..."
cd DataStream_work
mvn clean package -DskipTests
cd ..

echo "✅ All images built successfully!"
echo ""
echo "Next steps:"
echo "1. Import images to K3s: k3s ctr images import <image>.tar"
echo "2. Or use local registry"
echo "3. Deploy: ./deploy-k3s.sh"
