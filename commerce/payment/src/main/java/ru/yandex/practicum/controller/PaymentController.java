package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto createPayment(@Valid @RequestBody OrderDto orderDto) {
        log.info("==> Payment for orderDto = {}", orderDto);
        PaymentDto result = paymentService.createPayment(orderDto);
        log.info("<== Payment result = {}", result);

        return result;
    }

    @PostMapping("/totalCost")
    public double totalCost(@Valid @RequestBody OrderDto orderDto) {
        log.info("==> Count total cost for orderDto = {}", orderDto);
        Double result = paymentService.totalCost(orderDto);
        log.info("<== Total cost result = {}", result);

        return result;
    }

    @PostMapping("/refund")
    public void refund(@RequestBody UUID orderId) {
        log.info("==> Refund for orderId = {}", orderId);
        paymentService.refund(orderId);
        log.info("<== Refund end");
    }

    @PostMapping("/productCost")
    public double productCost(@Valid @RequestBody OrderDto orderDto) {
        log.info("==> Count cost of products for orderDto = {}", orderDto);
        Double result = paymentService.productCost(orderDto);
        log.info("<== Cost of products result = {}", result);

        return result;
    }

    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID orderId) {
        log.info("==> Payment failed for orderId = {}", orderId);
        paymentService.failed(orderId);
        log.info("<== Payment failed end");
    }
}
