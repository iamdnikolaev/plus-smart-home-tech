package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.CreateNewOrderRequestDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequestDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderDto> getOrders(String username);

    OrderDto createOrder(CreateNewOrderRequestDto newOrderRequest);

    OrderDto returnOrder(ProductReturnRequestDto returnRequest);

    OrderDto payOrder(UUID orderId);

    OrderDto payOrderFailed(UUID orderId);

    OrderDto deliveryOrder(UUID orderId);

    OrderDto deliveryOrderFailed(UUID orderId);

    OrderDto completedOrder(UUID orderId);

    OrderDto calculateTotal(UUID orderId);

    OrderDto calculateDelivery(UUID orderId);

    OrderDto assemblyOrder(UUID orderId);

    OrderDto assemblyOrderFailed(UUID orderId);
}