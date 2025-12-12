package ru.yandex.practicum.commerce.cart.service;

import interaction.client.WarehouseFeignClient;
import interaction.model.cart.CartDto;
import interaction.model.cart.ChangeProductQuantityRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.cart.model.ShoppingCartState;
import ru.yandex.practicum.commerce.cart.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository repository;
    private final WarehouseFeignClient warehouseClient;

    @Override
    public CartDto getShoppingCart(String username) {
        log.debug("Получение корзины для пользователя: {}", username);
        ShoppingCart cart = findExistingCart(username);
        return new CartDto(cart.getShoppingCartId(), cart.getProducts());
    }

    @Transactional
    @Override
    public CartDto addProductToCart(String username, Map<UUID, Long> addingProducts) {
        log.debug("Добавление товаров в корзину пользователя: {}, товары: {}", username, addingProducts);

        ShoppingCart cart = findExistingCart(username);

        Map<UUID, Long> productsInCart = cart.getProducts();
        addingProducts.forEach((id, quantity) -> productsInCart.merge(id, quantity, Long::sum));

        try {
            warehouseClient.checkAvailability(new CartDto(cart.getShoppingCartId(), addingProducts));
            log.debug("Проверка доступности на складе успешна для пользователя: {}", username);
        } catch (Exception e) {
            log.error("Ошибка при проверке доступности на складе для пользователя {}: {}", username, e.getMessage());
            throw new IllegalStateException("Failed to check warehouse availability: " + e.getMessage());
        }

        repository.save(cart);
        log.info("Товары успешно добавлены в корзину пользователя: {}", username);
        return new CartDto(cart.getShoppingCartId(), productsInCart);
    }

    @Transactional
    @Override
    public CartDto removeProductFromCart(String username, List<UUID> removingProducts) {
        log.debug("Удаление товаров из корзины пользователя: {}, товары: {}", username, removingProducts);

        ShoppingCart cart = findExistingCart(username);

        Map<UUID, Long> products = cart.getProducts();

        List<UUID> notFoundProducts = removingProducts.stream()
                .filter(productId -> !products.containsKey(productId))
                .toList();

        if (!notFoundProducts.isEmpty()) {
            log.warn("Товары {} не найдены в корзине пользователя {}", notFoundProducts, username);
            throw new IllegalArgumentException("Products not found in cart: " + notFoundProducts);
        }

        removingProducts.forEach(cart.getProducts()::remove);
        repository.save(cart);

        log.info("Товары успешно удалены из корзины пользователя: {}", username);
        return new CartDto(cart.getShoppingCartId(), cart.getProducts());
    }

    @Transactional
    @Override
    public CartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.debug("Изменение количества товара для пользователя: {}, запрос: {}", username, request);

        ShoppingCart cart = findExistingCart(username);

        Map<UUID, Long> products = cart.getProducts();
        UUID productId = request.getProductId();

        if (!products.containsKey(productId)) {
            log.warn("Товар {} не найден в корзине пользователя {}", productId, username);
            throw new IllegalArgumentException("Product not found in cart: " + productId);
        }

        Long newQuantity = request.getNewQuantity();
        if (newQuantity == null || newQuantity < 0) {
            log.error("Некорректное количество товара: {} для пользователя {}", newQuantity, username);
            throw new IllegalArgumentException("Quantity must be non-negative");
        }

        if (newQuantity == 0) {
            products.remove(productId);
            log.debug("Количество товара {} установлено в 0, товар удален", productId);
        } else {
            products.put(productId, newQuantity);
        }

        repository.save(cart);
        log.info("Количество товара {} изменено на {} для пользователя {}", productId, newQuantity, username);
        return new CartDto(cart.getShoppingCartId(), products);
    }

    @Transactional
    @Override
    public void deactivateShoppingCart(String username) {
        log.debug("Деактивация корзины пользователя: {}", username);

        ShoppingCart cart = findExistingCart(username);

        if (cart.getState() == ShoppingCartState.DEACTIVATED) {
            log.warn("Корзина пользователя {} уже деактивирована", username);
            return;
        }

        cart.setState(ShoppingCartState.DEACTIVATED);
        repository.save(cart);
        log.info("Корзина пользователя {} деактивирована", username);
    }

    private ShoppingCart findExistingCart(String username) {
        return repository.findByUsername(username).orElseGet(() -> {
            log.debug("Создание новой корзины для пользователя: {}", username);
            ShoppingCart newCart = createNewCart(username);
            return repository.save(newCart);
        });
    }

    private ShoppingCart createNewCart(String username) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUsername(username);
        cart.setShoppingCartId(UUID.randomUUID());
        cart.setProducts(new HashMap<>());
        cart.setState(ShoppingCartState.ACTIVE);
        return cart;
    }
}
