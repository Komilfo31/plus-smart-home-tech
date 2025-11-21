package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.Condition;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioAction;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotAnalysisService {

    private final ScenarioService scenarioService;
    private final GrpcCommandService grpcCommandService;

    public void analyzeSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();

        log.debug("Анализ снапшота для хаба: {}", hubId);

        List<Scenario> scenarios = scenarioService.getScenariosByHubId(hubId);

        for (Scenario scenario : scenarios) {
            if (isScenarioTriggered(scenario, sensorsState)) {
                log.info("Сценарий '{}' активирован для хаба: {}", scenario.getName(), hubId);
                executeScenarioActions(scenario, hubId);
            }
        }
    }

    private boolean isScenarioTriggered(Scenario scenario, Map<String, SensorStateAvro> sensorsState) {
        return scenario.getConditions().stream()
                .allMatch(scenarioCondition -> isConditionSatisfied(scenarioCondition, sensorsState));
    }

    private boolean isConditionSatisfied(ScenarioCondition scenarioCondition,
                                         Map<String, SensorStateAvro> sensorsState) {
        String sensorId = scenarioCondition.getSensor().getId();
        Condition condition = scenarioCondition.getCondition();
        SensorStateAvro sensorState = sensorsState.get(sensorId);

        if (sensorState == null) {
            log.debug("Датчик {} не найден в снапшоте", sensorId);
            return false;
        }

        Object sensorData = sensorState.getData();
        int sensorValue = extractSensorValue(sensorData, condition.getType());

        return evaluateCondition(sensorValue, condition);
    }

    private void executeScenarioActions(Scenario scenario, String hubId) {
        List<ActionExecution> actionsToExecute = new ArrayList<>();

        for (ScenarioAction scenarioAction : scenario.getActions()) {
            actionsToExecute.add(new ActionExecution(
                    scenarioAction.getSensor().getId(),
                    scenarioAction.getAction().getType(),
                    scenarioAction.getAction().getValue()
            ));
        }

        grpcCommandService.executeActions(hubId, scenario.getName(), actionsToExecute);
    }

    private int extractSensorValue(Object sensorData, String conditionType) {
        try {
            if (sensorData == null) {
                return 0;
            }

            switch (conditionType) {
                case "TEMPERATURE":
                    if (sensorData instanceof TemperatureSensorAvro) {
                        return ((TemperatureSensorAvro) sensorData).getTemperatureC();
                    }
                    if (sensorData instanceof ClimateSensorAvro) {
                        return ((ClimateSensorAvro) sensorData).getTemperatureC();
                    }
                    break;

                case "HUMIDITY":
                    if (sensorData instanceof ClimateSensorAvro) {
                        return ((ClimateSensorAvro) sensorData).getHumidity();
                    }
                    break;

                case "CO2_LEVEL":
                    if (sensorData instanceof ClimateSensorAvro) {
                        return ((ClimateSensorAvro) sensorData).getCo2Level();
                    }
                    break;

                case "LUMINOSITY":
                    if (sensorData instanceof LightSensorAvro) {
                        return ((LightSensorAvro) sensorData).getLuminosity();
                    }
                    break;

                case "LINK_QUALITY":
                    if (sensorData instanceof LightSensorAvro) {
                        return ((LightSensorAvro) sensorData).getLinkQuality();
                    }
                    if (sensorData instanceof MotionSensorAvro) {
                        return ((MotionSensorAvro) sensorData).getLinkQuality();
                    }
                    break;

                case "MOTION":
                    if (sensorData instanceof MotionSensorAvro) {
                        return ((MotionSensorAvro) sensorData).getMotion() ? 1 : 0;
                    }
                    break;

                case "VOLTAGE":
                    if (sensorData instanceof MotionSensorAvro) {
                        return ((MotionSensorAvro) sensorData).getVoltage();
                    }
                    break;

                case "SWITCH":
                    if (sensorData instanceof SwitchSensorAvro) {
                        return ((SwitchSensorAvro) sensorData).getState() ? 1 : 0;
                    }
                    break;

                default:
                    log.debug("Неизвестный тип условия: {}", conditionType);
                    return 0;
            }

            log.debug("Тип данных {} не соответствует ожидаемому типу условия {}",
                    sensorData.getClass().getSimpleName(), conditionType);

        } catch (Exception e) {
            log.warn("Ошибка извлечения значения датчика для типа {}: {}", conditionType, e.getMessage());
        }

        return 0;
    }

    private boolean evaluateCondition(int sensorValue, Condition condition) {
        switch (condition.getOperation()) {
            case GREATER:
                return sensorValue > condition.getValue();
            case LESS:
                return sensorValue < condition.getValue();
            case EQUALS:
                return sensorValue == condition.getValue();
            default:
                return false;
        }
    }

    public record ActionExecution(String sensorId, String actionType, int value) {
    }
}