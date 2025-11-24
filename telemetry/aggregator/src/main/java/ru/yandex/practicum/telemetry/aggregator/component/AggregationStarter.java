package ru.yandex.practicum.telemetry.aggregator.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AggregationStarter implements ApplicationRunner {

    @Value("${app.kafka.topics.sensors:telemetry.sensors.v1}")
    public String sensorsTopic;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Aggregator started. Listening to topic: {}", sensorsTopic);
    }
}
