package ru.yandex.practicum.commerce.delivery.mapper;

import interaction.model.delivery.DeliveryDto;
import interaction.model.warehouse.AddressDto;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.delivery.model.Address;
import ru.yandex.practicum.commerce.delivery.model.Delivery;

import java.util.UUID;

@Component
public class DeliveryMapper {

    public DeliveryDto toDto(Delivery delivery) {
        if (delivery == null) {
            return null;
        }

        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .fromAddress(convertToAddressDto(delivery.getFromAddress()))
                .toAddress(convertToAddressDto(delivery.getToAddress()))
                .orderId(delivery.getOrderId())
                .deliveryState(delivery.getState())
                .build();
    }

    public Delivery toEntity(DeliveryDto deliveryDto) {
        if (deliveryDto == null) {
            return null;
        }

        return Delivery.builder()
                .deliveryId(deliveryDto.getDeliveryId() != null ? deliveryDto.getDeliveryId() : UUID.randomUUID())
                .totalVolume(null)
                .totalWeight(null)
                .isFragile(null)
                .fromAddress(convertToAddressEntity(deliveryDto.getFromAddress()))
                .toAddress(convertToAddressEntity(deliveryDto.getToAddress()))
                .state(deliveryDto.getDeliveryState())
                .orderId(deliveryDto.getOrderId())
                .build();
    }

    private AddressDto convertToAddressDto(Address address) {

        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }

    private Address convertToAddressEntity(AddressDto addressDto) {

        return new Address(
                addressDto.getCountry(),
                addressDto.getCity(),
                addressDto.getStreet(),
                addressDto.getHouse(),
                addressDto.getFlat()
        );
    }
}
