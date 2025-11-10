package com.unnamed.conectareparo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * <p>Data Transfer Object for {@link com.unnamed.conectareparo.entity.ActionMaterial} http response.</p>
 *
 * <p>Each record describes one material item, including its public identifier,
 * name, quantity, and unit of measure.</p>
 */
@Schema(description = "Material information used in a maintenance action")
public record MaterialResponseDto(
        @Schema(description = "Unique identifier of the material", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Name of the material item", example = "Cimento")
        String itemName,
        @Schema(description = "Quantity of the material used", example = "5.0")
        BigDecimal quantity,
        @Schema(description = "Unit of measure for the quantity", example = "kg")
        String unitOfMeasure
) {
}