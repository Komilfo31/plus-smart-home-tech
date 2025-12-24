package ru.yandex.practicum.commerce.warehouse.service;

import interaction.model.cart.CartDto;
import interaction.model.warehouse.AddProductToWarehouseRequest;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.BookedProductDto;
import interaction.model.warehouse.NewProductInWarehouseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private final static String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];
    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request){
        if(request.getProductId() != null && repository.existsById(request.getProductId())){
            throw new IllegalArgumentException("Product already exists");
        }

        WarehouseProduct product = new WarehouseProduct();
        product.setProductId(request.getProductId());
        product.setFragile(request.isFragile());
        product.setDimension(mapper.dtoToDimension(request.getDimension()));
        product.setWeight(request.getWeight());
        product.setQuantity(0L);
        repository.save(product);
    }

    @Override
    public BookedProductDto bookProduct(CartDto cart){
        Map<UUID, Long> productsToBooking = cart.getProducts();
        List<WarehouseProduct> products = repository.findAllByProductIdIn(productsToBooking.keySet());

        if (products.size() != productsToBooking.size()) {

            List<UUID> notFoundedProducts = new ArrayList<>();
            List<UUID> foundedProducts = products.stream()
                    .map(WarehouseProduct::getProductId)
                    .toList();

            for (UUID uuid : productsToBooking.keySet()) {
                if (!foundedProducts.contains(uuid)) {
                    notFoundedProducts.add(uuid);
                }
            }

            throw new IllegalArgumentException("Not founded products: \n " + notFoundedProducts);
        }

        Map<UUID, Long> productsAfterBooking = new HashMap<>();

        double totalVolume = 0;
        double totalWeight = 0;
        boolean fragile = false;

        for (WarehouseProduct product : products) {
            productsAfterBooking.put(product.getProductId(),
                    product.getQuantity() - productsToBooking.get(product.getProductId()));

            if (product.isFragile()) {
                fragile = true;
            }
            totalWeight = totalWeight + product.getWeight();
            totalVolume = totalVolume + (product.getDimension().getDepth()
                    * product.getDimension().getWidth()
                    * product.getDimension().getHeight()
            );
        }

        List<UUID> notEnoughProducts = productsAfterBooking.entrySet().stream()
                .filter((entry) -> entry.getValue() < 0)
                .map(Map.Entry::getKey)
                .toList();

        if (!notEnoughProducts.isEmpty()) {
            throw new IllegalArgumentException("Not enough products: \n " + notEnoughProducts);
        }
        return new BookedProductDto(totalWeight, totalVolume, fragile);
    }

    @Override
    public void addQuantity(AddProductToWarehouseRequest request){
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + request.getProductId() + " not found"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        repository.save(product);
    }

    @Override
    public AddressDto getCurrentAddress(){
        return new AddressDto(
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS);
    }
}
