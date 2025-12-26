package ru.yandex.practicum.commerce.payment.service;

import interaction.client.DeliveryFeignClient;
import interaction.client.OrderFeignClient;
import interaction.client.StoreFeignClient;
import interaction.model.order.OrderDto;
import interaction.model.payment.PaymentDto;
import interaction.model.store.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.model.PaymentStatus;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final OrderFeignClient orderClient;
    private final StoreFeignClient storeClient;
    private final DeliveryFeignClient deliveryClient;

    private static final double TAX_RATE = 0.1;

    @Transactional
    @Override
    public PaymentDto createPayment(OrderDto order) {
        log.info("Создание платежа для заказа: {}", order.getOrderId());

        orderClient.getById(order.getOrderId());

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .orderId(order.getOrderId())
                .totalPayment(order.getTotalPrice())
                .deliveryTotal(order.getDeliveryPrice())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = repository.save(payment);
        log.info("Платеж создан с ID: {} для заказа: {}", savedPayment.getPaymentId(), order.getOrderId());
        return mapper.toDto(savedPayment);
    }

    @Override
    public Double calculateTotalCost(OrderDto order) {
        log.debug("Расчет общей стоимости для заказа: {}", order.getOrderId());

        Double productPrice = calculateProductCost(order);
        Double productTax = productPrice * TAX_RATE;
        Double deliveryPrice = deliveryClient.calculateDeliveryCost(order);

        Double totalCost = productPrice + productTax + deliveryPrice;
        log.debug("Общая стоимость рассчитана для заказа {}: {}", order.getOrderId(), totalCost);
        return totalCost;
    }

    @Transactional
    @Override
    public void simulateSuccessfulPayment(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        validatePaymentNotProcessed(payment);

        payment.setStatus(PaymentStatus.SUCCESS);
        Payment updatedPayment = repository.save(payment);

        orderClient.paymentOrder(updatedPayment.getOrderId());
        log.info("Платеж {} отмечен как УСПЕШНЫЙ для заказа: {}", paymentId, updatedPayment.getOrderId());
    }

    @Transactional
    @Override
    public void simulateFailedPayment(UUID paymentId) {
        Payment payment = getPayment(paymentId);
        validatePaymentNotProcessed(payment);

        payment.setStatus(PaymentStatus.FAILED);
        Payment updatedPayment = repository.save(payment);

        orderClient.paymentFailedOrder(updatedPayment.getOrderId());
        log.info("Платеж {} отмечен как НЕУСПЕШНЫЙ для заказа: {}", paymentId, updatedPayment.getOrderId());
    }

    @Override
    public Double calculateProductCost(OrderDto order) {
        log.debug("Расчет стоимости товаров для заказа: {}", order.getOrderId());

        Map<UUID, Long> productsInOrder = order.getProducts();
        validateProductsNotEmpty(productsInOrder);

        BigDecimal totalProductCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Long> entry : productsInOrder.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            ProductDto product = storeClient.getProduct(productId);
            validateProductExists(productId, product);

            BigDecimal productPrice = product.getPrice();
            BigDecimal productQuantity = BigDecimal.valueOf(quantity);

            BigDecimal productTotal = productPrice.multiply(productQuantity);
            totalProductCost = totalProductCost.add(productTotal);
        }

        log.debug("Стоимость товаров рассчитана для заказа {}: {}", order.getOrderId(), totalProductCost);
        return totalProductCost.doubleValue();
    }

    private Payment getPayment(UUID paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("Платеж не найден с ID: {}", paymentId);
                    return new IllegalArgumentException("Платеж с id " + paymentId + " не найден");
                });
    }

    private void validatePaymentNotProcessed(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.error("Платеж {} уже обработан со статусом: {}", payment.getPaymentId(), payment.getStatus());
            throw new IllegalStateException(
                    String.format("Платеж %s уже обработан со статусом: %s",
                            payment.getPaymentId(), payment.getStatus())
            );
        }
    }

    private void validateProductsNotEmpty(Map<UUID, Long> products) {
        if (products == null || products.isEmpty()) {
            log.error("Пустой список товаров в заказе");
            throw new IllegalArgumentException("Заказ должен содержать хотя бы один товар");
        }
    }

    private void validateProductExists(UUID productId, ProductDto product) {
        if (product == null) {
            log.error("Товар не найден: {}", productId);
            throw new IllegalArgumentException("Товар не найден: " + productId);
        }
    }
}
