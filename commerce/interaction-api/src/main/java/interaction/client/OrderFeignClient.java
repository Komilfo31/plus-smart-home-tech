package interaction.client;

import interaction.model.order.CreateNewOrderRequest;
import interaction.model.order.OrderDto;
import interaction.model.order.ProductReturnRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/shopping-store")
public interface OrderFeignClient {
    @GetMapping
    Page<OrderDto> getOrders(@Valid @NotEmpty @RequestParam String username, Pageable pageable);

    @PutMapping
    OrderDto addOrder(@Valid @RequestBody CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto returnProducts(@Valid @RequestBody ProductReturnRequest returnProducts);

    @PostMapping("/payment")
    OrderDto paymentOrder(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentFailedOrder(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto deliveryOrder(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailedOrder(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto completedOrder(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalPayment(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDelivery(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailedOrder(@RequestBody UUID orderId);

    @GetMapping("{orderId}")
    OrderDto getById(@PathVariable UUID orderId);
}
