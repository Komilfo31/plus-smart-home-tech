package interaction.model.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DimensionDto {
    private final double width;
    private final double height;
    private final double depth;
}
