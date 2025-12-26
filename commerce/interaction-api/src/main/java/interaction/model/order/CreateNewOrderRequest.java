package interaction.model.order;

import interaction.model.cart.CartDto;
import interaction.model.warehouse.AddressDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateNewOrderRequest {
    @NotNull
    private final CartDto cartDto;

    @NotNull
    private final AddressDto addressDto;
}
