package interaction.model.store.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID productId) {
        super(String.format("Товар с ID '%s' не найден", productId));
    }

    public ProductNotFoundException(UUID productId, String message) {
        super(String.format("Товар с ID '%s' не найден: %s", productId, message));
    }

    public ProductNotFoundException(String message) { // Для универсальности
        super(message);
    }
}
