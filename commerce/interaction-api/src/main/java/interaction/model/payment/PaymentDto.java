package interaction.model.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PaymentDto {
    private final UUID paymentId;
    private final Double totalPayment;
    private final Double deliveryTotal;
    private final Double feeTotal;
}
