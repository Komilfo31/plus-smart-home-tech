package ru.yandex.practicum.commerce.payment.service;

import interaction.model.order.OrderDto;
import interaction.model.payment.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto order);

    Double calculateTotalCost(OrderDto order);

    void simulateSuccessfulPayment(UUID paymentId);

    void simulateFailedPayment(UUID paymentId);

    Double calculateProductCost(OrderDto order);
}
