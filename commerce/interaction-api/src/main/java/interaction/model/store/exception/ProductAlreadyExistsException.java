package interaction.model.store.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String productName) {
        super(String.format("Товар с наименованием '%s' уже существует", productName));
    }
}
