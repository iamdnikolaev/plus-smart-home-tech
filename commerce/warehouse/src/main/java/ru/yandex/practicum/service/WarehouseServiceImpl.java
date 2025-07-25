package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoBookingFoundException;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final BookingRepository bookingRepository;

    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    @Override
    public void createProduct(NewProductInWarehouseRequestDto requestDto) {
        warehouseRepository.findById(requestDto.getProductId()).ifPresent(warehouse -> {
            throw new SpecifiedProductAlreadyInWarehouseException("The product already is in warehouse");
        });
        WarehouseProduct warehouseProduct = warehouseMapper.toWarehouseProduct(requestDto);
        warehouseRepository.save(warehouseProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkQuantity(ShoppingCartDto shoppingCartDto) {
        BookedProductsDto BookedProductsDto = new BookedProductsDto();
        for (UUID productId : shoppingCartDto.getProducts().keySet()) {
            WarehouseProduct product = warehouseRepository.findByProductId(productId)
                    .orElseThrow(() -> new ProductInShoppingCartNotInWarehouse("The product is not found in " +
                            "warehouse by productId =" + productId));
            long requestedQuantity = shoppingCartDto.getProducts().get(productId);
            long productQuantity = 0;
            if (product.getQuantity() != null) {
                productQuantity = product.getQuantity();
            }

            if (productQuantity < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("The product is not enough (" + productQuantity
                        + ") in warehouse for requested (" + requestedQuantity + ")");
            }

            double deliveryVolume = 0.0;
            if (BookedProductsDto.getDeliveryVolume() != null) {
                deliveryVolume = BookedProductsDto.getDeliveryVolume();
            }
            BookedProductsDto.setDeliveryVolume(
                    deliveryVolume + product.getDimension().getHeight()
                            * product.getDimension().getDepth()
                            * product.getDimension().getWidth()
            );

            double deliveryWeight = 0.0;
            if (BookedProductsDto.getDeliveryWeight() != null) {
                deliveryWeight = BookedProductsDto.getDeliveryWeight();
            }
            BookedProductsDto.setDeliveryWeight(deliveryWeight + product.getWeight());
            if (product.getFragile()) {
                BookedProductsDto.setFragile(true);
            }
            log.info("Successful checked productId = {}", productId);
        }
        log.info("All products have been checked");

        return BookedProductsDto;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequestDto requestDto) {
        WarehouseProduct product = warehouseRepository.findByProductId(requestDto.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("The product is not found in " +
                        "warehouse by productId = " + requestDto.getProductId()));

        long quantity = 0;
        if (product.getQuantity() != null) {
            quantity = product.getQuantity();
        }
        product.setQuantity(quantity + requestDto.getQuantity());
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDto getAddress() {
        return AddressDto.builder()
                .country("country")
                .city("city")
                .street("street")
                .house("house")
                .flat("flat")
                .build();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        Booking booking = bookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new NoBookingFoundException("Booking for orderId = " + request.getOrderId() +
                        "not found"));
        booking.setDeliveryId(request.getDeliveryId());
        bookingRepository.save(booking);
    }

    @Override
    public void returnProducts(Map<UUID, Long> products) {
        Set<WarehouseProduct> storedProducts = warehouseRepository.findAllByIdIn(products.keySet());
        storedProducts.forEach(product -> {
            UUID id = product.getProductId();
            product.setQuantity(product.getQuantity() + products.get(id));
        });
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Long> orderProducts = request.getProducts();
        Map<UUID, WarehouseProduct> products = getWarehouseProducts(orderProducts.keySet());

        double deliveryWeight = 0;
        double deliveryVolume = 0;
        boolean deliveryIsFragile = false;
        for (Map.Entry<UUID, Long> orderProduct : orderProducts.entrySet()) {
            WarehouseProduct product = products.get(orderProduct.getKey());
            long newQuantity = product.getQuantity() - orderProduct.getValue();
            if (newQuantity < 0) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Product with productId = " + product.getProductId() + " is not enough");
            }
            product.setQuantity(newQuantity);
            deliveryWeight += product.getWeight() * orderProduct.getValue();
            deliveryVolume += product.getDimension().getHeight() * product.getDimension().getWidth()
                    * product.getDimension().getDepth() * orderProduct.getValue();
            deliveryIsFragile = deliveryIsFragile || product.getFragile();
        }
        Booking booking = Booking.builder()
                .orderId(request.getOrderId())
                .products(request.getProducts())
                .build();
        bookingRepository.save(booking);

        return BookedProductsDto.builder()
                .deliveryWeight(deliveryWeight)
                .deliveryVolume(deliveryVolume)
                .fragile(deliveryIsFragile)
                .build();
    }

    private Map<UUID, WarehouseProduct> getWarehouseProducts(Collection<UUID> ids) {
        Map<UUID, WarehouseProduct> products = warehouseRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        if (products.size() != ids.size()) {
            List<UUID> listNotFoundProducts = ids
                    .stream()
                    .filter(uuid -> !products.containsKey(uuid))
                    .toList();

            throw new ProductInShoppingCartLowQuantityInWarehouse("Products not found in warehouse: "
                    + listNotFoundProducts);
        }

        return products;
    }

}
