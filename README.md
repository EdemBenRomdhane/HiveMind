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

### 📊 [DataStream](./DataStream_work) - Traitement en temps réel
**Responsable**: Adem Ben Romdhane

Collecte et traitement des événements en temps réel avec Apache Kafka et Apache Flink.

**API REST**: `POST http://localhost:8080/api/events`

**Topics Kafka**:
- `device-events-workstation`
- `device-events-server`
- `device-events-iot`
- `device-events-network`

[📖 Documentation complète](./DataStream_work/README.md)

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

# 2. Démarrer l'environnement Global
docker-compose up -d

# 2a. (Alternative) Démarrer uniquement le module DataStream
# cd DataStream_work
# docker-compose up -d

# 3. Créer les topics Kafka
for topic in device-events-workstation device-events-iot device-events-network device-events-server processed-events; do
  docker exec kafka kafka-topics --create --bootstrap-server kafka:29092 --topic $topic --partitions 1 --replication-factor 1 --if-not-exists
done

# 4. Démarrer le Workstation Agent (Source de données)
# Dans un nouveau terminal
cd Agents/workstation_agent
mvn spring-boot:run

# 5. Vérifier les données
# Les événements sont envoyés directement à Kafka (topic: device-events-workstation)
# Le Flink Job les traite et les envoie vers 'processed-events'
```

---

## 📡 Interfaces & Topics

### Kafka Topics (Principal Point d'Entrée)

Les agents envoient les données directement à Kafka sur le port **9094**.

| Topic | Description |
|-------|-------------|
| `device-events-workstation` | Événements bruts des postes de travail |
| `device-events-server` | Événements bruts des serveurs |
| `processed-events` | Événements enrichis par Flink (avec `filename`, `changeType`) |

### Legacy REST API (Obsolète)
*L'ancienne API REST (`POST /api/events`) est conservée pour compatibilité mais l'ingestion directe Kafka est recommandée.*


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

- [DataStream Module](./DataStream_work/README.md) - API Kafka/Flink
- [Backend API](#) - Services Spring Boot *(à venir)*
- [ELK Configuration](#) - Monitoring et logs *(à venir)*
- [AI Integration](#) - Ollama setup *(à venir)*
- [Frontend Dashboard](#) - React.js *(à venir)*

---

## 📝 License

Projet académique - ENISO (École Nationale d'Ingénieurs de Sousse)

---

**Status**: 🟢 En développement actif
