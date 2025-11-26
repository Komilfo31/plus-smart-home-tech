package ru.yandex.practicum.telemetry.analyzer.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import src.main.java.ru.yandex.practicum.telemetry.serialization.deserializer.HubEventDeserializer;
import src.main.java.ru.yandex.practicum.telemetry.serialization.deserializer.SnapshotDeserializer;

import java.util.Map;

@Configuration
@EnableKafka
public class AnalyzerKafkaConfig {

    @Bean
    public ConsumerFactory<String, HubEventAvro> hubEventConsumerFactory(
            KafkaProperties kafkaProperties,
            HubEventDeserializer hubEventDeserializer) {

        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                hubEventDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, HubEventAvro> hubEventKafkaListenerContainerFactory(
            ConsumerFactory<String, HubEventAvro> hubEventConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, HubEventAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hubEventConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumerFactory(
            KafkaProperties kafkaProperties,
            SnapshotDeserializer snapshotDeserializer) {

        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                snapshotDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SensorsSnapshotAvro> snapshotKafkaListenerContainerFactory(
            ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, SensorsSnapshotAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(snapshotConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public HubEventDeserializer hubEventDeserializer() {
        return new HubEventDeserializer();
    }

    @Bean
    public SnapshotDeserializer snapshotDeserializer() {
        return new SnapshotDeserializer();
    }
}
