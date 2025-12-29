package interaction.client;

import interaction.model.cart.CartDto;
import interaction.model.cart.ChangeProductQuantityRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartFeignClient {

    @GetMapping
    CartDto getShoppingCart(@Valid @NotEmpty @RequestParam String userName);

    @PutMapping
    CartDto addProduct(@Valid @NotEmpty @RequestParam String username,
                               @RequestBody Map<UUID, Long> productsToAdd);

    @DeleteMapping
    void deactivateShoppingCart(@Valid @NotEmpty @RequestParam String username);

    @PostMapping("/remove")
    CartDto removeProducts(@Valid @NotEmpty @RequestParam String username,
                                   @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    CartDto changeProductQuantity(@Valid @NotEmpty @RequestParam String username,
                                          @RequestBody ChangeProductQuantityRequest request);
}
