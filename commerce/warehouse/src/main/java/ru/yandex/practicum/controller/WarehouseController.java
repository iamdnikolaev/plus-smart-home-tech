package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@Slf4j
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PutMapping
    public void createProduct(@RequestBody @Valid NewProductInWarehouseRequestDto requestDto) {
        log.info("==> Create product requestDto = {}", requestDto);
        warehouseService.createProduct(requestDto);
        log.info("<== The product created in warehouse");
    }

    @PostMapping("/check")
    public BookedProductsDto checkQuantity(@RequestBody @Valid ShoppingCartDto cart) {
        log.info("==> Check product quantity in shopping card = {}", cart);
        BookedProductsDto result = warehouseService.checkQuantity(cart);
        log.info("<== Result of check = {}", result);

        return result;
    }

    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequestDto requestDto) {
        log.info("==> Add product to warehouse, requestDto = {}", requestDto);
        warehouseService.addProductToWarehouse(requestDto);
        log.info("<== The product added to warehouse");
    }

    @GetMapping("/address")
    public AddressDto getAddress() {
        log.info("==> Get address");
        AddressDto result = warehouseService.getAddress();
        log.info("<== Got address result = {}", result);

        return result;
    }

    @PostMapping("/shipped")
    public void shippedToDelivery(@RequestBody @Valid ShippedToDeliveryRequest request) {
        log.info("==> Shipped to delivery request = {}", request);
        warehouseService.shippedToDelivery(request);
        log.info("<== Shipped to delivery end");
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest request) {
        log.info("==> Assembly products for order request = {}", request);
        BookedProductsDto result = warehouseService.assemblyProductsForOrder(request);
        log.info("<== Assembled products for order result = {}", result);

        return result;
    }

    @PostMapping("/return")
    public void returnProducts(@RequestBody @Valid Map<UUID, Long> products) {
        log.info("==> Return products = {}", products);
        warehouseService.returnProducts(products);
        log.info("<== Return products end");
    }
}