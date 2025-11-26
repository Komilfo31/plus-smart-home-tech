package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.handler.hub.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.handler.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class EventGrpcController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public EventGrpcController(Set<SensorEventHandler> sensorEventHandlers,
                               Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getMessageType,
                        Function.identity()
                ));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getMessageType,
                        Function.identity()
                ));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request,
                                   StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получено gRPC событие датчика: {}, тип: {}", request.getId(), request.getPayloadCase());

            SensorEventHandler handler = sensorEventHandlers.get(request.getPayloadCase());
            if (handler == null) {
                throw new IllegalArgumentException("Не найден обработчик для типа события: " + request.getPayloadCase());
            }

            handler.handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            log.debug("Событие датчика {} успешно обработано", request.getId());

        } catch (Exception e) {
            log.error("Ошибка обработки gRPC события датчика: {}", request.getId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription("Ошибка обработки события: " + e.getMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request,
                                StreamObserver<Empty> responseObserver) {
        try {
            log.info("Получено gRPC событие хаба: {}, тип: {}", request.getHubId(), request.getPayloadCase());

            HubEventHandler handler = hubEventHandlers.get(request.getPayloadCase());
            if (handler == null) {
                throw new IllegalArgumentException("Не найден обработчик для типа события: " + request.getPayloadCase());
            }

            handler.handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

            log.debug("Событие хаба {} успешно обработано", request.getHubId());

        } catch (Exception e) {
            log.error("Ошибка обработки gRPC события хаба: {}", request.getHubId(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription("Ошибка обработки события: " + e.getMessage())
                            .withCause(e)
            ));
        }
    }
}
