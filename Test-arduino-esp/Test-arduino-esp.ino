#include <WiFi.h>
#include <HTTPClient.h>

const char* ssid = "realme C25Y";
const char* password = "mlk123456";
const char* serverUrl = "http://192.168.100.66:8080/iot/ingest-log"; // IP du backend

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nConnected to WiFi");
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(serverUrl);
    http.addHeader("Content-Type", "application/json");

    float temperature = random(200, 900) / 10.0; // 20.0 → 90.0
    String status = temperature > 80 ? "OFFLINE" : "ONLINE";

    // Fixed copy-paste artifacts (replaced 😕 with :)
    String json = "{"
      "\"deviceId\":\"esp32-01\","
      "\"temperature\":" + String(temperature) + ","
      "\"status\":\"" + status + "\""
    "}";

    int httpResponseCode = http.POST(json);

    Serial.print("HTTP Response: ");
    Serial.println(httpResponseCode);

    http.end();
  }

  delay(5000); // envoi toutes les 5 secondes
}
