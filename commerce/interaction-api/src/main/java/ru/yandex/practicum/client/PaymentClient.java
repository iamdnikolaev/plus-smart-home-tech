package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient {
    @PostMapping("/totalCost")
    Double totalCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/productCost")
    Double productCost(@Valid @RequestBody OrderDto order);

    @PostMapping
    PaymentDto createPayment(@Valid @RequestBody OrderDto order);
}