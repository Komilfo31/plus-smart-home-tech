package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventService {

    private final AnalyzerService analyzerService;

    @KafkaListener(
            topics = "${app.kafka.topics.hubs:telemetry.hubs.v1}",
            containerFactory = "hubEventKafkaListenerContainerFactory"
    )
    public void processHubEvents(List<HubEventAvro> hubEvents,
                                 Acknowledgment acknowledgment) {
        log.info("Получено {} hub событий: {}", hubEvents.size(),
                hubEvents.stream().map(e -> e.getClass().getSimpleName()).collect(Collectors.toList()));
        try {
            log.debug("Получено {} hub событий", hubEvents.size());

            for (HubEventAvro hubEvent : hubEvents) {
                processHubEvent(hubEvent);
            }

            acknowledgment.acknowledge();
            log.debug("Обработано {} hub событий", hubEvents.size());

        } catch (Exception e) {
            log.error("Ошибка обработки hub событий", e);
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
}
