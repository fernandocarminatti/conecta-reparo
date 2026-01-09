package com.unnamed.conectareparo.pledge.mapper;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.pledge.dto.PledgeDto;
import com.unnamed.conectareparo.pledge.dto.PledgeResponseDto;
import com.unnamed.conectareparo.pledge.entity.Pledge;
import org.springframework.stereotype.Component;

@Component
public class PledgeMapper {

    public Pledge toEntity(Maintenance maintenance, PledgeDto pledgeRequestDto) {
        return new Pledge(
                maintenance,
                pledgeRequestDto.volunteerName(),
                pledgeRequestDto.volunteerContact(),
                pledgeRequestDto.description(),
                pledgeRequestDto.type()
        );
    }

    public PledgeResponseDto toResponseDto(Pledge pledge){
        return new PledgeResponseDto(
                pledge.getPublicId(),
                pledge.getVolunteerName(),
                pledge.getVolunteerContact(),
                pledge.getDescription(),
                pledge.getType(),
                pledge.getStatus(),
                pledge.getCreatedAt(),
                pledge.getUpdatedAt()
        );
    }

}