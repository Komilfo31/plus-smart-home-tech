package ru.yandex.practicum.telemetry.collector.dto.hubevent;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ScenarioCondition {
    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private Integer value;
}
