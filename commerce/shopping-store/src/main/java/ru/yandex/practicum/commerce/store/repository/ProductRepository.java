package ru.yandex.practicum.commerce.store.repository;

import interaction.model.store.enums.ProductCategory;
import interaction.model.store.enums.ProductState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.store.model.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Transactional
    @Modifying
    @Query("update Product p set p.productState = :state where p.productId = :productId")
    int updateProductState(UUID productId, ProductState state);

    @Query("SELECT p FROM Product p WHERE p.productId = :id AND p.productState = 'ACTIVE'")
    Optional<Product> findActiveById(@Param("id") UUID id);

    Page<Product> findByProductCategoryAndProductState(
            ProductCategory category,
            ProductState state,
            Pageable pageable
    );
}
