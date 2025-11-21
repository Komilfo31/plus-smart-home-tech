package ru.yandex.practicum.telemetry.analyzer.repository;

import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;

import java.util.List;

public interface AnalyzerService {

    void addDevice(DeviceAddedEventAvro deviceAdded, String hubId);

    void removeDevice(DeviceRemovedEventAvro deviceRemoved);

    void addScenario(ScenarioAddedEventAvro scenarioAdded, String hubId);

    void removeScenario(ScenarioRemovedEventAvro scenarioRemoved, String hubId);

    List<Scenario> getScenariosByHubId(String hubId);
}
