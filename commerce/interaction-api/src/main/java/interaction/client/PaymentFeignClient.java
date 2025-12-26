package interaction.client;

import interaction.model.order.OrderDto;
import interaction.model.payment.PaymentDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentFeignClient {

    @PostMapping
    PaymentDto createPayment(@Valid @RequestBody OrderDto order);

    @PostMapping("/totalCost")
    Double calculateTotalCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/refund")
    void simulateSuccessfulPayment(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    Double calculateProductCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/failed")
    void simulateFailedPayment(@RequestBody UUID paymentId);
}
