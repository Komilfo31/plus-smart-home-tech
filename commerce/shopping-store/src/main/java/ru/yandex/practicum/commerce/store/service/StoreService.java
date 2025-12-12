package ru.yandex.practicum.commerce.store.service;

import interaction.model.store.dto.ProductDto;
import interaction.model.store.enums.ProductCategory;
import interaction.model.store.enums.QuantityState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StoreService {

    Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable);

    ProductDto getProductById(UUID id);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean deleteProductById(UUID id);

    boolean updateQuantity(UUID id, QuantityState state);

}