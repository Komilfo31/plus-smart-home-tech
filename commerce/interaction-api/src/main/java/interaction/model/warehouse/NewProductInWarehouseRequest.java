package interaction.model.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class NewProductInWarehouseRequest {
    private final UUID productId;
    private final boolean fragile;
    private final DimensionDto dimension;
    private final double weight;
}
