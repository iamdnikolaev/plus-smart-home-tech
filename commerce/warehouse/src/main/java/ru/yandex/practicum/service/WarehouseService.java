package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void createProduct(NewProductInWarehouseRequestDto requestDto);

    BookedProductsDto checkQuantity(ShoppingCartDto dto);

    AddressDto getAddress();

    void addProductToWarehouse(AddProductToWarehouseRequestDto requestDto);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void returnProducts(Map<UUID, Long> products);

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);
}
