package ru.yandex.practicum.commerce.store.service;

import interaction.model.store.dto.ProductDto;
import interaction.model.store.enums.ProductCategory;
import interaction.model.store.enums.ProductState;
import interaction.model.store.enums.QuantityState;
import interaction.model.store.exception.ProductNotActiveException;
import interaction.model.store.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.store.model.Product;
import ru.yandex.practicum.commerce.store.repository.ProductRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        log.debug("Получение товаров категории {} с пагинацией {}", category, pageable);

        ProductCategory domainCategory =
                ProductCategory.valueOf(category.name());

        Page<Product> products = productRepository.findByProductCategoryAndProductState(
                domainCategory,
                ProductState.ACTIVE,
                pageable
        );

        return products.map(productMapper::productToProductDto);
    }

    @Override
    public ProductDto getProductById(UUID id) {
        log.debug("Получение товара по ID: {}", id);

        return productRepository.findById(id)
                .map(productMapper::productToProductDto)
                .orElseThrow(() -> new ProductNotFoundException("Товар с ID " + id + " не найден"));
    }


    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        log.debug("Создание нового товара: {}", productDto.getProductName());

        Product product = productMapper.productDtoToProduct(productDto);

        product.setProductState(ProductState.ACTIVE);

        product.setProductId(productDto.getProductId());

        Product savedProduct = productRepository.save(product);
        log.info("Создан товар с ID: {}, наименованием: {}",
                savedProduct.getProductId(), savedProduct.getProductName());

        return productMapper.productToProductDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        log.debug("Полное обновление товара с ID: {}", productDto.getProductId());

        if (productDto.getProductId() == null) {
            throw new IllegalArgumentException("ID товара обязателен для обновления");
        }

        Product existingProduct = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(productDto.getProductId()));

        if (existingProduct.getProductState() != ProductState.ACTIVE) {
            throw new ProductNotActiveException(productDto.getProductId());
        }

        Product updatedProduct = productMapper.productDtoToProduct(productDto);

        updatedProduct.setProductId(existingProduct.getProductId());

        Product savedProduct = productRepository.save(updatedProduct);
        log.info("Полностью обновлен товар с ID: {}", savedProduct.getProductId());

        return productMapper.productToProductDto(savedProduct);
    }

    @Override
    @Transactional
    public boolean deleteProductById(UUID id) {
        int updated = productRepository.updateProductState(id, ProductState.DEACTIVATE);
        log.debug("Updated {} records for productId: {}", updated, id);
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean updateQuantity(UUID id, QuantityState state) {
        log.debug("Обновление статуса количества для товара с ID: {}", id);

        Product product = productRepository.findActiveById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        QuantityState domainState =
                QuantityState.valueOf(state.name());

        product.setQuantityState(domainState);
        productRepository.save(product);

        log.info("Статус количества товара с ID {} изменен на {}", id, state);
        return true;
    }
}
