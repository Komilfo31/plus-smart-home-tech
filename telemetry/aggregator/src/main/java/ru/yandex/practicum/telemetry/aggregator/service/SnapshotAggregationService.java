package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SnapshotAggregationService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId().toString();
        String sensorId = event.getId().toString();
        Instant eventTimestamp = event.getTimestamp();

        log.debug("Обновление состояния для хаба: {}, датчик: {}", hubId, sensorId);

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, id ->
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(hubId)
                        .setTimestamp(eventTimestamp)
                        .setSensorsState(new HashMap<>())
                        .build()
        );

        //существующее состояние датчика
        Map<String, SensorStateAvro> sensorsState = new HashMap<>(snapshot.getSensorsState());
        SensorStateAvro oldState = sensorsState.get(sensorId);

        if (oldState != null) {
            if (oldState.getTimestamp().isAfter(eventTimestamp)) {
                log.debug("Событие устарело для датчика {}. Пропускаем.", sensorId);
                return Optional.empty();
            }

            if (dataEquals(oldState.getData(), event.getPayload())) {
                log.debug("Данные датчика {} не изменились. Пропускаем.", sensorId);
                return Optional.empty();
            }
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(eventTimestamp)
                .setData(event.getPayload())
                .build();

        sensorsState.put(sensorId, newState);

        SensorsSnapshotAvro updatedSnapshot = SensorsSnapshotAvro.newBuilder(snapshot)
                .setTimestamp(eventTimestamp)
                .setSensorsState(sensorsState)
                .build();

        snapshots.put(hubId, updatedSnapshot);

        log.info("Снапшот хаба {} обновлен. Датчик: {}, время: {}",
                hubId, sensorId, eventTimestamp);
        return Optional.of(updatedSnapshot);
    }

    private boolean dataEquals(Object data1, Object data2) {
        if (data1 == null && data2 == null) return true;
        if (data1 == null || data2 == null) return false;
        return data1.equals(data2);
    }
}
