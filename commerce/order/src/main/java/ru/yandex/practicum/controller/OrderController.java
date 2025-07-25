package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.CreateNewOrderRequestDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequestDto;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getOrders(@RequestParam String username) {
        log.info("==> Get orders for user {}", username);
        List<OrderDto> result = orderService.getOrders(username);
        log.info("<== Get orders result = {}", result);

        return result;
    }

    @PutMapping
    public OrderDto createOrder(@Valid @RequestBody CreateNewOrderRequestDto newOrderRequest) {
        log.info("==> Create order by newOrderRequest = {}", newOrderRequest);
        OrderDto result = orderService.createOrder(newOrderRequest);
        log.info("<== Create order result = {}", result);

        return result;
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@Valid @RequestBody ProductReturnRequestDto returnRequest) {
        log.info("==> Return order by returnRequest = {}", returnRequest);
        OrderDto result = orderService.returnOrder(returnRequest);
        log.info("<== Return order result = {}", result);

        return result;
    }

    @PostMapping("/payment")
    public OrderDto payOrder(@RequestBody UUID orderId) {
        log.info("==> Pay orderId = {}", orderId);
        OrderDto result = orderService.payOrder(orderId);
        log.info("<== Pay order result = {}", result);

        return result;
    }

    @PostMapping("/payment/failed")
    public OrderDto payOrderFailed(@RequestBody UUID orderId) {
        log.info("==> Pay order failed by orderId = {}", orderId);
        OrderDto result = orderService.payOrderFailed(orderId);
        log.info("<== Pay order failed result = {}", result);

        return result;
    }

    @PostMapping("/delivery")
    public OrderDto deliveryOrder(@RequestBody UUID orderId) {
        log.info("==> Deliver by orderId = {}", orderId);
        OrderDto result = orderService.deliveryOrder(orderId);
        log.info("<== Deliver order result = {}", result);

        return result;
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryOrderFailed(@RequestBody UUID orderId) {
        log.info("==> Deliver order failed by orderId = {}", orderId);
        OrderDto result = orderService.deliveryOrderFailed(orderId);
        log.info("<== Deliver order failed result = {}", result);

        return result;
    }

    @PostMapping("/completed")
    public OrderDto completedOrder(@RequestBody UUID orderId) {
        log.info("==> Completed by orderId = {}", orderId);
        OrderDto result = orderService.completedOrder(orderId);
        log.info("<== Completed order result = {}", result);

        return result;
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotal(@RequestBody UUID orderId) {
        log.info("==> Calculate total by orderId = {}", orderId);
        OrderDto result = orderService.calculateTotal(orderId);
        log.info("<== Calculate total result = {}", result);

        return result;
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDelivery(@RequestBody UUID orderId) {
        log.info("==> Calculate delivery by orderId = {}", orderId);
        OrderDto result = orderService.calculateDelivery(orderId);
        log.info("<== Calculate delivery result = {}", result);

        return result;
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody UUID orderId) {
        log.info("==> Assembly by orderId = {}", orderId);
        OrderDto result = orderService.assemblyOrder(orderId);
        log.info("<== Assembly result = {}", result);

        return result;
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyOrderFailed(@RequestBody UUID orderId) {
        log.info("==> Assembly failed by orderId = {}", orderId);
        OrderDto result = orderService.assemblyOrderFailed(orderId);
        log.info("<== Assembly failed result = {}", result);

        return result;
    }
}