package com.hivemind.datastream.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemind.datastream.config.KafkaConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class HttpEventCollector {

    private static final int PORT = 8080;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static KafkaProducer<String, String> kafkaProducer;

    public static void main(String[] args) throws IOException {
        // 1. Setup Kafka Producer
        Properties props = new Properties();
        props.put("bootstrap.servers", KafkaConfig.BOOTSTRAP_SERVERS);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer<>(props);

        // 2. Start HTTP Server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/events", new EventHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("🚀 HTTP Event Collector started on port " + PORT);
        System.out.println("👉 Send POST requests to: http://localhost:" + PORT + "/api/events");
    }

    static class EventHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                try {
                    // Read Request Body
                    InputStream is = t.getRequestBody();
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                    // Validate JSON
                    JsonNode json = objectMapper.readTree(body);
                    System.out.println("📩 Received HTTP Event: " + body);

                    // Determine Topic based on device type (simple logic for now)
                    String topic = KafkaConfig.TOPIC_SERVER; // Default to server
                    if (body.contains("IOT"))
                        topic = KafkaConfig.TOPIC_IOT;
                    else if (body.contains("WS"))
                        topic = KafkaConfig.TOPIC_WORKSTATION;
                    else if (body.contains("NET"))
                        topic = KafkaConfig.TOPIC_NETWORK;

                    // Forward to Kafka
                    ProducerRecord<String, String> record = new ProducerRecord<>(topic, body);
                    kafkaProducer.send(record, (metadata, exception) -> {
                        if (exception != null) {
                            System.err.println("❌ Error sending to Kafka: " + exception.getMessage());
                        } else {
                            System.out.println("➡️ Forwarded to Kafka topic: " + metadata.topic());
                        }
                    });

                    // Send Response
                    String response = "Event received and forwarded to " + topic;
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                } catch (Exception e) {
                    String response = "Invalid JSON or Internal Error: " + e.getMessage();
                    t.sendResponseHeaders(400, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } else {
                String response = "Only POST method is allowed";
                t.sendResponseHeaders(405, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
