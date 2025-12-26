package interaction.model.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChangeProductQuantityRequest {
    private final UUID productId;
    private final Long newQuantity;
}
