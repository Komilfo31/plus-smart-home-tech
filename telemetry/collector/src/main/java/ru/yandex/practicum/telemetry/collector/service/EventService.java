package ru.yandex.practicum.telemetry.collector.service;

import ru.yandex.practicum.telemetry.collector.dto.hubevent.HubEvent;
import ru.yandex.practicum.telemetry.collector.dto.sensorevent.SensorEvent;

public interface EventService {
    void processSensorEvent(SensorEvent event);
    void processHubEvent(HubEvent event);
}
