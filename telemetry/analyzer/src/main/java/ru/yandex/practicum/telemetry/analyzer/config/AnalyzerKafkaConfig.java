package ru.yandex.practicum.telemetry.analyzer.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.serialization.HubEventDeserializer;
import ru.yandex.practicum.telemetry.analyzer.serialization.SnapshotDeserializer;

import java.util.Properties;

@Configuration
public class AnalyzerKafkaConfig {

    @Bean
    public HubEventDeserializer hubEventDeserializer() {
        return new HubEventDeserializer();
    }

    @Bean
    public SnapshotDeserializer snapshotDeserializer() {
        return new SnapshotDeserializer();
    }

    @Bean
    public Consumer<String, HubEventAvro> hubEventsConsumer(HubEventDeserializer hubEventDeserializer) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-hubevents-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "50");

        return new KafkaConsumer<>(props, new StringDeserializer(), hubEventDeserializer);
    }

    @Bean
    public Consumer<String, SensorsSnapshotAvro> snapshotsConsumer(SnapshotDeserializer snapshotDeserializer) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-snapshots-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SnapshotDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");

        return new KafkaConsumer<>(props, new StringDeserializer(), snapshotDeserializer);
    }
}
