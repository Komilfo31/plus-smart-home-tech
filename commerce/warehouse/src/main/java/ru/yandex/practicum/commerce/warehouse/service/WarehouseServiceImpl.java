package ru.yandex.practicum.commerce.warehouse.service;

import interaction.model.cart.CartDto;
import interaction.model.warehouse.AddProductToWarehouseRequest;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.AssemblyProductsForOrderRequest;
import interaction.model.warehouse.BookedProductDto;
import interaction.model.warehouse.NewProductInWarehouseRequest;
import interaction.model.warehouse.ShippedToDeliveryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.model.Dimension;
import ru.yandex.practicum.commerce.warehouse.model.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.repository.OrderBookingRepository;
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
    private final OrderBookingRepository orderBookingRepository;

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) {
        if (request.getProductId() != null && repository.existsById(request.getProductId())) {
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
    public BookedProductDto bookProduct(CartDto cart) {
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
    public void addQuantity(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product with id " + request.getProductId() + " not found"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        repository.save(product);
    }

    @Override
    public AddressDto getCurrentAddress() {
        return new AddressDto(
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS);
    }

    @Override
    @Transactional
    public BookedProductDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Long> productsToBook = request.getProducts();
        validateProductsNotEmpty(productsToBook);

        List<WarehouseProduct> warehouseProducts = getWarehouseProducts(productsToBook);

        Map<UUID, Long> bookedProducts = new HashMap<>();
        double totalVolume = 0.0;
        double totalWeight = 0.0;
        boolean fragile = false;

        for (WarehouseProduct warehouseProduct : warehouseProducts) {
            UUID productId = warehouseProduct.getProductId();
            Long requestedQuantity = productsToBook.get(productId);

            validateRequestedQuantity(productId, requestedQuantity, warehouseProduct.getQuantity());

            bookedProducts.put(productId, requestedQuantity);

            totalWeight += warehouseProduct.getWeight() * requestedQuantity;
            totalVolume += calculateProductVolume(warehouseProduct) * requestedQuantity;

            if (warehouseProduct.isFragile()) {
                fragile = true;
            }

            Long newQuantity = warehouseProduct.getQuantity() - requestedQuantity;
            warehouseProduct.setQuantity(newQuantity);
        }

        repository.saveAll(warehouseProducts);

        createOrderBooking(request.getOrderId(), bookedProducts);

        return new BookedProductDto(totalWeight, totalVolume, fragile);
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking orderBooking = orderBookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Бронирование заказа не найдено для order ID: " + request.getOrderId()
                        ));

        orderBooking.setDeliveryId(request.getDeliveryId());
    }

    @Override
    @Transactional
    public void acceptReturn(Map<UUID, Long> productsToReturn) {
        validateProductsNotEmpty(productsToReturn);

        List<WarehouseProduct> warehouseProducts = getWarehouseProducts(productsToReturn);

        for (WarehouseProduct product : warehouseProducts) {
            UUID productId = product.getProductId();
            Long returnedQuantity = productsToReturn.get(productId);

            validateReturnQuantity(productId, returnedQuantity);

            Long newQuantity = product.getQuantity() + returnedQuantity;
            product.setQuantity(newQuantity);
        }

        repository.saveAll(warehouseProducts);
    }

    private List<WarehouseProduct> getWarehouseProducts(Map<UUID, Long> productQuantities) {
        List<UUID> productIds = new ArrayList<>(productQuantities.keySet());
        List<WarehouseProduct> products = repository.findAllByProductIdIn(productIds);

        if (products.size() != productIds.size()) {
            List<UUID> foundIds = products.stream()
                    .map(WarehouseProduct::getProductId)
                    .toList();
            List<UUID> missingIds = productIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new IllegalArgumentException(
                    String.format("Товары не найдены на складе: %s", missingIds)
            );
        }

        return products;
    }

    private void createOrderBooking(UUID orderId, Map<UUID, Long> bookedProducts) {
        OrderBooking orderBooking = OrderBooking.builder()
                .orderId(orderId)
                .bookedProducts(new HashMap<>(bookedProducts))
                .build();

        orderBookingRepository.save(orderBooking);
        }

    private double calculateProductVolume(WarehouseProduct product) {
        Dimension dimension = product.getDimension();
        return dimension.getDepth() * dimension.getWidth() * dimension.getHeight();
    }

    private void validateProductsNotEmpty(Map<UUID, Long> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Список товаров не может быть пустым");
        }
    }

    private void validateRequestedQuantity(UUID productId, Long requestedQuantity, Long availableQuantity) {
        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new IllegalArgumentException(
                    String.format("Неверное количество для товара %s: %s", productId, requestedQuantity)
            );
        }

        if (requestedQuantity > availableQuantity) {
            throw new IllegalArgumentException(
                    String.format("Недостаточно товара %s: требуется %s, доступно %s",
                            productId, requestedQuantity, availableQuantity)
            );
        }
    }

    private void validateReturnQuantity(UUID productId, Long returnedQuantity) {
        if (returnedQuantity == null || returnedQuantity <= 0) {
            throw new IllegalArgumentException(
                    String.format("Неверное количество возврата для товара %s: %s", productId, returnedQuantity)
            );
        }
    }
}
