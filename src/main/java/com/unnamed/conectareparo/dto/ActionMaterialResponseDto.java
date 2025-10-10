package com.unnamed.conectareparo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ActionMaterialResponseDto(
        UUID publicId,
        String itemName,
        BigDecimal quantity,
        String unitOfMeasure
) {
}