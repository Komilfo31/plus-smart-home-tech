package interaction.model.delivery;

import interaction.model.warehouse.AddressDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryDto {
    @NotNull
    private final UUID deliveryId;

    @NotNull
    private final AddressDto fromAddress;

    @NotNull
    private final AddressDto toAddress;

    @NotNull
    private final UUID orderId;

    @NotNull
    private final DeliveryState deliveryState;
}
