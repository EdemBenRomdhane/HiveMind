import json
import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import threading
import time
from kafka import KafkaConsumer
from kafka.errors import KafkaError

# Configuration
KAFKA_BROKER = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")
TOPIC_ALERTS = os.getenv("TOPIC_ALERTS", "anomaly-alerts")
SMTP_HOST = os.getenv("SMTP_HOST", "mailhog")
SMTP_PORT = int(os.getenv("SMTP_PORT", 1025))
FROM_EMAIL = os.getenv("FROM_EMAIL", "hivemind-alert@local.com")
TO_EMAIL = os.getenv("TO_EMAIL", "admin@hivemind.com")

def send_alert_email(alert):
    """
    Sends an email using the configured SMTP server.
    """
    try:
        severity = alert.get("severity", "UNKNOWN")
        
        # Determine strict urgency
        if severity != "HIGH":
            print(f"Skipping email for severity: {severity}")
            return

        subject = f"🚨 [HiveMind] HIGH SEVERITY ANOMALY DETECTED"
        
        body = f"""
        <html>
          <body>
            <h2>⚠️ High Severity Thread Detected</h2>
            <p><strong>Device:</strong> {alert.get('deviceId', 'Unknown')}</p>
            <p><strong>Event Type:</strong> {alert.get('eventType', 'Unknown')}</p>
            <p><strong>Description:</strong> {alert.get('description', 'No description available')}</p>
            <p><strong>Timestamp:</strong> {alert.get('timestamp', 'N/A')}</p>
            <hr>
            <p><i>HiveMind Security System</i></p>
          </body>
        </html>
        """

        msg = MIMEMultipart()
        msg['From'] = FROM_EMAIL
        msg['To'] = TO_EMAIL
        msg['Subject'] = subject
        msg.attach(MIMEText(body, 'html'))

        with smtplib.SMTP(SMTP_HOST, SMTP_PORT) as server:
            server.send_message(msg)
            
        print(f"✅ Alert email sent to {TO_EMAIL}")

    except Exception as e:
        print(f"❌ Failed to send email: {e}")

def start_consumer():
    print(f"Connecting to Kafka at {KAFKA_BROKER}...")
    consumer = None
    while not consumer:
        try:
            consumer = KafkaConsumer(
                "ai-alerts", "anomaly-alerts",
                bootstrap_servers=KAFKA_BROKER,
                group_id="mailing-service-group",
                auto_offset_reset='latest',
                value_deserializer=lambda x: json.loads(x.decode('utf-8'))
            )
            print("✅ Connected to Kafka for Mailing Service (AI + Anomaly)!")
        except KafkaError as e:
            print(f"⚠️ Kafka connection failed ({e}). Retrying in 5s...")
            time.sleep(5)

    for message in consumer:
        try:
            alert = message.value
            print(f"📥 Received alert from topic [{message.topic}]: {alert}")
            send_alert_email(alert)
        except Exception as e:
            print(f"❌ Error processing message: {e}")

if __name__ == "__main__":
    # Also simple health check loop or just blocking consumer
    print("Mailing Service Starting...")
    start_consumer()
