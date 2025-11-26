package ru.yandex.practicum.telemetry.collector.mapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public EventMapper eventMapper() {
        return new EventMapper();
    }

    @Bean
    public ProtoToAvroMapper protoToAvroMapper() {
        return new ProtoToAvroMapper();
    }
}
