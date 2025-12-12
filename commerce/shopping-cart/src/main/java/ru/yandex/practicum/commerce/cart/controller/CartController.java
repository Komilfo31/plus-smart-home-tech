package ru.yandex.practicum.commerce.cart.controller;

import interaction.client.CartFeignClient;
import interaction.model.cart.CartDto;
import interaction.model.cart.ChangeProductQuantityRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/api/v1/shopping-cart")
@RestController
@RequiredArgsConstructor
public class CartController implements CartFeignClient {

    private final ShoppingCartService service;

    @Override
    @GetMapping
    public CartDto getShoppingCart(@Valid @NotEmpty @RequestParam String username) {
        return service.getShoppingCart(username);
    }

    @Override
    @PutMapping
    public CartDto addProduct(@Valid @NotEmpty @RequestParam String username,
                                      @RequestBody Map<UUID, Long> productsToAdd) {
        return service.addProductToCart(username, productsToAdd);
    }

    @Override
    @DeleteMapping
    public void deactivateShoppingCart(@Valid @NotEmpty @RequestParam String username) {
        service.deactivateShoppingCart(username);
    }

    @Override
    @PostMapping("/remove")
    public CartDto removeProducts(@Valid @NotEmpty String username,
                                          @RequestBody List<UUID> productIds) {
        return service.removeProductFromCart(username, productIds);
    }

    @Override
    @PostMapping("/change-quantity")
    public CartDto changeProductQuantity(@Valid @NotEmpty String username,
                                                 @RequestBody ChangeProductQuantityRequest request) {
        return service.changeProductQuantity(username, request);
    }
}
