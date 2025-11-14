package ru.yandex.practicum.telemetry.collector.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventService;

@Slf4j
@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final KafkaEventService kafkaEventService;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("Обработка события датчика освещения: {}", event.getId());
        kafkaEventService.processSensorEvent(event);
    }
}
