package ru.yandex.practicum.commerce.cart.service;

import interaction.model.cart.CartDto;
import interaction.model.cart.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    CartDto getShoppingCart(String username);

    CartDto addProductToCart(String username, Map<UUID, Long> productsToAdd);

    void deactivateShoppingCart(String username);

    CartDto removeProductFromCart(String username, List<UUID> productIds);

    CartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}
