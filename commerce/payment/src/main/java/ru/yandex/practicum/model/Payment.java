package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.PaymentStatus;

import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    UUID paymentId;
    @Column(name = "order_id")
    UUID orderId;
    @Column(name = "products_total")
    double productsTotal;
    @Column(name = "delivery_total")
    double deliveryTotal;
    @Column(name = "total_payment")
    double totalPayment;
    @Column(name = "fee_total")
    double feeTotal;
    @Enumerated(EnumType.STRING)
    PaymentStatus status;
}
