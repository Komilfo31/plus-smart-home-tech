package ru.yandex.practicum.commerce.store.controller;

import interaction.client.StoreFeignClient;
import interaction.model.store.dto.ProductDto;
import interaction.model.store.enums.ProductCategory;
import interaction.model.store.enums.QuantityState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.store.service.StoreService;

import java.util.UUID;

@RequestMapping("/api/v1/shopping-store")
@RestController
@RequiredArgsConstructor
public class StoreController implements StoreFeignClient {

    private final StoreService service;

    @Override
    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable) {
        return service.getProductsByCategory(category, pageable);
    }

    @Override
    @PutMapping
    public ProductDto createNewProduct(@RequestBody ProductDto dto) {
        return service.createProduct(dto);
    }

    @Override
    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto dto) {
        return service.updateProduct(dto);
    }

    @Override
    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody UUID productId) {
        return service.deleteProductById(productId);
    }

    @PostMapping("/quantityState")
    @Override
    public boolean setProductQuantityState(@RequestParam UUID productId,
                                           @RequestParam String quantityState) {
        return service.updateQuantity(productId, QuantityState.valueOf(quantityState));
    }

    @Override
    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        return service.getProductById(productId);
    }
}
