package ru.yandex.practicum.commerce.order.service;

import interaction.client.CartFeignClient;
import interaction.client.DeliveryFeignClient;
import interaction.client.PaymentFeignClient;
import interaction.client.WarehouseFeignClient;
import interaction.model.delivery.DeliveryDto;
import interaction.model.delivery.DeliveryState;
import interaction.model.order.CreateNewOrderRequest;
import interaction.model.order.OrderDto;
import interaction.model.order.OrderState;
import interaction.model.order.ProductReturnRequest;
import interaction.model.payment.PaymentDto;
import interaction.model.warehouse.AddressDto;
import interaction.model.warehouse.AssemblyProductsForOrderRequest;
import interaction.model.warehouse.BookedProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CartFeignClient cartClient;
    private final WarehouseFeignClient warehouseClient;
    private final PaymentFeignClient paymentClient;
    private final DeliveryFeignClient deliveryClient;

    @Override
    public Page<OrderDto> getOrders(String username, Pageable pageable) {
        UUID shoppingCartId = cartClient.getShoppingCart(username).getShoppingCartId();
        return repository.findByShoppingCartId(shoppingCartId, pageable).map(mapper::toDto);
    }

    @Transactional
    @Override
    public OrderDto addOrder(CreateNewOrderRequest request) {
        BookedProductDto bookedProducts = warehouseClient.checkAvailability(request.getCartDto());
        UUID shoppingCartId = request.getCartDto().getShoppingCartId();
        Map<UUID, Long> productsInOrder = request.getCartDto().getProducts();

        Order order = Order.builder()
                .orderId(UUID.randomUUID())
                .state(OrderState.NEW)
                .products(new HashMap<>(productsInOrder))
                .shoppingCartId(shoppingCartId)
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .fragile(bookedProducts.isFragile())
                .build();

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDto returnProductsFromOrder(ProductReturnRequest request) {
        Order order = repository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + request.getOrderId() + " not found"));

        if (order.getState() != OrderState.DELIVERED && order.getState() != OrderState.COMPLETED) {
            throw new IllegalArgumentException("Возврат продукции возможен только из выполненных заказов.");
        }

        UUID orderId = request.getOrderId();
        Map<UUID, Long> currentProducts = new HashMap<>(order.getProducts());
        Map<UUID, Long> productsToReturn = request.getProducts();

        for (Map.Entry<UUID, Long> entry : productsToReturn.entrySet()) {
            UUID productId = entry.getKey();
            Long quantityToReturn = entry.getValue();

            if (!currentProducts.containsKey(productId)) {
                throw new IllegalArgumentException("Продукта с ID " + productId + " нет в заказе " + orderId);
            }

            Long currentQuantity = currentProducts.get(productId);

            if (quantityToReturn > currentQuantity) {
                throw new IllegalArgumentException(
                        "Количество возвращаемого продукта " + productId +
                                " (" + quantityToReturn + ") превышает количество в заказе (" + currentQuantity + ")"
                );
            }

            Long newQuantity = currentQuantity - quantityToReturn;
            if (newQuantity == 0) {
                currentProducts.remove(productId);
            } else {
                currentProducts.put(productId, newQuantity);
            }
        }

        order.setProducts(currentProducts);
        order.setState(OrderState.PRODUCT_RETURNED);

        warehouseClient.acceptReturn(productsToReturn);

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDto paymentOrder(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));
        order.setState(OrderState.PAID);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDto paymentFailedOrder(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));
        order.setState(OrderState.PAYMENT_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));
        order.setState(OrderState.DELIVERED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDto deliveryFailedOrder(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));
        order.setState(OrderState.DELIVERY_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDto completedOrder(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));
        order.setState(OrderState.COMPLETED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDto calculateTotalPayment(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));

        PaymentDto paymentDto = paymentClient.createPayment(mapper.toDto(order));
        order.setPaymentId(paymentDto.getPaymentId());

        Double productCost = paymentClient.calculateProductCost(mapper.toDto(order));
        order.setProductPrice(productCost);

        Double totalCost = paymentClient.calculateTotalCost(mapper.toDto(order));
        order.setTotalPrice(totalCost);

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));

        AddressDto fromAddress = warehouseClient.getWarehouseAddress();
        AddressDto toAddress = warehouseClient.getWarehouseAddress();

        DeliveryDto deliveryDto = new DeliveryDto(
                null,
                fromAddress,
                toAddress,
                orderId,
                DeliveryState.CREATED);

        deliveryClient.addDelivery(deliveryDto);

        Double deliveryCost = deliveryClient.calculateDeliveryCost(mapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDto assemblyOrder(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));
        warehouseClient.assemblyProductsForOrder(new AssemblyProductsForOrderRequest(
                order.getProducts(),
                orderId)
        );
        order.setState(OrderState.ASSEMBLED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    @Override
    public OrderDto assemblyFailedOrder(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));
        order.setState(OrderState.ASSEMBLY_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Override
    public OrderDto getById(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " not found"));
        return mapper.toDto(order);
    }
}
