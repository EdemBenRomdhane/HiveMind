#!/bin/bash
# Simple test to verify Kafka connection from agent

echo "🧪 Testing Kafka Connection..."
echo ""

# Test 1: Check if Kafka is accessible
echo "1️⃣ Testing Kafka accessibility on localhost:9094..."
timeout 2 bash -c "</dev/tcp/localhost/9094" && echo "✅ Kafka port 9094 is open" || echo "❌ Cannot connect to Kafka on port 9094"
echo ""

# Test 2: Send a test message using kafka-console-producer
echo "2️⃣ Sending test message to Kafka..."
echo '{"eventType":"TEST","deviceId":"TEST-001","severity":"LOW","message":"Connection test"}' | \
docker exec -i kafka kafka-console-producer --bootstrap-server kafka:29092 --topic device-events-workstation
echo "✅ Test message sent"
echo ""

# Test 3: Read messages from topic
echo "3️⃣ Reading messages from topic (should see test message)..."
docker exec kafka kafka-console-consumer --bootstrap-server kafka:29092 --topic device-events-workstation --from-beginning --timeout-ms 3000 2>&1 | grep -v "ERROR\|Processed"
echo ""

# Test 4: Check agent configuration
echo "4️⃣ Agent Kafka configuration:"
grep "spring.kafka.bootstrap-servers" Agents/workstation_agent/src/main/resources/application.properties
echo ""

echo "✅ Test complete!"
