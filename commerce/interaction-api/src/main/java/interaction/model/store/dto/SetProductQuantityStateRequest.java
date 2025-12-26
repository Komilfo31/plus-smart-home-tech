package interaction.model.store.dto;

import interaction.model.store.enums.QuantityState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    private final UUID productId;
    private final QuantityState quantityState;
}
