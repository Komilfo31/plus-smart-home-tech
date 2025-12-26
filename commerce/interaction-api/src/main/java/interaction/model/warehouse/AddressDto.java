package interaction.model.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AddressDto {
    private final String country;
    private final String city;
    private final String street;
    private final String house;
    private final String flat;
}
