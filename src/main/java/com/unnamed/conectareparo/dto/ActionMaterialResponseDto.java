package com.unnamed.conectareparo.dto;

import java.math.BigDecimal;
import java.util.UUID;
/**
 * Data Transfer Object for {@link ActionMaterial} Response
 * @param publicId     Unique identifier for the action material
 * @param itemName     Name of the material item
 * @param quantity     Quantity of the material
 * @param unitOfMeasure Unit of measure for the quantity
 */
public record ActionMaterialResponseDto(
        UUID publicId,
        String itemName,
        BigDecimal quantity,
        String unitOfMeasure
) {
}