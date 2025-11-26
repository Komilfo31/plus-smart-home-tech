package ru.yandex.practicum.telemetry.collector.dto.hubevent;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeviceAction {
    private String sensorId;
    private ActionType type;
    private Integer value;
}
