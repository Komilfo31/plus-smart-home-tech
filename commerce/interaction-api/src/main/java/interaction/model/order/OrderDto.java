package interaction.model.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderDto {
    @NotNull
    private final UUID orderId;

    private final UUID shoppingCartId;

    @NotNull
    private final Map<UUID, Long> products;

    private final UUID paymentId;

    private final UUID deliveryId;

    private final String state;

    private final Double deliveryWeight;

    private final Double deliveryVolume;

    private final boolean fragile;

    private final Double totalPrice;

    private final Double deliveryPrice;

    private final Double productPrice;
}
