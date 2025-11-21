package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioAction;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;

    @Transactional(readOnly = true)
    public List<Scenario> getScenariosByHubId(String hubId) {
        return scenarioRepository.findByHubIdWithConditionsAndActions(hubId);
    }

    @Transactional
    public Scenario createOrUpdateScenario(Scenario scenario) {
        Optional<Scenario> existing = scenarioRepository.findByHubIdAndName(
                scenario.getHubId(), scenario.getName());

        if (existing.isPresent()) {
            Scenario existingScenario = existing.get();

            existingScenario.getConditions().clear();
            existingScenario.getConditions().addAll(scenario.getConditions());

            existingScenario.getActions().clear();
            existingScenario.getActions().addAll(scenario.getActions());

            log.info("Обновлен сценарий: {} для хаба: {}", scenario.getName(), scenario.getHubId());
            return scenarioRepository.save(existingScenario);
        } else {
            log.info("Создан новый сценарий: {} для хаба: {}", scenario.getName(), scenario.getHubId());
            return scenarioRepository.save(scenario);
        }
    }

    @Transactional
    public void deleteScenario(String hubId, String scenarioName) {
        scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .ifPresent(scenario -> {
                    scenarioRepository.delete(scenario);
                    log.info("Удален сценарий: {} для хаба: {}", scenarioName, hubId);
                });
    }

    public List<ScenarioAction> getScenarioActions(Scenario scenario) {
        return scenario.getActions().stream().toList();
    }

    public List<ScenarioCondition> getScenarioConditions(Scenario scenario) {
        return scenario.getConditions().stream().toList();
    }
}
