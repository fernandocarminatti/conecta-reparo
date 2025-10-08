package com.unnamed.conectareparo.mapper;

import com.unnamed.conectareparo.dto.NewPledgeRequestDto;
import com.unnamed.conectareparo.dto.PledgeResponseDto;
import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.Pledge;
import org.springframework.stereotype.Component;

@Component
public class PledgeMapper {

    public Pledge toEntity(Maintenance maintenance, NewPledgeRequestDto pledgeRequestDto){
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