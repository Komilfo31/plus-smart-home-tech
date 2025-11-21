package ru.yandex.practicum.telemetry.analyzer.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.repository.AnalyzerService;

import java.time.Duration;
import java.util.Collections;


@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> hubEventsConsumer;
    private final AnalyzerService analyzerService;

    @Override
    public void run() {
        try {
            hubEventsConsumer.subscribe(Collections.singletonList("telemetry.hubs.v1"));
            log.info("HubEventProcessor started in thread: {}. Subscribed to telemetry.hubs.v1",
                    Thread.currentThread().getName());

            while (true) {
                ConsumerRecords<String, HubEventAvro> records =
                        hubEventsConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    processHubEvent(record.value());
                }

                hubEventsConsumer.commitSync();
            }

        } catch (WakeupException ignored) {
            log.info("HubEventProcessor: получен сигнал Wakeup");
        } catch (Exception e) {
            log.error("Ошибка в HubEventProcessor", e);
        } finally {
            cleanup();
        }
    }


    private void processHubEvent(HubEventAvro event) {
        try {
            String hubId = event.getHubId();
            Object payload = event.getPayload();

            if (payload instanceof DeviceAddedEventAvro) {
                analyzerService.addDevice((DeviceAddedEventAvro) payload, hubId);

            } else if (payload instanceof ScenarioAddedEventAvro) {
                analyzerService.addScenario((ScenarioAddedEventAvro) payload, hubId);

            } else if (payload instanceof DeviceRemovedEventAvro) {
                analyzerService.removeDevice((DeviceRemovedEventAvro) payload);

            } else if (payload instanceof ScenarioRemovedEventAvro) {
                analyzerService.removeScenario((ScenarioRemovedEventAvro) payload, hubId);

            } else {
                log.warn("Неизвестный тип события: {}", payload.getClass().getSimpleName());
            }

        } catch (Exception e) {
            log.error("Ошибка обработки события хаба", e);
        }
    }

    private void cleanup() {
        try {
            hubEventsConsumer.commitSync();
            log.info("HubEventProcessor: смещения зафиксированы");
        } finally {
            hubEventsConsumer.close();
            log.info("HubEventProcessor: консьюмер закрыт");
        }
    }

}
