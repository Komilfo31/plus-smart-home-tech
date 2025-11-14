package ru.yandex.practicum.telemetry.collector.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class ProtoToAvroMapper {

    public SensorEventAvro toAvro(SensorEventProto proto) {
        if (proto == null) {
            return null;
        }

        SensorEventAvro.Builder builder = SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(convertTimestamp(proto.getTimestamp()));

        switch (proto.getPayloadCase()) {
            case MOTION_SENSOR:
                builder.setPayload(toAvro(proto.getMotionSensor()));
                break;
            case LIGHT_SENSOR:
                builder.setPayload(toAvro(proto.getLightSensor()));
                break;
            case CLIMATE_SENSOR:
                builder.setPayload(toAvro(proto.getClimateSensor()));
                break;
            case SWITCH_SENSOR:
                builder.setPayload(toAvro(proto.getSwitchSensor()));
                break;
            case TEMPERATURE_SENSOR:
                builder.setPayload(toAvro(proto.getTemperatureSensor()));
                break;
            case PAYLOAD_NOT_SET:
                throw new IllegalArgumentException("Payload не установлен для события датчика: " + proto.getId());
            default:
                throw new IllegalArgumentException("Неизвестный тип payload для события датчика: " + proto.getPayloadCase());
        }

        return builder.build();
    }

    public HubEventAvro toAvro(HubEventProto proto) {
        if (proto == null) {
            return null;
        }

        HubEventAvro.Builder builder = HubEventAvro.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(convertTimestamp(proto.getTimestamp()));

        switch (proto.getPayloadCase()) {
            case DEVICE_ADDED:
                builder.setPayload(toAvro(proto.getDeviceAdded()));
                break;
            case DEVICE_REMOVED:
                builder.setPayload(toAvro(proto.getDeviceRemoved()));
                break;
            case SCENARIO_ADDED:
                builder.setPayload(toAvro(proto.getScenarioAdded()));
                break;
            case SCENARIO_REMOVED:
                builder.setPayload(toAvro(proto.getScenarioRemoved()));
                break;
            case PAYLOAD_NOT_SET:
                throw new IllegalArgumentException("Payload не установлен для события хаба: " + proto.getHubId());
            default:
                throw new IllegalArgumentException("Неизвестный тип payload для события хаба: " + proto.getPayloadCase());
        }

        return builder.build();
    }

    public MotionSensorAvro toAvro(MotionSensorProto proto) {
        if (proto == null) {
            return null;
        }

        return MotionSensorAvro.newBuilder()
                .setLinkQuality(proto.getLinkQuality())
                .setMotion(proto.getMotion())
                .setVoltage(proto.getVoltage())
                .build();
    }

    public LightSensorAvro toAvro(LightSensorProto proto) {
        if (proto == null) {
            return null;
        }

        return LightSensorAvro.newBuilder()
                .setLinkQuality(proto.getLinkQuality())
                .setLuminosity(proto.getLuminosity())
                .build();
    }

    public ClimateSensorAvro toAvro(ClimateSensorProto proto) {
        if (proto == null) {
            return null;
        }

        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(proto.getTemperatureC())
                .setHumidity(proto.getHumidity())
                .setCo2Level(proto.getCo2Level())
                .build();
    }

    public SwitchSensorAvro toAvro(SwitchSensorProto proto) {
        if (proto == null) {
            return null;
        }

        return SwitchSensorAvro.newBuilder()
                .setState(proto.getState())
                .build();
    }

    public TemperatureSensorAvro toAvro(TemperatureSensorProto proto) {
        if (proto == null) {
            return null;
        }

        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(proto.getTemperatureC())
                .setTemperatureF(proto.getTemperatureF())
                .build();
    }

    public DeviceAddedEventAvro toAvro(DeviceAddedEventProto proto) {
        if (proto == null) {
            return null;
        }

        return DeviceAddedEventAvro.newBuilder()
                .setId(proto.getId())
                .setType(mapDeviceType(proto.getType()))
                .build();
    }

    public DeviceRemovedEventAvro toAvro(DeviceRemovedEventProto proto) {
        if (proto == null) {
            return null;
        }

        return DeviceRemovedEventAvro.newBuilder()
                .setId(proto.getId())
                .build();
    }

    public ScenarioAddedEventAvro toAvro(ScenarioAddedEventProto proto) {
        if (proto == null) {
            return null;
        }

        ScenarioAddedEventAvro.Builder builder = ScenarioAddedEventAvro.newBuilder()
                .setName(proto.getName());

        if (proto.getConditionCount() > 0) {
            List<ScenarioConditionAvro> conditions = proto.getConditionList().stream()
                    .map(this::toAvro)
                    .collect(Collectors.toList());
            builder.setConditions(conditions);
        }

        if (proto.getActionCount() > 0) {
            List<DeviceActionAvro> actions = proto.getActionList().stream()
                    .map(this::toAvro)
                    .collect(Collectors.toList());
            builder.setActions(actions);
        }

        return builder.build();
    }

    public ScenarioRemovedEventAvro toAvro(ScenarioRemovedEventProto proto) {
        if (proto == null) {
            return null;
        }

        return ScenarioRemovedEventAvro.newBuilder()
                .setName(proto.getName())
                .build();
    }

    public ScenarioConditionAvro toAvro(ScenarioConditionProto proto) {
        if (proto == null) {
            return null;
        }

        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(proto.getSensorId())
                .setType(mapConditionType(proto.getType()))
                .setOperation(mapConditionOperation(proto.getOperation()));

        switch (proto.getValueCase()) {
            case BOOL_VALUE:
                builder.setValue(proto.getBoolValue() ? 1 : 0);
                break;
            case INT_VALUE:
                builder.setValue(proto.getIntValue());
                break;
            case VALUE_NOT_SET:
                break;
            default:
                log.warn("Неизвестный тип value для условия: {}", proto.getValueCase());
        }

        return builder.build();
    }

    public DeviceActionAvro toAvro(DeviceActionProto proto) {
        if (proto == null) {
            return null;
        }

        DeviceActionAvro.Builder builder = DeviceActionAvro.newBuilder()
                .setSensorId(proto.getSensorId())
                .setType(mapActionType(proto.getType()));

        if (proto.hasValue()) {
            builder.setValue(proto.getValue());
        }

        return builder.build();
    }

    private DeviceTypeAvro mapDeviceType(DeviceTypeProto protoType) {
        if (protoType == null) {
            return null;
        }

        switch (protoType) {
            case MOTION_SENSOR:
                return DeviceTypeAvro.MOTION_SENSOR;
            case TEMPERATURE_SENSOR:
                return DeviceTypeAvro.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR:
                return DeviceTypeAvro.LIGHT_SENSOR;
            case CLIMATE_SENSOR:
                return DeviceTypeAvro.CLIMATE_SENSOR;
            case SWITCH_SENSOR:
                return DeviceTypeAvro.SWITCH_SENSOR;
            default:
                throw new IllegalArgumentException("Неизвестный DeviceTypeProto: " + protoType);
        }
    }

    private ConditionTypeAvro mapConditionType(ConditionTypeProto protoType) {
        if (protoType == null) {
            return null;
        }

        switch (protoType) {
            case MOTION:
                return ConditionTypeAvro.MOTION;
            case LUMINOSITY:
                return ConditionTypeAvro.LUMINOSITY;
            case SWITCH:
                return ConditionTypeAvro.SWITCH;
            case TEMPERATURE:
                return ConditionTypeAvro.TEMPERATURE;
            case CO2LEVEL:
                return ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY:
                return ConditionTypeAvro.HUMIDITY;
            default:
                throw new IllegalArgumentException("Неизвестный ConditionTypeProto: " + protoType);
        }
    }

    private ConditionOperationAvro mapConditionOperation(ConditionOperationProto protoOperation) {
        if (protoOperation == null) {
            return null;
        }

        switch (protoOperation) {
            case EQUALS:
                return ConditionOperationAvro.EQUALS;
            case GREATER_THAN:
                return ConditionOperationAvro.GREATER_THAN;
            case LOWER_THAN:
                return ConditionOperationAvro.LOWER_THAN;
            default:
                throw new IllegalArgumentException("Неизвестный ConditionOperationProto: " + protoOperation);
        }
    }

    private ActionTypeAvro mapActionType(ActionTypeProto protoType) {
        if (protoType == null) {
            return null;
        }

        switch (protoType) {
            case ACTIVATE:
                return ActionTypeAvro.ACTIVATE;
            case DEACTIVATE:
                return ActionTypeAvro.DEACTIVATE;
            case INVERSE:
                return ActionTypeAvro.INVERSE;
            case SET_VALUE:
                return ActionTypeAvro.SET_VALUE;
            default:
                throw new IllegalArgumentException("Неизвестный ActionTypeProto: " + protoType);
        }
    }

    private Instant convertTimestamp(com.google.protobuf.Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
