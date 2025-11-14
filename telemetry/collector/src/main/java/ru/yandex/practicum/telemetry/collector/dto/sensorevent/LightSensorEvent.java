package ru.yandex.practicum.telemetry.collector.dto.sensorevent;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class LightSensorEvent extends SensorEvent {
    private Integer linkQuality;
    private Integer luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}