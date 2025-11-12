package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.telemetry.collector.dto.hubevent.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.HubEvent;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.ScenarioRemovedEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.LightSensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.MotionSensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.SensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.SwitchSensorEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

public class EventMapper {

    private final SensorEventMapper sensorEventMapper;
    private final HubEventMapper hubEventMapper;

    public EventMapper() {
        this.sensorEventMapper = new SensorEventMapper();
        this.hubEventMapper = new HubEventMapper();
    }

    public SensorEventAvro toAvro(SensorEvent event) {
        return sensorEventMapper.toAvro(event);
    }

    public ClimateSensorAvro toAvro(ClimateSensorEvent event) {
        return sensorEventMapper.toAvro(event);
    }

    public LightSensorAvro toAvro(LightSensorEvent event) {
        return sensorEventMapper.toAvro(event);
    }

    public MotionSensorAvro toAvro(MotionSensorEvent event) {
        return sensorEventMapper.toAvro(event);
    }

    public SwitchSensorAvro toAvro(SwitchSensorEvent event) {
        return sensorEventMapper.toAvro(event);
    }

    public TemperatureSensorAvro toAvro(TemperatureSensorEvent event) {
        return sensorEventMapper.toAvro(event);
    }

    public HubEventAvro toAvro(HubEvent event) {
        return hubEventMapper.toAvro(event);
    }

    public DeviceAddedEventAvro toAvro(DeviceAddedEvent event) {
        return hubEventMapper.toAvro(event);
    }

    public DeviceRemovedEventAvro toAvro(DeviceRemovedEvent event) {
        return hubEventMapper.toAvro(event);
    }

    public ScenarioAddedEventAvro toAvro(ScenarioAddedEvent event) {
        return hubEventMapper.toAvro(event);
    }

    public ScenarioRemovedEventAvro toAvro(ScenarioRemovedEvent event) {
        return hubEventMapper.toAvro(event);
    }
}
