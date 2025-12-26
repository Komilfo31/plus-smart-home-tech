package ru.yandex.practicum.commerce.delivery.controller;

import interaction.client.DeliveryFeignClient;
import interaction.model.delivery.DeliveryDto;
import interaction.model.order.OrderDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;

import java.util.UUID;

@RequestMapping("/api/v1/delivery")
@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryFeignClient {
    private final DeliveryService deliveryService;

    @Override
    @PutMapping
    public DeliveryDto addDelivery(@Valid @RequestBody DeliveryDto delivery) {
        return deliveryService.addDelivery(delivery);
    }

    @Override
    @PostMapping("/successful")
    public void simulateSuccessfulDelivery(@RequestBody UUID orderId) {
        deliveryService.simulateSuccessfulDelivery(orderId);
    }

    @Override
    @PostMapping("/picked")
    public void simulateDeliveryReceived(@RequestBody UUID orderId) {
        deliveryService.simulateDeliveryReceived(orderId);
    }

    @Override
    @PostMapping("/failed")
    public void simulateDeliveryFailed(@RequestBody UUID orderId) {
        deliveryService.simulateDeliveryFailed(orderId);
    }

    @Override
    @PostMapping("/cost")
    public Double calculateDeliveryCost(@RequestBody OrderDto order) {
        return deliveryService.calculateDeliveryCost(order);
    }
}
