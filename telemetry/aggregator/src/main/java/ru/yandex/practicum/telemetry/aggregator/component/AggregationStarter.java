package ru.yandex.practicum.telemetry.aggregator.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfig;

@Component
@Slf4j
@RequiredArgsConstructor
public class AggregationStarter implements ApplicationRunner {

    private final KafkaConfig kafkaConfig;

    @Override
    public void run(ApplicationArguments args) {
        start();
    }

    public void start() {
        logStartupInfo();
        initializeAggregation();
        log.info("Aggregator application started successfully");
    }

    public void logStartupInfo() {
        log.info("Aggregator Startup Configuration");
        log.info("Listening to Kafka topic: {}", kafkaConfig.getTopics().getSensors());
        log.info("Producing to Kafka topic: {}", kafkaConfig.getTopics().getSnapshots());
        log.info("Aggregator Ready");
    }

    private void initializeAggregation() {
        log.debug("Initializing aggregation components...");
    }
}
