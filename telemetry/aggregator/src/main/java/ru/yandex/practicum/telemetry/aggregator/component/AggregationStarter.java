package ru.yandex.practicum.telemetry.aggregator.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.service.SnapshotAggregationService;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final SnapshotAggregationService aggregationService;
    private final Consumer<String, SensorEventAvro> kafkaConsumer;
    private final Producer<String, SensorsSnapshotAvro> kafkaProducer;

    public void start() {
        try {
            kafkaConsumer.subscribe(Collections.singletonList("telemetry.sensors.v1"));
            log.info("Aggregator started. Subscribed to telemetry.sensors.v1");

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = kafkaConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    processEvent(record.value());
                }

                kafkaConsumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("Получен сигнал Wakeup - завершаем работу");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            cleanup();
        }
    }

    private void processEvent(SensorEventAvro event) {
        log.debug("Получено событие от датчика: {}, хаб: {}", event.getId(), event.getHubId());

        Optional<SensorsSnapshotAvro> updatedSnapshot = aggregationService.updateState(event);

        if (updatedSnapshot.isPresent()) {
            sendSnapshot(updatedSnapshot.get());
        }
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        ProducerRecord<String, SensorsSnapshotAvro> snapshotRecord =
                new ProducerRecord<>("telemetry.snapshots.v1", snapshot.getHubId().toString(), snapshot);

        kafkaProducer.send(snapshotRecord, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка отправки снапшота для хаба {}", snapshot.getHubId(), exception);
            } else {
                log.info("Снапшот отправлен для хаба {} в топик {}",
                        snapshot.getHubId(), metadata.topic());
            }
        });
    }

    private void cleanup() {
        try {
            kafkaProducer.flush();
            log.info("Данные продюсера сброшены в Kafka");

            kafkaConsumer.commitSync();
            log.info("Смещения консьюмера зафиксированы");
        } finally {
            kafkaConsumer.close();
            log.info("Консьюмер закрыт");

            kafkaProducer.close();
            log.info("Продюсер закрыт");
        }
    }
}
