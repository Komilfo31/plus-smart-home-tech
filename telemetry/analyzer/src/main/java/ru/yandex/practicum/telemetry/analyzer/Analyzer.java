package ru.yandex.practicum.telemetry.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Analyzer {

    private static final Logger log = LoggerFactory.getLogger(Analyzer.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Analyzer.class, args);

        log.info("Analyzer application started successfully");
        log.info("Sending commands via gRPC to hub-router");

        addShutdownHook(context);
    }

    private static void addShutdownHook(ConfigurableApplicationContext context) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            context.close();
            log.info("Analyzer application shutdown completed");
        }));
    }
}
