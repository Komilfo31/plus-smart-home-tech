package ru.yandex.practicum.commerce.store.model;

import interaction.model.store.enums.ProductCategory;
import interaction.model.store.enums.ProductState;
import interaction.model.store.enums.QuantityState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "ProductBuilder", toBuilder = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_src", length = 255)
    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", nullable = false, length = 32)
    @Builder.Default
    private QuantityState quantityState = QuantityState.ENOUGH;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", nullable = false, length = 32)
    @Builder.Default
    private ProductState productState = ProductState.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false, length = 32)
    private ProductCategory productCategory;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
