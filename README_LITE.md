# HiveMind Lite - Quick Start Guide 🚀

The "Lite" version of HiveMind is optimized for local development and integration testing with physical devices like ESP32. It uses a reduced service footprint while maintaining the core DataStream and Anomaly Detection features.

## 📋 Prerequisites
- **Docker & Docker Compose**
- **Java 21 / Maven** (for building backend/flink services)
- **Node.js** (optional, for the dashboard)

---

## 🛠️ Step 1: Build the Services
Before starting the containers, ensure the latest code changes are compiled:

```bash
# Build the Flink processing job
cd DataStream_work && mvn clean package -DskipTests && cd ..

# Build the Backend service
cd database-backend && mvn clean package -DskipTests && cd ..

# Build the Anomaly Detection service
cd anomaly-detection-service && mvn clean package -DskipTests && cd ..
```

---

## ⚡ Step 2: Launch the Lite Stack
Use the dedicated `lite` compose file to start the infrastructure:

```bash
docker-compose -f docker-compose.lite.yml build
docker-compose -f docker-compose.lite.yml up -d
```

### Core Services:
- **Kafka (`localhost:9094`)**: Message broker.
- **Cassandra (`localhost:9042`)**: Persistent storage.
- **Backend API (`localhost:8089`)**: UI data provider.
- **Kafka REST Proxy (`localhost:8083`)**: Gateway for ESP32/IoT devices.
- **Flink**: Real-time normalization engine.
- **Anomaly Service**: Trigger alerts for sensor thresholds.

---

## 🏢 Step 3: Start the DataStream Job
Flink requires a manual job submission after the JobManager is healthy:

```bash
docker-compose -f docker-compose.lite.yml up flink-job-submitter
```
*Verify success at the Flink UI: [http://localhost:8085](http://localhost:8085)*

---

## 🖥️ Step 4: Access the Dashboard
Open the premium dashboard in your browser:
`HiveMind-Web/index.html`

---

## 🔌 Step 5: Connecting Devices
### ESP32 (IoT Agent)
Update your `Test-arduino-esp.ino` with:
- **WiFi**: `SSID` and `Password`
- **Server**: `http://<YOUR_IP>:8083/topics/device-events-iot`

### Workstation Agent
Run the Java agent on your machine to monitor local file changes:
```bash
cd Agents/workstation_agent
mvn spring-boot:run -Dspring-boot.run.arguments="--watch.path=/path/to/folder"
```

---

## 🔍 Verification & Troubleshooting
- **Check Backend API**: `curl http://localhost:8089/api/health`
- **View Kafka Logs**: `docker logs -f kafka-lite`
- **Monitor Anomaly Alerts**: `docker logs -f anomaly-lite`

> [!TIP]
> If the UI shows "API: Offline", ensure `backend-lite` is running and port `8089` isn't blocked.
