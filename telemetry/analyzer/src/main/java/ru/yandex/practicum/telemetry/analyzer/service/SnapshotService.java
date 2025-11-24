package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotService {

    private final SnapshotAnalysisService snapshotAnalysisService;

    @KafkaListener(
            topics = "${app.kafka.topics.snapshots}",
            containerFactory = "snapshotKafkaListenerContainerFactory"
    )
    public void processSnapshots(List<SensorsSnapshotAvro> snapshots,
                                 Acknowledgment acknowledgment) {
        try {
            log.debug("Получено {} снапшотов", snapshots.size());

            for (SensorsSnapshotAvro snapshot : snapshots) {
                processSnapshot(snapshot);
            }

            acknowledgment.acknowledge();
            log.debug("Обработано {} снапшотов", snapshots.size());

        } catch (Exception e) {
            log.error("Ошибка обработки снапшотов", e);
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
}
