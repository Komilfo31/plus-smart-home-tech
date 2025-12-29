package interaction.model.store.dto;

import interaction.model.store.enums.ProductCategory;
import interaction.model.store.enums.ProductState;
import interaction.model.store.enums.QuantityState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ProductDto {
    private final UUID productId;
    private final String productName;
    private final String description;
    private final String imageSrc;
    private final QuantityState quantityState;
    private final ProductState productState;
    private final ProductCategory productCategory;
    private final BigDecimal price; //BigDecimal для избежания ошибок округления
}
