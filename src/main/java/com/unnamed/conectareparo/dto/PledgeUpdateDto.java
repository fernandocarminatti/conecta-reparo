package com.unnamed.conectareparo.dto;

import com.unnamed.conectareparo.entity.PledgeStatus;
import com.unnamed.conectareparo.entity.PledgeType;

public record PledgeUpdateDto(
        String volunteerName,
        String volunteerContact,
        String description,
        PledgeType type,
        PledgeStatus status
) {
}