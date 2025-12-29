package interaction.model.warehouse;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AssemblyProductsForOrderRequest {
    @NotEmpty
    private final Map<UUID, Long> products;

    @NotNull
    private final UUID orderId;
}
