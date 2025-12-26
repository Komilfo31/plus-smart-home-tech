package ru.yandex.practicum.commerce.delivery.service;

import interaction.model.delivery.DeliveryDto;
import interaction.model.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto addDelivery(DeliveryDto delivery);

    void simulateSuccessfulDelivery(UUID orderId);

    void simulateDeliveryReceived(UUID orderId);

    void simulateDeliveryFailed(UUID orderId);

    Double calculateDeliveryCost(OrderDto order);
}
