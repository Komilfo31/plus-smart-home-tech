package interaction.client;

import interaction.model.cart.CartDto;
import interaction.model.warehouse.AddProductToWarehouseRequest;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.BookedProductDto;
import interaction.model.warehouse.NewProductInWarehouseRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class WarehouseClientFallbackFactory implements WarehouseFeignClient {

    @Override
    public void registerNewProduct(NewProductInWarehouseRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public BookedProductDto checkAvailability(CartDto cart) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }
}
