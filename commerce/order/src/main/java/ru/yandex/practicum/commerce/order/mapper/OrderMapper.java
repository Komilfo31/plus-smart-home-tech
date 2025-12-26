package ru.yandex.practicum.commerce.order.mapper;

import interaction.model.order.OrderDto;
import interaction.model.order.OrderState;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.order.model.Order;

import java.util.HashMap;

@Component
public class OrderMapper {

    public OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        return OrderDto.builder()
                .orderId(order.getOrderId())
                .state(order.getState().name())
                .products(new HashMap<>(order.getProducts()))
                .shoppingCartId(order.getShoppingCartId())
                .deliveryId(order.getDeliveryId())
                .paymentId(order.getPaymentId())
                .deliveryVolume(order.getDeliveryVolume())
                .deliveryWeight(order.getDeliveryWeight())
                .fragile(order.getFragile())
                .totalPrice(order.getTotalPrice())
                .productPrice(order.getProductPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .build();
    }

    public Order toEntity(OrderDto orderDto) {
        if (orderDto == null) {
            return null;
        }

        Order order = new Order();
        order.setOrderId(orderDto.getOrderId());
        order.setState(OrderState.valueOf(orderDto.getState()));
        order.setProducts(new HashMap<>(orderDto.getProducts()));
        order.setShoppingCartId(orderDto.getShoppingCartId());
        order.setDeliveryId(orderDto.getDeliveryId());
        order.setPaymentId(orderDto.getPaymentId());
        order.setDeliveryVolume(orderDto.getDeliveryVolume());
        order.setDeliveryWeight(orderDto.getDeliveryWeight());
        order.setFragile(orderDto.isFragile());
        order.setTotalPrice(orderDto.getTotalPrice());
        order.setProductPrice(orderDto.getProductPrice());
        order.setDeliveryPrice(orderDto.getDeliveryPrice());

        return order;
    }
}
