package ru.yandex.practicum.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

@Configuration
@Slf4j
public class WarehouseFallback implements WarehouseClient {

    private static final String MSG = "Warehouse Fallback response for %s: the service is temporarily unavailable";

    @Override
    public void createProduct(NewProductInWarehouseRequestDto product) {
        log.info(String.format(MSG, "createProduct"));
    }

    @Override
    public BookedProductsDto checkQuantity(ShoppingCartDto cart) {
        log.info(String.format(MSG, "checkQuantity"));
        return new BookedProductsDto();
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequestDto product) {
        log.info(String.format(MSG, "addProductToWarehouse"));
    }

    @Override
    public AddressDto getAddress() {
        log.info(String.format(MSG, "getAddress"));
        return new AddressDto();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info(String.format(MSG, "shippedToDelivery"));
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        log.info(String.format(MSG, "assemblyProductsForOrder"));
        return new BookedProductsDto();
    }

    @Override
    public void returnProducts(@RequestBody Map<UUID, Long> products) {
        log.info(String.format(MSG, "returnProducts"));
    }
}
