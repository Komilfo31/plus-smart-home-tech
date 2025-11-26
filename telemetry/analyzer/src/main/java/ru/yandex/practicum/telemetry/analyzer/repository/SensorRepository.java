package ru.yandex.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.telemetry.analyzer.entity.Sensor;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, String> {

    Optional<Sensor> findByIdAndHubId(String id, String hubId);

    List<Sensor> findByHubId(String hubId);

    void deleteByIdAndHubId(String id, String hubId);

    @Modifying
    @Query("DELETE FROM Sensor s WHERE s.hubId = :hubId AND s.id = :deviceId")
    int deleteByHubIdAndId(@Param("hubId") String hubId, @Param("deviceId") String deviceId);


    @Query("SELECT COUNT(s) > 0 FROM Sensor s WHERE s.hubId = :hubId AND s.id = :deviceId")
    boolean existsByHubIdAndId(@Param("hubId") String hubId, @Param("deviceId") String deviceId);

}
