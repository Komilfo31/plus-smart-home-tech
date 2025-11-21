package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.telemetry.analyzer.entity.Sensor;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, String> {

    Optional<Sensor> findByIdAndHubId(String id, String hubId);

    List<Sensor> findByHubId(String hubId);

    void deleteByIdAndHubId(String id, String hubId);
}
