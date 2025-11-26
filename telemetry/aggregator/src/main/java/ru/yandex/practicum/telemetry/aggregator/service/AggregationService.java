package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfig;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationService {

    private final SnapshotAggregationService aggregationService;
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    private final KafkaConfig kafkaConfig;

    @KafkaListener(
            topics = "${app.kafka.topics.sensors}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void processSensorEvents(List<ConsumerRecord<String, SensorEventAvro>> records,
                                    Acknowledgment acknowledgment) {
        try {
            log.debug("Получена пачка из {} событий", records.size());

            for (ConsumerRecord<String, SensorEventAvro> record : records) {
                processEvent(record.value());
            }

            acknowledgment.acknowledge();
            log.debug("Успешно обработано {} событий", records.size());

        } catch (Exception e) {
            log.error("Ошибка обработки пачки событий", e);
        }
    }

    private void processEvent(SensorEventAvro event) {
        try {
            log.debug("Обработка события от датчика: {}, хаб: {}", event.getId(), event.getHubId());

            Optional<SensorsSnapshotAvro> updatedSnapshot = aggregationService.updateState(event);

            if (updatedSnapshot.isPresent()) {
                sendSnapshot(updatedSnapshot.get());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки события датчика {}: {}", event.getId(), e.getMessage());
        }
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            String topic = kafkaConfig.getTopics().getSnapshots();

            kafkaTemplate.send(
                    topic,
                    null,
                    snapshot.getTimestamp().toEpochMilli(),
                    snapshot.getHubId().toString(),
                    snapshot
            );

            log.debug("Снапшот отправлен для хаба: {}, топик: {}", snapshot.getHubId(), topic);

        } catch (Exception e) {
            log.error("Ошибка отправки снапшота для хаба {}", snapshot.getHubId(), e);
        }
    }
}
