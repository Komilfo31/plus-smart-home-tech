package ru.yandex.practicum.telemetry.analyzer.service;

import com.google.protobuf.Empty;
import com.google.protobuf.util.Timestamps;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.telemetry.analyzer.mapper.ActionTypeMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcCommandService {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    private final ActionTypeMapper actionTypeMapper;

    public void executeActions(String hubId, String scenarioName,
                               List<SnapshotAnalysisService.ActionExecution> actions) {
        for (SnapshotAnalysisService.ActionExecution action : actions) {
            try {
                String sensorIdString = String.valueOf(action.sensorId());

                ActionTypeProto actionTypeProto = actionTypeMapper.mapToActionTypeProto(
                        action.actionType(), action.value());

                DeviceActionProto deviceAction = DeviceActionProto.newBuilder()
                        .setSensorId(sensorIdString)
                        .setType(actionTypeProto)
                        .setValue(action.value())
                        .build();

                DeviceActionRequest request = DeviceActionRequest.newBuilder()
                        .setHubId(hubId)
                        .setScenarioName(scenarioName)
                        .setAction(deviceAction)
                        .setTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
                        .build();

                Empty response = hubRouterClient.handleDeviceAction(request);

                log.info("Команда отправлена: хаб={}, сценарий={}, устройство={}, действие={}, значение={}",
                        hubId, scenarioName, action.sensorId(), action.actionType(), action.value());

            } catch (StatusRuntimeException e) {
                log.error("Ошибка отправки команды через gRPC для устройства {}: {}",
                        action.sensorId(), e.getStatus(), e);
            } catch (Exception e) {
                log.error("Неожиданная ошибка при отправке команды для устройства {}",
                        action.sensorId(), e);
            }
        }
    }
}
