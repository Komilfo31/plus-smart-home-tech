package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    @Query("SELECT DISTINCT s FROM Scenario s " +
            "LEFT JOIN FETCH s.conditions sc " +
            "LEFT JOIN FETCH sc.sensor " +
            "LEFT JOIN FETCH s.actions sa " +
            "LEFT JOIN FETCH sa.sensor " +
            "WHERE s.hubId = :hubId")
    List<Scenario> findByHubIdWithConditionsAndActions(@Param("hubId") String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);
}
