package ru.yandex.practicum.commerce.delivery.repository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.delivery.model.Delivery;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    Optional<Delivery> findByOrderId(UUID orderId);

    default Delivery findByOrderIdOrThrow(UUID orderId) {
        return findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Доставка для заказа " + orderId + " не найдена"
                ));
    }
}
