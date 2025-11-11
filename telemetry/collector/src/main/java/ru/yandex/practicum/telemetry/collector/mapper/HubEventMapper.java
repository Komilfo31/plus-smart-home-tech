package ru.yandex.practicum.telemetry.collector.mapper;

import ru.yandex.practicum.telemetry.collector.dto.hubevent.ActionType;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.ConditionOperation;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.ConditionType;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.DeviceAction;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.DeviceType;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.HubEvent;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.ScenarioCondition;
import ru.yandex.practicum.telemetry.collector.dto.hubevent.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.List;
import java.util.stream.Collectors;

public class HubEventMapper {

    public HubEventAvro toAvro(HubEvent event) {
        if (event == null) {
            return null;
        }

        Object payload = determinePayload(event);
        if (payload == null) {
            throw new IllegalStateException("Не удалось определить payload для типа события: " + event.getClass().getSimpleName());
        }

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private Object determinePayload(HubEvent event) {
        if (event instanceof DeviceAddedEvent) {
            return toAvro((DeviceAddedEvent) event);
        } else if (event instanceof DeviceRemovedEvent) {
            return toAvro((DeviceRemovedEvent) event);
        } else if (event instanceof ScenarioAddedEvent) {
            return toAvro((ScenarioAddedEvent) event);
        } else if (event instanceof ScenarioRemovedEvent) {
            return toAvro((ScenarioRemovedEvent) event);
        } else {
            throw new IllegalArgumentException("Неизвестный тип события хаба: " + event.getClass().getSimpleName());
        }
    }

    public DeviceAddedEventAvro toAvro(DeviceAddedEvent event) {
        if (event == null) {
            return null;
        }

        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(mapDeviceType(event.getDeviceType()))
                .build();
    }

    public DeviceRemovedEventAvro toAvro(DeviceRemovedEvent event) {
        if (event == null) {
            return null;
        }

        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    public ScenarioAddedEventAvro toAvro(ScenarioAddedEvent event) {
        if (event == null) {
            return null;
        }

        ScenarioAddedEventAvro.Builder builder = ScenarioAddedEventAvro.newBuilder()
                .setName(event.getName());

        if (event.getConditions() != null) {
            List<ScenarioConditionAvro> conditions = event.getConditions().stream()
                    .map(this::toAvro)
                    .collect(Collectors.toList());
            builder.setConditions(conditions);
        }

        if (event.getActions() != null) {
            List<DeviceActionAvro> actions = event.getActions().stream()
                    .map(this::toAvro)
                    .collect(Collectors.toList());
            builder.setActions(actions);
        }

        return builder.build();
    }

    public ScenarioRemovedEventAvro toAvro(ScenarioRemovedEvent event) {
        if (event == null) {
            return null;
        }

        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }

    public ScenarioConditionAvro toAvro(ScenarioCondition condition) {
        if (condition == null) {
            return null;
        }

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(mapConditionType(condition.getType()))
                .setOperation(mapConditionOperation(condition.getOperation()))
                .setValue(condition.getValue())
                .build();
    }

    public DeviceActionAvro toAvro(DeviceAction action) {
        if (action == null) {
            return null;
        }

        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(mapActionType(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    private DeviceTypeAvro mapDeviceType(DeviceType type) {
        if (type == null) {
            return null;
        }
        return DeviceTypeAvro.valueOf(type.name());
    }

    private ConditionTypeAvro mapConditionType(ConditionType type) {
        if (type == null) {
            return null;
        }
        return ConditionTypeAvro.valueOf(type.name());
    }

    private ConditionOperationAvro mapConditionOperation(ConditionOperation operation) {
        if (operation == null) {
            return null;
        }
        return ConditionOperationAvro.valueOf(operation.name());
    }

    private ActionTypeAvro mapActionType(ActionType type) {
        if (type == null) {
            return null;
        }
        return ActionTypeAvro.valueOf(type.name());
    }
}
