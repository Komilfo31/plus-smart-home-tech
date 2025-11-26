package ru.yandex.practicum.telemetry.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Properties;

@Getter
@Setter
@Component
@ConfigurationProperties("app.kafka")
public class KafkaConfig {
    private Topics topics = new Topics();
    private ProducerConfig producer = new ProducerConfig();
    private ConsumerConfig consumer = new ConsumerConfig();

    @Getter
    @Setter
    public static class Topics {
        private String sensors;
        private String snapshots;
    }

    @Getter
    @Setter
    public static class ProducerConfig {
        private Properties properties = new Properties();
    }

    @Getter
    @Setter
    public static class ConsumerConfig {
        private Duration pollTimeout = Duration.ofSeconds(5);
        private Properties properties = new Properties();
    }
}
