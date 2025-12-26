package interaction.model.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookedProductDto {
    private final double deliveryWeight;
    private final double deliveryVolume;
    private final boolean fragile;
}
