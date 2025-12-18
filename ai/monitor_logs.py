import json
import logging
import threading
import time
import requests
import os
from kafka import KafkaConsumer, KafkaProducer
from kafka.errors import KafkaError
from detect_anomaly import detect_anomaly, DEFAULT_MODEL

# Configure Logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("AI-Monitor")

# Configuration Kafka
KAFKA_BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
TOPIC_LOGS = ["processed-events"]
TOPIC_AI_ALERTS = "ai-alerts"
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", DEFAULT_MODEL)

# Global Producer
producer = None

def get_producer():
    global producer
    if producer is None:
        try:
            producer = KafkaProducer(
                bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
                value_serializer=lambda v: json.dumps(v).encode('utf-8')
            )
            logger.info("Kafka Producer initialized.")
        except KafkaError as e:
            logger.error(f"Failed to initialize Kafka Producer: {e}")
    return producer

def start_monitoring():
    """
    Starts the Kafka Consumer loop in a separate thread.
    """
    monitor_thread = threading.Thread(target=_consume_loop, daemon=True)
    monitor_thread.start()
    logger.info("Background Kafka Monitor started.")

def _consume_loop():
    logger.info(f"Connecting to Kafka at {KAFKA_BOOTSTRAP_SERVERS}...")
    
    consumer = None
    while not consumer:
        try:
            consumer = KafkaConsumer(
                *TOPIC_LOGS,
                bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
                group_id="ai-threat-detection-group",
                auto_offset_reset='latest',
                value_deserializer=lambda x: x.decode('utf-8')
            )
            logger.info("Connected to Kafka!")
        except KafkaError as e:
            logger.warning(f"Kafka connection failed ({e}). Retrying in 5s...")
            time.sleep(5)

    for message in consumer:
        try:
            log_content = message.value
            logger.info(f"Received log: {log_content[:50]}...")
            
            # 1. Analyze with AI
            analysis = detect_anomaly(log_content, OLLAMA_MODEL)
            
            # 2. If Anomaly, Report via Kafka
            if analysis.get("anomaly", False):
                logger.warning(f"🚨 ANOMALY DETECTED: {analysis.get('reason')}")
                _report_threat(log_content, analysis)
            else:
                logger.info("Log analysis: Clean.")
                
        except Exception as e:
            logger.error(f"Error processing message: {e}")

def _report_threat(log_content, analysis):
    """
    Sends the detected threat to the AI alerts Kafka topic.
    """
    threat_event = {
        "alertId": str(time.time()),
        "deviceId": "AI_SERVICE", 
        "description": analysis.get("reason", "Unknown threat"),
        "severity": "HIGH" if analysis.get("confidence", 0) > 0.8 else "MEDIUM",
        "confidence": analysis.get("confidence", 0.0),
        "timestamp": datetime.now().isoformat()
    }
    
    p = get_producer()
    if p:
        try:
            p.send(TOPIC_AI_ALERTS, threat_event)
            p.flush()
            logger.info(f"Threat reported to Kafka topic '{TOPIC_AI_ALERTS}' successfully.")
        except Exception as e:
            logger.error(f"Failed to send threat to Kafka: {e}")
    else:
        logger.error("Kafka Producer not available, threat dropped.")

if __name__ == "__main__":
    start_monitoring()
    # Keep main thread alive for testing
    while True:
        time.sleep(1)