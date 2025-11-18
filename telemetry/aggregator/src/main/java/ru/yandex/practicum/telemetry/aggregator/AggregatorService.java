package ru.yandex.practicum.telemetry.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.telemetry.aggregator.component.AggregationStarter;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AggregatorService {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AggregatorService.class, args);

        AggregationStarter aggregator = context.getBean(AggregationStarter.class);
        aggregator.start();

        context.registerShutdownHook();
    }
}
