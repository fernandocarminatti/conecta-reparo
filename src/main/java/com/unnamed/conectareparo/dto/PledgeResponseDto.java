package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.PledgeStatus;
import com.unnamed.conectareparo.entity.PledgeType;

import java.time.ZonedDateTime;
import java.util.UUID;

public record PledgeResponseDto(
        UUID publicId,
        String volunteerName,
        String volunteerContact,
        String description,
        PledgeType type,
        PledgeStatus status,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}