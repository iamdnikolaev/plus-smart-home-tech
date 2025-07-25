package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto createDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        log.info("==> Create delivery by deliveryDto = {}", deliveryDto);
        DeliveryDto result = deliveryService.createDelivery(deliveryDto);
        log.info("<== Create delivery result = {}", result);

        return result;
    }

    @PostMapping("/successful")
    public void successfulDelivery(@RequestBody UUID deliveryId) {
        log.info("==> Successful delivery for deliveryId = {}", deliveryId);
        deliveryService.successfulDelivery(deliveryId);
        log.info("<== Successful delivery end");
    }

    @PostMapping("/picked")
    public void pickedDelivery(@RequestBody UUID deliveryId) {
        log.info("==> Picked delivery for deliveryId = {}", deliveryId);
        deliveryService.pickedDelivery(deliveryId);
        log.info("<== Picked delivery end");
    }

    @PostMapping("/failed")
    public void failedDelivery(@RequestBody UUID deliveryId) {
        log.info("==> Failed delivery for deliveryId = {}", deliveryId);
        deliveryService.failedDelivery(deliveryId);
        log.info("<== Failed delivery end");
    }

    @PostMapping("/cost")
    public double costDelivery(@Valid @RequestBody OrderDto orderDto) {
        log.info("==> Cost delivery for orderDto = {}", orderDto);
        double result = deliveryService.costDelivery(orderDto);
        log.info("<== Cost delivery result = {}", result);

        return result;
    }
}