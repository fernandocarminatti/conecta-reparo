package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.PledgeStatus;
import com.unnamed.conectareparo.entity.PledgeType;

/**
 * Data Transfer Object for {@link Pledge} Update
 * @param volunteerName name of the volunteer making the pledge
 * @param volunteerContact contact information of the volunteer (e.g., email or phone)
 * @param description detailed description of the pledge
 * @param type type of the pledge (LABOR, MATERIAL)
 * @param status current status of the pledge
 */
public record PledgeUpdateDto(
        String volunteerName,
        String volunteerContact,
        String description,
        PledgeType type,
        PledgeStatus status
) {
}