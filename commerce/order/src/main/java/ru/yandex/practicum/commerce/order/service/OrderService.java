package ru.yandex.practicum.commerce.order.service;

import interaction.model.order.CreateNewOrderRequest;
import interaction.model.order.OrderDto;
import interaction.model.order.ProductReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    Page<OrderDto> getOrders(String username, Pageable pageable);

    OrderDto addOrder(CreateNewOrderRequest request);

    OrderDto returnProductsFromOrder(ProductReturnRequest request);

    OrderDto paymentOrder(UUID orderId);

    OrderDto paymentFailedOrder(UUID orderId);

    OrderDto deliveryOrder(UUID orderId);

    OrderDto deliveryFailedOrder(UUID orderId);

    OrderDto completedOrder(UUID orderId);

    OrderDto calculateTotalPayment(UUID orderId);

    OrderDto calculateDelivery(UUID orderId);

    OrderDto assemblyOrder(UUID orderId);

    OrderDto assemblyFailedOrder(UUID orderId);

    OrderDto getById(UUID orderId);
}
