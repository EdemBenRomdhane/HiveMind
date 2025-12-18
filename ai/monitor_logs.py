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
TOPIC_LOGS = ["processed-events"]  # Consume normalized events from Flink
TOPIC_ALERTS = "anomaly-alerts"  # Produce alerts to same topic as Anomaly Service
BACKEND_URL = os.getenv("BACKEND_URL", "http://backend-service:8080/api/events")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", DEFAULT_MODEL)

def start_monitoring():
    """
    Starts the Kafka Consumer loop in a separate thread.
    """
    monitor_thread = threading.Thread(target=_consume_loop, daemon=True)
    monitor_thread.start()
    logger.info("Background Kafka Monitor started.")

def _consume_loop():
    logger.info(f"Connecting to Kafka at {KAFKA_BROKER}...")
    
    consumer = None
    while not consumer:
        try:
            consumer = KafkaConsumer(
                *TOPIC_LOGS,
                bootstrap_servers=KAFKA_BROKER,
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
            analysis = detect_anomaly(log_content, MODEL)
            
            # 2. If Anomaly, Report to Backend
            if analysis.get("anomaly", False):
                logger.warning(f"🚨 ANOMALY DETECTED: {analysis.get('reason')}")
                _report_threat(log_content, analysis)
            else:
                logger.info("Log analysis: Clean.")
                
        except Exception as e:
            logger.error(f"Error processing message: {e}")

def _report_threat(log_content, analysis):
    """
    Sends the detected threat to the Backend Service.
    """
    threat_event = {
        "eventType": "AI_THREAT_DETECTED",
        "deviceId": "AI_SERVICE", # Or extract from log
        "severity": "HIGH" if analysis.get("confidence", 0) > 0.8 else "MEDIUM",
        # "description": analysis.get("reason", "Unknown threat"), # Backend might not accept extra fields yet
    }
    
    try:
        response = requests.post(BACKEND_URL, json=threat_event)
        if response.status_code in [200, 201]:
            logger.info("Threat reported to Backend successfully.")
        else:
            logger.error(f"Failed to report threat. Status: {response.status_code}")
    except Exception as e:
        logger.error(f"Backend connection failed: {e}")

if __name__ == "__main__":
    start_monitoring()
    # Keep main thread alive for testing
    while True:
        time.sleep(1)