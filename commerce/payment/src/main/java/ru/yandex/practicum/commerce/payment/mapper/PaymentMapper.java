package ru.yandex.practicum.commerce.payment.mapper;

import interaction.model.payment.PaymentDto;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.model.PaymentStatus;

@Component
public class PaymentMapper {
    public PaymentDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .totalPayment(payment.getTotalPayment())
                .deliveryTotal(payment.getDeliveryTotal())
                .feeTotal(calculateFee(payment))
                .build();
    }

    public Payment toEntity(PaymentDto paymentDto) {
        if (paymentDto == null) {
            return null;
        }

        return Payment.builder()
                .paymentId(paymentDto.getPaymentId())
                .totalPayment(paymentDto.getTotalPayment())
                .deliveryTotal(paymentDto.getDeliveryTotal())
                .status(PaymentStatus.PENDING)
                .build();
    }

    //вычисляю комиссию
    private Double calculateFee(Payment payment) {
        if (payment == null || payment.getTotalPayment() == null) {
            return 0.0;
        }

        Double baseAmount = payment.getTotalPayment();
        Double deliveryTotal = payment.getDeliveryTotal() != null ? payment.getDeliveryTotal() : 0.0;
        Double taxableAmount = baseAmount - deliveryTotal;

        return taxableAmount * 0.1;
    }
}
