package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.PledgeStatus;
import com.unnamed.conectareparo.entity.PledgeType;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for {@link Pledge} Response
 * @param publicId public facing identifier as UUID
 * @param volunteerName name of the volunteer making the pledge
 * @param volunteerContact contact information of the volunteer (e.g., email or phone)
 * @param description detailed description of the pledge
 * @param type type of the pledge
 * @param status current status of the pledge
 * @param createdAt date and time when the pledge was created
 * @param updatedAt date and time when the pledge was last updated
 */
public record PledgeResponseDto(
        UUID id,
        String volunteerName,
        String volunteerContact,
        String description,
        PledgeType type,
        PledgeStatus status,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}