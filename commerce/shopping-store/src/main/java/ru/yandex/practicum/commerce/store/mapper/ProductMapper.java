package ru.yandex.practicum.commerce.store.mapper;

import interaction.model.store.dto.ProductDto;
import interaction.model.store.enums.ProductCategory;
import interaction.model.store.enums.ProductState;
import interaction.model.store.enums.QuantityState;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.store.model.Product;

@Component
public class ProductMapper {

    public ProductDto productToProductDto(Product product) {
        if (product == null) {
            return null;
        }

        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .quantityState(mapQuantityState(product.getQuantityState()))
                .productState(mapProductState(product.getProductState()))
                .productCategory(mapProductCategory(product.getProductCategory()))
                .price(product.getPrice())
                .build();
    }

    public Product productDtoToProduct(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }

        validateProductDto(productDto);

        return Product.builder()
                .productId(productDto.getProductId())
                .productName(productDto.getProductName())
                .description(productDto.getDescription())
                .imageSrc(productDto.getImageSrc())
                .quantityState(mapDomainQuantityState(productDto.getQuantityState()))
                .productState(ProductState.DEACTIVATE)
                .productCategory(mapDomainProductCategory(productDto.getProductCategory()))
                .price(productDto.getPrice())
                .build();
    }

    private QuantityState mapQuantityState(
            QuantityState domainState) {
        return domainState != null ?
                QuantityState.valueOf(domainState.name()) :
                null;
    }

    private QuantityState mapDomainQuantityState(
            QuantityState dtoState) {
        return dtoState != null ?
                QuantityState.valueOf(dtoState.name()) :
                null;
    }

    private ProductState mapProductState(
            ProductState domainState) {
        return domainState != null ?
                ProductState.valueOf(domainState.name()) :
                null;
    }

    private ProductCategory mapProductCategory(
            ProductCategory domainCategory) {
        return domainCategory != null ?
                ProductCategory.valueOf(domainCategory.name()) :
                null;
    }

    private ProductCategory mapDomainProductCategory(
            ProductCategory dtoCategory) {
        return dtoCategory != null ?
                ProductCategory.valueOf(dtoCategory.name()) :
                null;
    }

    private void validateProductDto(ProductDto productDto) {
        if (productDto.getProductName() == null || productDto.getProductName().isBlank()) {
            throw new IllegalArgumentException("Наименование товара обязательно");
        }
        if (productDto.getDescription() == null || productDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание товара обязательно");
        }
        if (productDto.getPrice() == null || productDto.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной");
        }
        if (productDto.getProductCategory() == null) {
            throw new IllegalArgumentException("Категория товара обязательна");
        }
    }
}
