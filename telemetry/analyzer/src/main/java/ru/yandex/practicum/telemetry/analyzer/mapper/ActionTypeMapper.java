package ru.yandex.practicum.telemetry.analyzer.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;

@Component
public class ActionTypeMapper {

    public ActionTypeProto mapToActionTypeProto(String actionType, int value) {
        if (actionType == null) {
            return ActionTypeProto.SET_VALUE;
        }

        String upperActionType = actionType.toUpperCase();

        switch (upperActionType) {
            case "SWITCH_ON":
            case "ACTIVATE":
            case "TURN_ON":
            case "ON":
            case "ENABLE":
                return ActionTypeProto.ACTIVATE;

            case "SWITCH_OFF":
            case "DEACTIVATE":
            case "TURN_OFF":
            case "OFF":
            case "DISABLE":
                return ActionTypeProto.DEACTIVATE;

            case "TOGGLE":
            case "INVERSE":
            case "SWITCH":
            case "FLIP":
                return ActionTypeProto.INVERSE;

            case "SET_VALUE":
            case "SET_TEMPERATURE":
            case "SET_HUMIDITY":
            case "SET_LIGHT":
            case "SET_BRIGHTNESS":
            case "ADJUST":
            case "TEMPERATURE":
            case "HUMIDITY":
            case "LIGHT":
            case "BRIGHTNESS":
            case "VALUE":
                return ActionTypeProto.SET_VALUE;

            default:
                return ActionTypeProto.SET_VALUE;
        }
    }
}
