package ru.yandex.practicum.telemetry.collector.dto.sensorevent;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class SwitchSensorEvent extends SensorEvent {
    @NotNull
    private Boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
