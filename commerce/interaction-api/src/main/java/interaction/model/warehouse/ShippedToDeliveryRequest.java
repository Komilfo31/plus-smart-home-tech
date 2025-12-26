package interaction.model.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ShippedToDeliveryRequest {
    @NotNull
    private final UUID orderId;

    @NotNull
    private final UUID deliveryId;
}
