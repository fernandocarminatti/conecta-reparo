package com.unnamed.conectareparo.maintenanceaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Data Transfer Object for creation {@link com.unnamed.conectareparo.entity.ActionMaterial} via http request.
 */
@Schema(description = "Data Transfer Object for creating a new action material")
public record MaterialDto(
        @Schema(description = "Name of the material item", example = "Cimento")
        @NotBlank(message = "Item name is mandatory.")
        String itemName,
        @Schema(description = "Quantity of the material used", example = "5.0")
        @NotNull(message = "Quantity used is mandatory.")
        @Positive(message = "Quantity used must be positive.")
        BigDecimal quantity,
        @Schema(description = "Unit of measure for the quantity", example = "kg")
        @NotBlank(message = "Unit of measure is mandatory.")
        String unitOfMeasure
) {
}