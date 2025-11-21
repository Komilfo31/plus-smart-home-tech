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
import ru.yandex.practicum.telemetry.analyzer.repository.AnalyzerService;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.List;

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
            String deviceType = deviceAdded.getType().toString();

            Sensor sensor = sensorRepository.findById(deviceId)
                    .orElse(new Sensor());

            sensor.setId(deviceId);
            sensor.setHubId(hubId);
            sensor.setId(deviceType);

            sensorRepository.save(sensor);
            log.info("Устройство добавлено: hub={}, device={}, type={}", hubId, deviceId, deviceType);

        } catch (Exception e) {
            log.error("Ошибка добавления устройства: hub={}, device={}", hubId, deviceAdded.getId(), e);
            throw new RuntimeException("Не удалось добавить устройство", e);
        }
    }

    @Override
    public void removeDevice(DeviceRemovedEventAvro deviceRemoved) {
        try {
            String deviceId = deviceRemoved.getId().toString();
            sensorRepository.deleteById(deviceId);
            log.info("Устройство удалено: device={}", deviceId);

        } catch (Exception e) {
            log.error("Ошибка удаления устройства: device={}", deviceRemoved.getId(), e);
            throw new RuntimeException("Не удалось удалить устройство", e);
        }
    }

    @Override
    public void addScenario(ScenarioAddedEventAvro scenarioAdded, String hubId) {
        try {
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
                    .ifPresent(scenario -> {
                        scenarioRepository.delete(scenario);
                        log.info("Сценарий удален: hub={}, name={}", hubId, scenarioName);
                    });

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
}
