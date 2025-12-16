#!/bin/bash

# Deploy Script for HiveMind on K3s

echo "🚀 Starting HiveMind K3s Deployment..."

# 1. Create Namespace
echo "📦 Creating Namespace..."
kubectl apply -f k8s/00-namespaces.yaml

# 2. Deploy Infrastructure
echo "building infrastructure..."
kubectl apply -f k8s/01-infrastructure/

# 3. Wait for Kafka (simple wait)
echo "⏳ Waiting for Infrastructure (30s)..."
sleep 30

# 4. Deploy Services
echo "🚀 Deploying Microservices..."
kubectl apply -f k8s/02-services/

# 5. Deploy Flink
echo "🌊 Deploying Flink..."
kubectl apply -f k8s/03-flink/

echo "✅ Deployment commands submitted. Check status with: kubectl get pods -n hivemind"
echo "   - Flink UI: http://localhost:30085"
echo "   - Backend: http://localhost:30089/api/health"
echo "   - IoT: http://localhost:30080"
