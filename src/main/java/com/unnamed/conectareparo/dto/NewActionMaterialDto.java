package com.unnamed.conectareparo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Data Transfer Object for {@link ActionMaterial} Creation
 * @param itemName name of the material item
 * @param quantity quantity of the material used
 * @param unitOfMeasure unit of measure for the quantity (e.g., kg, liters, pieces)
 */
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