package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto order);

    Double totalCost(OrderDto order);

    void refund(UUID uuid);

    Double productCost(OrderDto order);

    void failed(UUID uuid);
}