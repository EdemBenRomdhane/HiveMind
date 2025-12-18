import json
import time
from kafka import KafkaProducer

KAFKA_BOOTSTRAP_SERVERS = "localhost:9094"

def produce_test_events():
    producer = KafkaProducer(
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )
    
    # 1. IoT Anomaly (Temperature)
    iot_event = {
        "deviceId": "IOT-SENSOR-01",
        "temperature": 125.0,
        "deviceType": "IOT",
        "timestamp": "2025-12-18T16:00:00"
    }
    print(f"Sending IoT Anomaly: {iot_event}")
    producer.send("processed-events", iot_event)
    
    # 2. AI Threat (Suspicious Log)
    ai_event = "ALERT: Multiple failed login attempts from IP 192.168.1.50 for user 'admin'"
    print(f"Sending AI Threat Log: {ai_event}")
    # Note: AI service expects a string or a JSON? 
    # Let's check ai/monitor_logs.py value_deserializer
    # value_deserializer=lambda x: x.decode('utf-8') -> it expects STRING
    producer.send("processed-events", value=ai_event.encode('utf-8'))
    
    producer.flush()
    print("Events sent!")

if __name__ == "__main__":
    produce_test_events()
