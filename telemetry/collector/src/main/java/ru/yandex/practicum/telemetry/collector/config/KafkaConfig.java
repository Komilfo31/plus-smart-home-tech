package ru.yandex.practicum.telemetry.collector.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import src.main.java.ru.yandex.practicum.telemetry.serialization.serializer.CollectorAvroSerializer;

import java.util.Map;


@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, SpecificRecordBase> producerFactory(
            KafkaProperties kafkaProperties,
            CollectorAvroSerializer collectorAvroSerializer) {

        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                collectorAvroSerializer
        );
    }

    @Bean
    public KafkaTemplate<String, SpecificRecordBase> kafkaTemplate(
            ProducerFactory<String, SpecificRecordBase> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public CollectorAvroSerializer collectorAvroSerializer() {
        return new CollectorAvroSerializer();
    }
}
