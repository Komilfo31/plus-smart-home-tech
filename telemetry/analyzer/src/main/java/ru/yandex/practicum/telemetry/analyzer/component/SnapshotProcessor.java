package ru.yandex.practicum.telemetry.analyzer.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.service.SnapshotAnalysisService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final Consumer<String, SensorsSnapshotAvro> snapshotsConsumer;
    private final SnapshotAnalysisService snapshotAnalysisService;

    public void start() {
        try {
            snapshotsConsumer.subscribe(Collections.singletonList("telemetry.snapshots.v1"));
            log.info("SnapshotProcessor started. Subscribed to telemetry.snapshots.v1");

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        snapshotsConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    processSnapshot(record.value());
                }

                snapshotsConsumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("SnapshotProcessor: получен сигнал Wakeup");
        } catch (Exception e) {
            log.error("Ошибка в SnapshotProcessor", e);
        } finally {
            cleanup();
        }
    }

    private void processSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            log.debug("Обработка снапшота для хаба: {}", snapshot.getHubId());
            snapshotAnalysisService.analyzeSnapshot(snapshot);
        } catch (Exception e) {
            log.error("Ошибка обработки снапшота для хаба: {}", snapshot.getHubId(), e);
        }
    }

    private void cleanup() {
        try {
            snapshotsConsumer.commitSync();
            log.info("SnapshotProcessor: смещения зафиксированы");
        } finally {
            snapshotsConsumer.close();
            log.info("SnapshotProcessor: консьюмер закрыт");
        }
    }
}
