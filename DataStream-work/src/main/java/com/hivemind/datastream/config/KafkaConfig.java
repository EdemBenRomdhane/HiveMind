package com.hivemind.datastream.config;

public class KafkaConfig {
    public static final String BOOTSTRAP_SERVERS = System.getenv("KAFKA_BOOTSTRAP_SERVERS") != null
            ? System.getenv("KAFKA_BOOTSTRAP_SERVERS")
            : "localhost:9094";
    public static final String CONSUMER_GROUP_ID = "hivemind-flink-group";

    // Topics
    public static final String TOPIC_WORKSTATION = "device-events-workstation";
    public static final String TOPIC_IOT = "device-events-iot";
    public static final String TOPIC_NETWORK = "device-events-network";
    public static final String TOPIC_SERVER = "device-events-server";

    // All topics list for easier subscription
    public static final String[] ALL_TOPICS = {
            TOPIC_WORKSTATION,
            TOPIC_IOT,
            TOPIC_NETWORK,
            TOPIC_SERVER
    };
}
