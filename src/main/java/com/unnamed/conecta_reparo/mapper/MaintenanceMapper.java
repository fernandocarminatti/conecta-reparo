package com.unnamed.conecta_reparo.mapper;

import com.unnamed.conecta_reparo.dto.MaintenanceResponseDto;
import com.unnamed.conecta_reparo.dto.NewMaintenanceRequestDto;
import com.unnamed.conecta_reparo.entity.Maintenance;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceMapper {

    public Maintenance toEntity(NewMaintenanceRequestDto maintenanceRequestDto){
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