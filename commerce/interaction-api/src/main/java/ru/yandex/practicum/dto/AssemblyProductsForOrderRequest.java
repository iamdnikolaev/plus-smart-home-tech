package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssemblyProductsForOrderRequest {
    @NotNull
    UUID orderId;
    @NotEmpty
    Map<UUID, Long> products;
}
