package ru.yandex.practicum.commerce.delivery.service;

import interaction.client.OrderFeignClient;
import interaction.client.WarehouseFeignClient;
import interaction.model.delivery.DeliveryDto;
import interaction.model.delivery.DeliveryState;
import interaction.model.order.OrderDto;
import interaction.model.warehouse.ShippedToDeliveryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.model.Address;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final OrderFeignClient orderClient;
    private final WarehouseFeignClient warehouseClient;

    private static final String ADDRESS_1 = "ADDRESS_1";
    private static final String ADDRESS_2 = "ADDRESS_2";
    private static final double BASE_COST = 5.0;
    private static final double ADDRESS_MULTIPLIER_COST = 5.0;
    private static final double FRAGILE_MULTIPLIER = 0.2;
    private static final double WEIGHT_MULTIPLIER = 0.3;
    private static final double VOLUME_MULTIPLIER = 0.2;
    private static final double DIFFERENT_STREET_MULTIPLIER = 0.2;

    @Override
    @Transactional
    public DeliveryDto addDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = mapper.toEntity(deliveryDto);
        Delivery savedDelivery = repository.save(delivery);
        log.info("Доставка создана с ID: {} для заказа: {}", savedDelivery.getDeliveryId(), deliveryDto.getOrderId());
        return mapper.toDto(savedDelivery);
    }

    @Transactional
    @Override
    public void simulateSuccessfulDelivery(UUID orderId) {
        Delivery delivery = repository.findByOrderIdOrThrow(orderId);

        delivery.setState(DeliveryState.DELIVERED);
        repository.save(delivery);

        orderClient.deliveryOrder(orderId);
    }

    @Transactional
    @Override
    public void simulateDeliveryReceived(UUID orderId) {
        Delivery delivery = repository.findByOrderIdOrThrow(orderId);

        delivery.setState(DeliveryState.IN_PROGRESS);
        Delivery updatedDelivery = repository.save(delivery);

        orderClient.assemblyOrder(orderId);
        warehouseClient.shippedToDelivery(new ShippedToDeliveryRequest(orderId, delivery.getDeliveryId()));
    }

    @Transactional
    @Override
    public void simulateDeliveryFailed(UUID orderId) {
        Delivery delivery = repository.findByOrderIdOrThrow(orderId);

        delivery.setState(DeliveryState.FAILED);
        Delivery updatedDelivery = repository.save(delivery);

        orderClient.deliveryFailedOrder(orderId);
    }

    @Override
    public Double calculateDeliveryCost(OrderDto order) {
        log.debug("Расчет стоимости доставки для заказа: {}", order.getOrderId());

        UUID deliveryId = order.getDeliveryId();
        if (deliveryId == null) {
            throw new IllegalArgumentException("Delivery ID не может быть null");
        }

        Delivery delivery = repository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Доставка с id " + deliveryId + " не найдена"));

        double currentCost = BASE_COST;

        //учет адреса склада
        Address warehouseAddress = delivery.getFromAddress();
        double addressMultiplier = calculateAddressMultiplier(warehouseAddress);
        currentCost += ADDRESS_MULTIPLIER_COST * addressMultiplier;

        //если признак хрупкости
        if (delivery.getIsFragile() != null && delivery.getIsFragile()) {
            currentCost += currentCost * FRAGILE_MULTIPLIER;
        }

        if (delivery.getTotalWeight() != null) {
            currentCost += delivery.getTotalWeight() * WEIGHT_MULTIPLIER;
        }

        if (delivery.getTotalVolume() != null) {
            currentCost += delivery.getTotalVolume() * VOLUME_MULTIPLIER;
        }

        //сравнение улиц
        boolean sameStreet = isSameStreet(delivery.getFromAddress(), delivery.getToAddress());
        if (!sameStreet) {
            currentCost += currentCost * DIFFERENT_STREET_MULTIPLIER;
        }

        log.debug("Стоимость доставки рассчитана для заказа {}: {}", order.getOrderId(), currentCost);
        return currentCost;
    }

    private double calculateAddressMultiplier(Address address) {
        if (address == null) {
            return 1.0;
        }

        boolean containsKeyword1 = containsKeyword(address, ADDRESS_1);
        boolean containsKeyword2 = containsKeyword(address, ADDRESS_2);

        if (containsKeyword1 && !containsKeyword2) {
            return 1.0;
        } else if (containsKeyword2 && !containsKeyword1) {
            return 2.0;
        } else if (containsKeyword1 && containsKeyword2) {
            return 2.0;
        } else {
            return 1.0;
        }
    }

    private boolean isSameStreet(Address fromAddress, Address toAddress) {
        if (fromAddress == null || toAddress == null) {
            return false;
        }
        String fromStreet = fromAddress.getStreet();
        String toStreet = toAddress.getStreet();
        return fromStreet != null && toStreet != null && fromStreet.equals(toStreet);
    }

    private boolean containsKeyword(Address address, String keyword) {
        if (address == null || keyword == null) {
            return false;
        }
        String lowerKeyword = keyword.toLowerCase();
        return (address.getCountry() != null && address.getCountry().toLowerCase().contains(lowerKeyword)) ||
                (address.getCity() != null && address.getCity().toLowerCase().contains(lowerKeyword)) ||
                (address.getStreet() != null && address.getStreet().toLowerCase().contains(lowerKeyword)) ||
                (address.getHouse() != null && address.getHouse().toLowerCase().contains(lowerKeyword)) ||
                (address.getFlat() != null && address.getFlat().toLowerCase().contains(lowerKeyword));
    }
}
