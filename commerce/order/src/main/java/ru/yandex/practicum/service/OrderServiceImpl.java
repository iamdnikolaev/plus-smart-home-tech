package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.ShoppingCartClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.CreateNewOrderRequestDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ProductReturnRequestDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartClient shoppingCartClient;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrders(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("Wrong username");
        }
        ShoppingCartDto shoppingCartDto = shoppingCartClient.getShoppingCart(username);
        List<Order> orders = orderRepository.findByShoppingCartId(shoppingCartDto.getShoppingCartId());

        return orderMapper.toOrderDtos(orders);
    }

    @Override
    public OrderDto createOrder(CreateNewOrderRequestDto newOrderRequest) {
        Order order = Order.builder()
                .shoppingCartId(newOrderRequest.getShoppingCartDto().getShoppingCartId())
                .products(newOrderRequest.getShoppingCartDto().getProducts())
                .state(OrderState.NEW)
                .build();
        Order newOrder = orderRepository.save(order);
        BookedProductsDto bookedProducts = warehouseClient.assemblyProductsForOrder(
                new AssemblyProductsForOrderRequest(
                        newOrderRequest.getShoppingCartDto().getShoppingCartId(),
                        newOrderRequest.getShoppingCartDto().getProducts()
                ));

        newOrder.setFragile(bookedProducts.getFragile());
        newOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        newOrder.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        newOrder.setProductPrice(paymentClient.productCost(orderMapper.toOrderDto(newOrder)));

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(newOrder.getOrderId())
                .fromAddress(warehouseClient.getAddress())
                .toAddress(newOrderRequest.getAddressDto())
                .build();
        newOrder.setDeliveryId(deliveryClient.createDelivery(deliveryDto).getDeliveryId());

        paymentClient.createPayment(orderMapper.toOrderDto(newOrder));

        return orderMapper.toOrderDto(newOrder);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequestDto returnRequest) {
        Order order = getOrderById(returnRequest.getOrderId());
        warehouseClient.returnProducts(returnRequest.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto payOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAID);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto payOrderFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERED);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto deliveryOrderFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto completedOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.COMPLETED);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setTotalPrice(paymentClient.totalCost(orderMapper.toOrderDto(order)));

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setDeliveryPrice(deliveryClient.costDelivery(orderMapper.toOrderDto(order)));

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLED);

        return orderMapper.toOrderDto(order);
    }

    @Override
    public OrderDto assemblyOrderFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);

        return orderMapper.toOrderDto(order);
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(String.format("Order not found by id = %s ", orderId))
                );
    }
}
