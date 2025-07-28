package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repositury.DeliveryRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toDelivery(deliveryDto);
        delivery.setDeliveryStatus(DeliveryState.CREATED);

        return deliveryMapper.toDeliveryDto(deliveryRepository.save(delivery));
    }

    @Override
    public void successfulDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryStatus(DeliveryState.DELIVERED);
        orderClient.completedOrder(delivery.getOrderId());
    }

    @Override
    public void pickedDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryStatus(DeliveryState.IN_PROGRESS);
        orderClient.assemblyOrder(delivery.getOrderId());
        ShippedToDeliveryRequest deliveryRequest = new ShippedToDeliveryRequest(
                delivery.getOrderId(), delivery.getDeliveryId()
        );
        warehouseClient.shippedToDelivery(deliveryRequest);
    }

    @Override
    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryStatus(DeliveryState.FAILED);
        orderClient.deliveryOrderFailed(delivery.getOrderId());
    }

    @Override
    @Transactional(readOnly = true)
    public double costDelivery(OrderDto orderDto) {
        final double baseRate = 5.0;

        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getOrderId()).orElseThrow(
                () -> new NoDeliveryFoundException(String.format("Delivery not found by orderId = %s",
                        orderDto.getOrderId()))
        );

        AddressDto warehouseAddress = warehouseClient.getAddress();
        double addressCost = switch (warehouseAddress.getCity()) {
            case "ADDRESS_1" -> baseRate * 1;
            case "ADDRESS_2" -> baseRate * 2;
            default ->
                    throw new IllegalStateException(String.format("Unexpected value: %s", warehouseAddress.getCity()));
        };
        double deliveryCost = baseRate + addressCost;
        if (orderDto.getFragile()) {
            deliveryCost += deliveryCost * 0.2;
        }
        deliveryCost += orderDto.getDeliveryWeight() * 0.3;
        deliveryCost += orderDto.getDeliveryVolume() * 0.2;
        if (!warehouseAddress.getStreet().equals(delivery.getRecipientAddress().getStreet())) {
            deliveryCost += deliveryCost * 0.2;
        }

        return deliveryCost;
    }

    private Delivery getDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new NoDeliveryFoundException(String.format("Delivery not found by id = %s", deliveryId))
        );
    }
}
