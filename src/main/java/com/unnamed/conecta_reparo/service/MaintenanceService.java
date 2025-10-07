package com.unnamed.conecta_reparo.service;

import com.unnamed.conecta_reparo.dto.MaintenanceResponseDto;
import com.unnamed.conecta_reparo.dto.NewMaintenanceRequestDto;
import com.unnamed.conecta_reparo.entity.Maintenance;
import com.unnamed.conecta_reparo.mapper.MaintenanceMapper;
import com.unnamed.conecta_reparo.repository.MaintenanceRepository;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceMapper maintenanceMapper;

    public MaintenanceService(MaintenanceRepository maintenanceRepository, MaintenanceMapper maintenanceMapper) {
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceMapper = maintenanceMapper;
    }

    public MaintenanceResponseDto createMaintenance(NewMaintenanceRequestDto maintenanceDTO) {
        Maintenance maintenance = new Maintenance(
                maintenanceDTO.title(),
                maintenanceDTO.description(),
                maintenanceDTO.category(),
                maintenanceDTO.scheduledDate()
        );
        maintenanceRepository.save(maintenance);
        return maintenanceMapper.toResponseDto(maintenance);
    }
}