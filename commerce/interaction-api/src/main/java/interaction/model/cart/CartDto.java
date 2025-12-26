package interaction.model.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CartDto {
    private final UUID shoppingCartId;
    private final Map<UUID, Long> products;
}
