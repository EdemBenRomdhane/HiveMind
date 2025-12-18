#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <time.h>

// WiFi credentials
const char* ssid = "Alpinia";
const char* password = "Alpinia@2025";

// Backend endpoint
// Kafka REST Proxy endpoint
// Note: Kafka REST Proxy (port 8083) is the standard bridge to Kafka
const char* serverUrl = "http://192.168.100.141:8083/topics/device-events-iot";

// Device ID
const char* deviceId = "esp32-sensor-01";

void setup() {
  Serial.begin(115200);

  // Connect to WiFi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");

  // Init time
  configTime(0, 0, "pool.ntp.org", "time.nist.gov");
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;

    // Create JSON payload in Kafka REST Proxy format
    // Format: {"records": [{"value": {...}}]}
    StaticJsonDocument<500> doc;
    JsonArray records = doc.createNestedArray("records");
    JsonObject record = records.createNestedObject();
    JsonObject value = record.createNestedObject("value");
    
    value["deviceId"] = deviceId;
    value["temperature"] = random(200, 300) / 10.0;
    value["status"] = "ONLINE";
    
    time_t now;
    time(&now);
    char timeString[30];
    strftime(timeString, sizeof(timeString), "%Y-%m-%dT%H:%M:%SZ", gmtime(&now));
    value["timestamp"] = timeString;

    String requestBody;
    serializeJson(doc, requestBody);

    // Send POST request to Kafka REST Proxy
    http.begin(serverUrl);
    http.addHeader("Content-Type", "application/vnd.kafka.json.v2+json");
    
    int httpResponseCode = http.POST(requestBody);

    if (httpResponseCode > 0) {
      String response = http.getString();
      Serial.print("Kafka Sync Response: ");
      Serial.println(httpResponseCode);
      Serial.println(response);
    } else {
      Serial.print("Error on sending to Kafka: ");
      Serial.println(httpResponseCode);
    }

    http.end();
  } else {
    Serial.println("WiFi Disconnected");
  }

  delay(5000);
}