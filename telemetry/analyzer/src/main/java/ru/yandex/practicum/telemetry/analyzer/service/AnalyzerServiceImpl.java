package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.converter.ScenarioConverter;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.entity.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnalyzerServiceImpl implements AnalyzerService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ScenarioConverter scenarioConverter;

    @Override
    public void addDevice(DeviceAddedEventAvro deviceAdded, String hubId) {
        try {
            String deviceId = deviceAdded.getId().toString();

            Sensor sensor = sensorRepository.findById(deviceId)
                    .orElse(new Sensor());

            sensor.setId(deviceId);
            sensor.setHubId(hubId);

            sensorRepository.save(sensor);
            log.info("Устройство добавлено: hub={}, device={}", hubId, deviceId);

        } catch (Exception e) {
            log.error("Ошибка добавления устройства: hub={}, device={}", hubId, deviceAdded.getId(), e);
            throw new RuntimeException("Не удалось добавить устройство", e);
        }
    }

    @Override
    public void removeDevice(DeviceRemovedEventAvro deviceRemoved) {
        try {
            String deviceId = deviceRemoved.getId().toString();

            Sensor sensor = sensorRepository.findById(deviceId)
                    .orElseThrow(() -> {
                        log.warn("Устройство не найдено для удаления: device={}", deviceId);
                        return new RuntimeException("Устройство не найдено: " + deviceId);
                    });

            String hubId = sensor.getHubId();
            sensorRepository.delete(sensor);
            log.info("Устройство удалено: hub={}, device={}", hubId, deviceId);

        } catch (Exception e) {
            log.error("Ошибка удаления устройства: device={}", deviceRemoved.getId(), e);
            throw new RuntimeException("Не удалось удалить устройство", e);
        }
    }

    @Override
    public void addScenario(ScenarioAddedEventAvro scenarioAdded, String hubId) {
        try {
            validateScenarioSensors(hubId, scenarioAdded);

            Scenario scenario = scenarioConverter.convertToScenario(hubId, scenarioAdded);

            scenarioRepository.findByHubIdAndName(hubId, scenario.getName())
                    .ifPresent(existing -> scenarioRepository.delete(existing));

            scenarioRepository.save(scenario);
            log.info("Сценарий добавлен: hub={}, name={}, conditions={}, actions={}",
                    hubId, scenarioAdded.getName(),
                    scenario.getConditions().size(), scenario.getActions().size());

        } catch (Exception e) {
            log.error("Ошибка добавления сценария: hub={}, name={}",
                    hubId, scenarioAdded.getName(), e);
            throw new RuntimeException("Не удалось добавить сценарий", e);
        }
    }

    @Override
    public void removeScenario(ScenarioRemovedEventAvro scenarioRemoved, String hubId) {
        try {
            String scenarioName = scenarioRemoved.getName().toString();
            scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                    .ifPresentOrElse(
                            scenario -> {
                                scenarioRepository.delete(scenario);
                                log.info("Сценарий удален: hub={}, name={}", hubId, scenarioName);
                            },
                            () -> log.warn("Сценарий не найден для удаления: hub={}, name={}", hubId, scenarioName)
                    );

        } catch (Exception e) {
            log.error("Ошибка удаления сценария: hub={}, name={}",
                    hubId, scenarioRemoved.getName(), e);
            throw new RuntimeException("Не удалось удалить сценарий", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Scenario> getScenariosByHubId(String hubId) {
        return scenarioRepository.findByHubIdWithConditionsAndActions(hubId);
    }

    private void validateScenarioSensors(String hubId, ScenarioAddedEventAvro scenarioAdded) {
        Set<String> missingSensors = new HashSet<>();

        for (var condition : scenarioAdded.getConditions()) {
            String sensorId = condition.getSensorId().toString();
            if (!sensorRepository.existsByHubIdAndId(hubId, sensorId)) {
                missingSensors.add(sensorId);
            }
        }

        for (var action : scenarioAdded.getActions()) {
            String sensorId = action.getSensorId().toString();
            if (!sensorRepository.existsByHubIdAndId(hubId, sensorId)) {
                missingSensors.add(sensorId);
            }
        }

        if (!missingSensors.isEmpty()) {
            String errorMessage = String.format(
                    "Сценарий '%s' ссылается на несуществующие датчики в хабе %s: %s",
                    scenarioAdded.getName(), hubId, missingSensors
            );
            log.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
