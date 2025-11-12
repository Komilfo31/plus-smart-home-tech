package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.telemetry.collector.dto.sensorevent.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.LightSensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.MotionSensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.SensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.SwitchSensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

public class SensorEventMapper {

    public SensorEventAvro toAvro(SensorEvent event) {
        if (event == null) {
            return null;
        }

        Object payload = determinePayload(event);
        if (payload == null) {
            throw new IllegalStateException("Не удалось определить payload для типа события: " + event.getClass().getSimpleName());
        }

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private Object determinePayload(SensorEvent event) {
        if (event instanceof ClimateSensorEvent) {
            return toAvro((ClimateSensorEvent) event);
        } else if (event instanceof LightSensorEvent) {
            return toAvro((LightSensorEvent) event);
        } else if (event instanceof MotionSensorEvent) {
            return toAvro((MotionSensorEvent) event);
        } else if (event instanceof SwitchSensorEvent) {
            return toAvro((SwitchSensorEvent) event);
        } else if (event instanceof TemperatureSensorEvent) {
            return toAvro((TemperatureSensorEvent) event);
        } else {
            throw new IllegalArgumentException("Неизвестный тип события датчика: " + event.getClass().getSimpleName());
        }
    }

    public ClimateSensorAvro toAvro(ClimateSensorEvent event) {
        if (event == null) {
            return null;
        }

        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
    }

    public LightSensorAvro toAvro(LightSensorEvent event) {
        if (event == null) {
            return null;
        }

        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    public MotionSensorAvro toAvro(MotionSensorEvent event) {
        if (event == null) {
            return null;
        }

        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.getMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    public SwitchSensorAvro toAvro(SwitchSensorEvent event) {
        if (event == null) {
            return null;
        }

        return SwitchSensorAvro.newBuilder()
                .setState(event.getState())
                .build();
    }

    public TemperatureSensorAvro toAvro(TemperatureSensorEvent event) {
        if (event == null) {
            return null;
        }

        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }
}
