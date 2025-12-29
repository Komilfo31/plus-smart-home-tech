package ru.yandex.practicum.commerce.warehouse.controller;

import interaction.client.WarehouseFeignClient;
import interaction.model.cart.CartDto;
import interaction.model.warehouse.AddProductToWarehouseRequest;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.AssemblyProductsForOrderRequest;
import interaction.model.warehouse.BookedProductDto;
import interaction.model.warehouse.NewProductInWarehouseRequest;
import interaction.model.warehouse.ShippedToDeliveryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseFeignClient {

    private final WarehouseService service;

    @Override
    @PutMapping
    public void registerNewProduct(@RequestBody NewProductInWarehouseRequest request) {
        service.addNewProduct(request);
    }

    @Override
    @PostMapping("/check")
    public BookedProductDto checkAvailability(CartDto cart) {
        return service.bookProduct(cart);
    }

    @Override
    @PostMapping("/add")
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        service.addQuantity(request);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return service.getCurrentAddress();
    }

    @Override
    @PostMapping("/assembly")
    public BookedProductDto assemblyProductsForOrder(
            @Valid @RequestBody AssemblyProductsForOrderRequest request) {
        return service.assemblyProductsForOrder(request);
    }

    @Override
    @PostMapping("/shipped")
    public void shippedToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request) {
        service.shippedToDelivery(request);
    }

    @Override
    @PostMapping("/return")
    public void acceptReturn(@RequestBody Map<UUID, Long> productsToReturn) {
        service.acceptReturn(productsToReturn);
    }
}
