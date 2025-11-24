package ru.yandex.practicum.telemetry.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Aggregator {

    private static final Logger log = LoggerFactory.getLogger(Aggregator.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Aggregator.class, args);

        log.info("Aggregator application started successfully");
        log.info("Listening to Kafka topic: telemetry.sensors.v1");
        log.info("Producing to Kafka topic: telemetry.snapshots.v1");
    }
}
