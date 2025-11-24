package ru.yandex.practicum.telemetry.aggregator.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.serialization.deserializer.SensorEventDeserializer;
import ru.yandex.practicum.telemetry.serialization.serializer.AggregatorAvroSerializer;

import java.util.Map;

@Configuration
@EnableKafka
public class AggregatorKafkaConfig {

    @Bean
    public ConsumerFactory<String, SensorEventAvro> sensorEventConsumerFactory(
            KafkaProperties kafkaProperties,
            SensorEventDeserializer sensorEventDeserializer) {

        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                sensorEventDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SensorEventAvro> kafkaListenerContainerFactory(
            ConsumerFactory<String, SensorEventAvro> sensorEventConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, SensorEventAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sensorEventConsumerFactory);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ProducerFactory<String, SpecificRecordBase> producerFactory(
            KafkaProperties kafkaProperties) {

        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        return new DefaultKafkaProducerFactory<>(
                props,
                new StringSerializer(),
                new AggregatorAvroSerializer()
        );
    }

    @Bean
    public KafkaTemplate<String, SpecificRecordBase> kafkaTemplate(
            ProducerFactory<String, SpecificRecordBase> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public SensorEventDeserializer sensorEventDeserializer() {
        return new SensorEventDeserializer();
    }
}
