package ru.yandex.practicum.telemetry.collector.service;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.HubEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.SensorEvent;
import ru.yandex.practicum.telemetry.collector.exception.EventProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.telemetry.collector.mapper.EventMapper;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventService implements EventService {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate; // ← Изменили тип
    private final EventMapper eventMapper = new EventMapper();

    private static final String SENSORS_TOPIC = "telemetry.sensors.v1";
    private static final String HUBS_TOPIC = "telemetry.hubs.v1";

    @Override
    public void processSensorEvent(SensorEvent event) {
        try {
            SensorEventAvro avroEvent = eventMapper.toAvro(event);

            if (avroEvent == null) {
                throw new EventProcessingException("Failed to map sensor event to Avro");
            }

            kafkaTemplate.send(SENSORS_TOPIC, event.getId(), avroEvent)
                    .get(10, TimeUnit.SECONDS);

            log.debug("Successfully sent sensor event to Kafka: {}", event.getId());

        } catch (Exception e) {
            log.error("Error processing sensor event: {}", event.getId(), e);
            throw new EventProcessingException("Failed to process sensor event: " + event.getId(), e);
        }
    }

    @Override
    public void processHubEvent(HubEvent event) {
        try {
            HubEventAvro avroEvent = eventMapper.toAvro(event);

            if (avroEvent == null) {
                throw new EventProcessingException("Failed to map hub event to Avro");
            }

            kafkaTemplate.send(HUBS_TOPIC, event.getHubId(), avroEvent)
                    .get(10, TimeUnit.SECONDS);

            log.debug("Successfully sent hub event to Kafka: {}", event.getHubId());

        } catch (Exception e) {
            log.error("Error processing hub event: {}", event.getHubId(), e);
            throw new EventProcessingException("Failed to process hub event: " + event.getHubId(), e);
        }
    }
}
