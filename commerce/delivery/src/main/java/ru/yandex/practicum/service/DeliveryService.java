package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createDelivery(DeliveryDto deliveryDto);

    void successfulDelivery(UUID deliveryId);

    void pickedDelivery(UUID deliveryId);

    void failedDelivery(UUID deliveryId);

    double costDelivery(OrderDto orderDto);
}