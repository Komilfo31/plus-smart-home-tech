package interaction.model.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class ProductReturnRequest {
    private final UUID orderId;

    @NotNull
    private final Map<UUID, Long> products;
}
