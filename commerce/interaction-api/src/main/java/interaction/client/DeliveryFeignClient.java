package interaction.client;

import interaction.model.delivery.DeliveryDto;
import interaction.model.order.OrderDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryFeignClient {

    @PutMapping
    DeliveryDto addDelivery(@Valid @RequestBody DeliveryDto delivery);

    @PostMapping("/successful")
    void simulateSuccessfulDelivery(@RequestBody UUID orderId);

    @PostMapping("/picked")
    void simulateDeliveryReceived(@RequestBody UUID orderId);

    @PostMapping("/failed")
    void simulateDeliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/cost")
    Double calculateDeliveryCost(@Valid @RequestBody OrderDto order);
}
