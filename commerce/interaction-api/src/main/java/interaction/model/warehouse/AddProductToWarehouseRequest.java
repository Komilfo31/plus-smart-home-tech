package interaction.model.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AddProductToWarehouseRequest {
    private final UUID productId;
    private final long quantity;
}
