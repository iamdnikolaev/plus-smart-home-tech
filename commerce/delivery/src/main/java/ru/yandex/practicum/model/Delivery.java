package ru.yandex.practicum.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "delivery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id")
    UUID deliveryId;
    @Column(name = "order_id")
    UUID orderId;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "from_address_id")
    Address senderAddress;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "to_address_id")
    Address recipientAddress;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "state")
    DeliveryState deliveryStatus;
    Double deliveryWeight;
    Double deliveryVolume;
    boolean fragile;
}
