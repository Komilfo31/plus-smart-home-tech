package ru.yandex.practicum.telemetry.collector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CollectorService {

    public static void main(String[] args) {
        try {
            log.info("Starting Collector Service");
            SpringApplication.run(CollectorService.class, args);
            log.info("Collector Service started successfully");
        } catch (Exception e) {
            log.error("Failed to start Collector Service", e);
            throw e;
        }
    }
}
