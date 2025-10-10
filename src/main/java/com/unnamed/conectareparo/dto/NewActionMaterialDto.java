package com.unnamed.conectareparo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record NewActionMaterialDto(
        @NotBlank(message = "Item name is mandatory.")
        String itemName,
        @NotNull(message = "Quantity used is mandatory.")
        @Positive(message = "Quantity used must be positive.")
        BigDecimal quantity,
        @NotBlank(message = "Unit of measure is mandatory.")
        String unitOfMeasure
) {
}