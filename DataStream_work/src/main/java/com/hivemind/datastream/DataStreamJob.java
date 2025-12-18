package com.hivemind.datastream;

import com.hivemind.datastream.config.KafkaConfig;
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
                                .setStartingOffsets(OffsetsInitializer.earliest())
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

                // Add Source
                DataStream<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

                // Process Stream (extract fields, normalize)
                // This step acts as the "Standardizer" - ensuring all logs match the HiveMind
                // Schema
                DataStream<String> processedStream = stream.map(new EventProcessor());

                // Send ALL processed/normalized events to processed-events topic
                // Downstream services (AI, Anomaly, Mailing) will consume from here
                processedStream.sinkTo(processedSink);

                // Print to stdout for debugging
                processedStream.print("✅ NORMALIZED EVENT");

                // Execute program
                env.execute("HiveMind DataStream Normalization Service");
        }
}
