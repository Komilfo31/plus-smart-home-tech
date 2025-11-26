package ru.yandex.practicum.telemetry.analyzer.converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.Action;
import ru.yandex.practicum.telemetry.analyzer.entity.Condition;
import ru.yandex.practicum.telemetry.analyzer.entity.ConditionType;
import ru.yandex.practicum.telemetry.analyzer.entity.Operation;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioAction;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioActionId;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioConditionId;
import ru.yandex.practicum.telemetry.analyzer.entity.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioConverter {

    private final SensorRepository sensorRepository;

    public Scenario convertToScenario(String hubId, ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro event) {
        log.info("Конвертация сценария: hub={}, name={}", hubId, event.getName());

        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(event.getName().toString());

        if (event.getConditions() != null) {
            for (ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro conditionAvro : event.getConditions()) {
                try {
                    ScenarioCondition scenarioCondition = convertToScenarioCondition(hubId, conditionAvro);
                    scenarioCondition.setScenario(scenario);
                    scenario.getConditions().add(scenarioCondition);
                } catch (Exception e) {
                    log.error("Ошибка конвертации условия для сценария {}", event.getName(), e);
                }
            }
        }

        if (event.getActions() != null) {
            for (ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro actionAvro : event.getActions()) {
                try {
                    ScenarioAction scenarioAction = convertToScenarioAction(hubId, actionAvro);
                    scenarioAction.setScenario(scenario);
                    scenario.getActions().add(scenarioAction);
                } catch (Exception e) {
                    log.error("Ошибка конвертации действия для сценария {}", event.getName(), e);
                }
            }
        }

        log.info("Сценарий сконвертирован: {} условий, {} действий",
                scenario.getConditions().size(), scenario.getActions().size());
        return scenario;
    }

    private ScenarioCondition convertToScenarioCondition(String hubId,
                                                         ScenarioConditionAvro avroCondition) {
        ScenarioCondition scenarioCondition = new ScenarioCondition();

        ScenarioConditionId id = new ScenarioConditionId();
        id.setSensorId(avroCondition.getSensorId().toString());
        scenarioCondition.setId(id);

        String sensorId = avroCondition.getSensorId().toString();
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Сенсор не найден: " + sensorId + " для хаба: " + hubId));
        scenarioCondition.setSensor(sensor);

        Condition condition = new Condition();
        condition.setType(mapToConditionType(avroCondition.getType()).name());
        condition.setOperation(mapToOperation(avroCondition.getOperation()));
        condition.setValue(extractConditionValue(avroCondition.getValue()));
        scenarioCondition.setCondition(condition);

        return scenarioCondition;
    }

    private ScenarioAction convertToScenarioAction(String hubId,
                                                   DeviceActionAvro avroAction) {
        ScenarioAction scenarioAction = new ScenarioAction();

        ScenarioActionId id = new ScenarioActionId();
        id.setSensorId(avroAction.getSensorId().toString());
        scenarioAction.setId(id);

        String sensorId = avroAction.getSensorId().toString();
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Сенсор не найден: " + sensorId + " для хаба: " + hubId));
        scenarioAction.setSensor(sensor);

        Action action = new Action();
        action.setType(mapToActionType(avroAction.getType()));
        action.setValue(avroAction.getValue() != null ? avroAction.getValue() : 0);
        scenarioAction.setAction(action);

        return scenarioAction;
    }

    public ConditionType mapToConditionType(ConditionTypeAvro conditionTypeAvro) {
        return switch (conditionTypeAvro) {
            case MOTION -> ConditionType.MOTION;
            case LUMINOSITY -> ConditionType.LUMINOSITY;
            case SWITCH -> ConditionType.SWITCH;
            case TEMPERATURE -> ConditionType.TEMPERATURE;
            case CO2LEVEL -> ConditionType.CO2_LEVEL;
            case HUMIDITY -> ConditionType.HUMIDITY;
            default -> {
                log.warn("Неизвестный тип условия: {}", conditionTypeAvro);
                yield ConditionType.SWITCH;
            }
        };
    }

    public Operation mapToOperation(ConditionOperationAvro conditionOperationAvro) {
        return switch (conditionOperationAvro) {
            case EQUALS -> Operation.EQUALS;
            case GREATER_THAN -> Operation.GREATER;
            case LOWER_THAN -> Operation.LESS;
            default -> {
                log.warn("Неизвестная операция условия: {}", conditionOperationAvro);
                yield Operation.EQUALS;
            }
        };
    }

    public String mapToActionType(ActionTypeAvro actionTypeAvro) {
        return switch (actionTypeAvro) {
            case ACTIVATE -> "ACTIVATE";
            case DEACTIVATE -> "DEACTIVATE";
            case INVERSE -> "INVERSE";
            case SET_VALUE -> "SET_VALUE";
            default -> {
                log.warn("Неизвестный тип действия: {}", actionTypeAvro);
                yield "SET_VALUE";
            }
        };
    }

    private int extractConditionValue(Object value) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        } else {
            log.warn("Неизвестный тип значения условия: {}", value.getClass());
            return 0;
        }
    }
}