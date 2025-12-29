package ru.yandex.practicum.commerce.payment.controller;

import interaction.client.PaymentFeignClient;
import interaction.model.order.OrderDto;
import interaction.model.payment.PaymentDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.payment.service.PaymentService;

import java.util.UUID;

@RequestMapping("/api/v1/payment")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentFeignClient {

    private final PaymentService service;

    @Override
    @PostMapping
    public PaymentDto createPayment(@Valid @RequestBody OrderDto order) {
        return service.createPayment(order);
    }

    @Override
    @PostMapping("/totalCost")
    public Double calculateTotalCost(@Valid @RequestBody OrderDto order) {
        return service.calculateTotalCost(order);
    }

    @Override
    @PostMapping("/refund")
    public void simulateSuccessfulPayment(@RequestBody UUID paymentId) {
        service.simulateSuccessfulPayment(paymentId);
    }

    @Override
    @PostMapping("/failed")
    public void simulateFailedPayment(@RequestBody UUID paymentId) {
        service.simulateFailedPayment(paymentId);
    }

    @Override
    @PostMapping("/productCost")
    public Double calculateProductCost(@Valid @RequestBody OrderDto order) {
        return service.calculateProductCost(order);
    }
}
