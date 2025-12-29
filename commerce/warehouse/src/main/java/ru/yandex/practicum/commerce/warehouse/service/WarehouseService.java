package ru.yandex.practicum.commerce.warehouse.service;

import interaction.model.cart.CartDto;
import interaction.model.warehouse.AddProductToWarehouseRequest;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.AssemblyProductsForOrderRequest;
import interaction.model.warehouse.BookedProductDto;
import interaction.model.warehouse.NewProductInWarehouseRequest;
import interaction.model.warehouse.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void addNewProduct(NewProductInWarehouseRequest request);

    BookedProductDto bookProduct(CartDto cart);

    void addQuantity(AddProductToWarehouseRequest request);

    AddressDto getCurrentAddress();

    BookedProductDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> productsToReturn);
}
