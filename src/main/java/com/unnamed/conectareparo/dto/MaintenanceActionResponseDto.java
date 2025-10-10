package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.ActionOutcomeStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record MaintenanceActionResponseDto(
        UUID publicId,
        String executedBy,
        ZonedDateTime startDate,
        ZonedDateTime completionDate,
        String actionDescription,
        List<ActionMaterialResponseDto> materialsUsed,
        ActionOutcomeStatus outcomeStatus,
        ZonedDateTime createdAt
) {
}