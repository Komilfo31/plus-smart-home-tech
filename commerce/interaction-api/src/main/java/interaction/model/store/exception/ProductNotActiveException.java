package interaction.model.store.exception;

import java.util.UUID;

public class ProductNotActiveException extends RuntimeException {
    public ProductNotActiveException(UUID productId) {
        super(String.format("Товар с ID '%s' не активен", productId));
    }
}