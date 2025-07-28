package ru.yandex.practicum.model;

import jakarta.persistence.Entity;
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

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "address")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID addressId;
    String country;
    String city;
    String street;
    String house;
    String flat;
}
