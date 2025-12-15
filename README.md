<<<<<<< HEAD
# HiveMind - Automated Security for the Ecosystem

> *"The caravan moves on, and the dogs bark"*

Système de sécurité automatisé et intelligent capable de surveiller et de protéger l'ensemble d'un écosystème réseau.

---

## 📋 Vue d'ensemble

HiveMind combine la surveillance en temps réel, la détection d'anomalies basée sur l'IA et des réponses automatisées pour garantir une sécurité continue de votre infrastructure.

**Couverture**: Ordinateurs, Serveurs, Routeurs, Commutateurs, Objets connectés (IoT)

---

## 🏗️ Architecture

```
Devices → DataStream (Kafka/Flink) → Backend (Spring Boot) → Database (Cassandra/PostgreSQL)
                ↓                              ↓
            ELK Stack                      AI (Ollama)
                ↓                              ↓
            Dashboard (React.js) ← WebSocket ← Alerts
```

---

## 🚀 Modules

### 📊 [DataStream](./DataStream-work) - Traitement en temps réel
**Responsable**: Adem Ben Romdhane

Collecte et traitement des événements en temps réel avec Apache Kafka et Apache Flink.

**API REST**: `POST http://localhost:8080/api/events`

**Topics Kafka**:
- `device-events-workstation`
- `device-events-server`
- `device-events-iot`
- `device-events-network`

[📖 Documentation complète](./DataStream-work/README.md)

---

### 🔐 Backend - Services & API
**Responsable**: Jasser Lefi

Services Spring Boot, API REST, intégration des bases de données et sécurité.

---

### 🔍 Security & ELK - Analyse des logs
**Responsable**: Malek Boujazza

Mise en place de la suite ELK, analyse des logs et détection des menaces.

---

### 🤖 AI - Détection d'anomalies
**Responsable**: Eya Skhiri

Intégration d'Ollama pour l'analyse sémantique et la détection d'anomalies.

---

### 🎨 DevOps & Frontend
**Responsable**: Ahmed Rayen Thabet

Automatisation, déploiement, CI/CD et développement du tableau de bord React.

---

## 🛠️ Technologies

- **Data Streaming**: Apache Kafka, Apache Flink, MQTT
- **Backend**: Spring Boot, Spring Security
- **Databases**: Cassandra, PostgreSQL
- **Monitoring**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **AI**: Ollama
- **DevOps**: Docker, Kubernetes, Ansible
- **Frontend**: React.js

---

## 🚦 Quick Start

```bash
# 1. Cloner le projet
git clone https://github.com/iluvumua/HiveMind.git
cd HiveMind

# 2. Démarrer le module DataStream
cd DataStream-work
docker-compose up -d

# 3. Créer les topics Kafka
for topic in device-events-workstation device-events-iot device-events-network device-events-server; do
  docker exec kafka kafka-topics --create --bootstrap-server kafka:29092 --topic $topic --partitions 1 --replication-factor 1 --if-not-exists
done

# 4. Build et démarrer l'API
mvn clean package -DskipTests
mvn spring-boot:run

# 5. Tester l'API
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"eventType":"LOGIN_FAILURE","deviceId":"WS-001","severity":"CRITICAL","username":"alice","authenticationStatus":"FAILURE"}'
```

---

## 📡 API Endpoints

### DataStream API

**Base URL**: `http://localhost:8080`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/events` | Soumettre un événement de sécurité |
| GET | `/api/health` | Vérifier l'état de l'API |

**Exemple de payload**:
```json
{
  "eventType": "LOGIN_FAILURE",
  "deviceId": "WS-001",
  "severity": "CRITICAL",
  "username": "alice",
  "authenticationStatus": "FAILURE"
}
```

---

## 👥 Équipe

| Rôle | Nom |
|------|-----|
| DevOps & Frontend | Ahmed Rayen Thabet |
| Data Stream Engineer | Adem Ben Romdhane |
| Security Engineer | Malek Boujazza |
| AI Engineer | Eya Skhiri |
| Backend Developer | Jasser Lefi |

---

## 📚 Documentation

- [DataStream Module](./DataStream-work/README.md) - API Kafka/Flink
- [Backend API](#) - Services Spring Boot *(à venir)*
- [ELK Configuration](#) - Monitoring et logs *(à venir)*
- [AI Integration](#) - Ollama setup *(à venir)*
- [Frontend Dashboard](#) - React.js *(à venir)*

---

## 📝 License

Projet académique - ENISO (École Nationale d'Ingénieurs de Sousse)

---

**Status**: 🟢 En développement actif
=======
# Security System Backend

Spring Boot backend for the automated security system.

## Requirements
- Java 17+
- PostgreSQL
- Apache Kafka

## Configuration
Update `src/main/resources/application.yml` with your database and Kafka credentials.

## Running
```bash
./mvnw spring-boot:run
```

## API Endpoints
- `GET /api/health`: Check system status.
- `GET /api/users`: List users.
- `POST /api/users`: Create user.
- `GET /api/devices`: List devices.
- `POST /api/devices`: Register device.
>>>>>>> 778bd7e (this is my work)
