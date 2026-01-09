package com.unnamed.conectareparo.maintenance.mapper;

import com.unnamed.conectareparo.maintenance.dto.MaintenanceDto;
import com.unnamed.conectareparo.maintenance.dto.MaintenanceResponseDto;
import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceMapper {

    public Maintenance toEntity(MaintenanceDto maintenanceRequestDto) {
        return new Maintenance(
                maintenanceRequestDto.title(),
                maintenanceRequestDto.description(),
                maintenanceRequestDto.category(),
                maintenanceRequestDto.scheduledDate()
        );
    }

    public MaintenanceResponseDto toResponseDto(Maintenance maintenance){
        return new MaintenanceResponseDto(
                maintenance.getPublicId(),
                maintenance.getTitle(),
                maintenance.getDescription(),
                maintenance.getCategory(),
                maintenance.getScheduledDate(),
                maintenance.getStatus(),
                maintenance.getCreatedAt(),
                maintenance.getUpdatedAt()
        );
    }
}