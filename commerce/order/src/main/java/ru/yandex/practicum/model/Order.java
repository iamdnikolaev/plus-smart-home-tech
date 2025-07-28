package ru.yandex.practicum.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.OrderState;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    UUID orderId;
    @Column(name = "shopping_cart_id")
    UUID shoppingCartId;
    @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    Map<UUID, Long> products;
    @Column(name = "payment_id")
    UUID paymentId;
    @Column(name = "delivery_id")
    UUID deliveryId;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    OrderState state;
    @Column(name = "delivery_weight")
    double deliveryWeight;
    @Column(name = "delivery_volume")
    double deliveryVolume;
    @Column(name = "fragile")
    boolean fragile;
    @Column(name = "total_price")
    double totalPrice;
    @Column(name = "delivery_price")
    double deliveryPrice;
    @Column(name = "product_price")
    double productPrice;
}
