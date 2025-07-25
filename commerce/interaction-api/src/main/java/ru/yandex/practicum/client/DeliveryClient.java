package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {

    @PutMapping
    DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void completeDelivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/picked")
    void confirmPickup(@RequestBody @NotNull UUID orderId);

    @PostMapping("/failed")
    void failDelivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/cost")
    double costDelivery(@RequestBody @Valid OrderDto orderDto);
}
