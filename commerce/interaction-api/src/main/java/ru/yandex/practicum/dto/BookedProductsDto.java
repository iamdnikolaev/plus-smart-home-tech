package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {
    @NotNull
    Double deliveryWeight;
    @NotNull
    Double deliveryVolume;
    @NotNull
    Boolean fragile;
}
