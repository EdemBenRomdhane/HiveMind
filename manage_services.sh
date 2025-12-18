#!/bin/bash

# HiveMind Pairwise Service Manager
# Helps run the system in pairs to save resources

COMPOSE_FILE="docker-compose.yml"

show_help() {
    echo "Usage: ./manage_services.sh [command]"
    echo ""
    echo "Commands:"
    echo "  ingestion   - Run Kafka + DataStream (Flink)"
    echo "  detection   - Run Kafka + Anomaly Detection + Mailing + Mailhog"
    echo "  backend     - Run Kafka + Database Backend + Cassandra"
    echo "  bridge      - Run Kafka + REST Proxy (for ESP32/IoT)"
    echo "  status      - Show running microservices"
    echo "  stop        - Stop all services"
}

case "$1" in
    bridge)
        echo "🚀 Starting IoT Bridge: Kafka + Kafka REST Proxy..."
        docker-compose -f $COMPOSE_FILE up -d kafka kafka-rest
        ;;
    ingestion)
        echo "🚀 Starting Ingestion Pair: Kafka + DataStream..."
        docker-compose -f $COMPOSE_FILE up -d kafka jobmanager taskmanager iot-device-service
        ;;
    security)
        echo "🚀 Starting Security Pair: Kafka + AI + Anomaly + Mailing..."
        docker-compose -f $COMPOSE_FILE up -d kafka ai-service anomaly-detection-service mailing-service mailhog
        ;;
    storage)
        echo "🚀 Starting Storage Pair: Kafka + Backend + Cassandra..."
        docker-compose -f $COMPOSE_FILE up -d kafka backend-service cassandra
        ;;
    infra)
        echo "🚀 Starting Infrastructure only..."
        docker-compose -f $COMPOSE_FILE up -d kafka
        ;;
    status)
        docker-compose -f $COMPOSE_FILE ps
        ;;
    stop)
        echo "🛑 Stopping and cleaning up all HiveMind services..."
        docker-compose -f $COMPOSE_FILE down --remove-orphans
        ;;
    *)
        show_help
        ;;
esac
