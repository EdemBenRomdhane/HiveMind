package com.hivemind.datastream;

import com.hivemind.datastream.config.KafkaConfig;
import com.hivemind.datastream.filter.LogFilter;
import com.hivemind.datastream.processor.EventProcessor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;

public class DataStreamJob {

        public static void main(String[] args) throws Exception {
                // Set up the streaming execution environment
                final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

                System.out.println("🚀 Starting HiveMind DataStream Job...");
                System.out.println("📋 Log Filtering: Enabled (keeping suspicious, discarding harmless)");

                // Create Kafka Source
                KafkaSource<String> source = KafkaSource.<String>builder()
                                .setBootstrapServers(KafkaConfig.BOOTSTRAP_SERVERS)
                                .setTopics(KafkaConfig.ALL_TOPICS)
                                .setGroupId(KafkaConfig.CONSUMER_GROUP_ID)
                                .setStartingOffsets(OffsetsInitializer.latest())
                                .setValueOnlyDeserializer(new SimpleStringSchema())
                                .build();

                // Create Kafka Sink for ALL processed events
                KafkaSink<String> processedSink = KafkaSink.<String>builder()
                                .setBootstrapServers(KafkaConfig.BOOTSTRAP_SERVERS)
                                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                                                .setTopic(KafkaConfig.TOPIC_PROCESSED)
                                                .setValueSerializationSchema(new SimpleStringSchema())
                                                .build())
                                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                                .build();

                // Create Kafka Sink for FILTERED anomaly alerts only
                KafkaSink<String> anomalySink = KafkaSink.<String>builder()
                                .setBootstrapServers(KafkaConfig.BOOTSTRAP_SERVERS)
                                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                                                .setTopic(KafkaConfig.TOPIC_ANOMALY_ALERTS)
                                                .setValueSerializationSchema(new SimpleStringSchema())
                                                .build())
                                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                                .build();

                // Add Source
                DataStream<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

                // Process Stream (extract fields, normalize)
                DataStream<String> processedStream = stream.map(new EventProcessor());

                // Send ALL processed events to processed-events topic (for debugging/audit)
                processedStream.sinkTo(processedSink);

                // Filter Stream (keep only suspicious/dangerous logs)
                DataStream<String> filteredStream = processedStream.filter(new LogFilter());

                // Send FILTERED anomaly alerts to anomaly-alerts topic (for ELK/AI)
                filteredStream.sinkTo(anomalySink);

                // Print filtered alerts to stdout (for debugging)
                filteredStream.print("🚨 ANOMALY ALERT");

                // Execute program
                env.execute("HiveMind DataStream Processing with Log Filtering");
        }
}
