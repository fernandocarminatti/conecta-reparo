package com.unnamed.conectareparo.service;

import com.unnamed.conectareparo.dto.MaintenanceActionResponseDto;
import com.unnamed.conectareparo.dto.NewMaintenanceActionDto;
import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.MaintenanceAction;
import com.unnamed.conectareparo.mapper.MaintenanceActionMapper;
import com.unnamed.conectareparo.repository.MaintenanceActionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MaintenanceActionService {

    MaintenanceActionRepository maintenanceActionRepository;
    MaintenanceService maintenanceService;
    MaintenanceActionMapper maintenanceActionMapper;

    public MaintenanceActionService(MaintenanceActionRepository maintenanceActionRepository, MaintenanceService maintenanceService, MaintenanceActionMapper maintenanceActionMapper) {
        this.maintenanceActionRepository = maintenanceActionRepository;
        this.maintenanceService = maintenanceService;
        this.maintenanceActionMapper = maintenanceActionMapper;
    }

    @Transactional
    public MaintenanceActionResponseDto createMaintenanceAction(UUID maintenancePublicId, NewMaintenanceActionDto newMaintenanceActionDto) {
        Maintenance existingMaintenance = maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId);
        if (existingMaintenance.isCompleted()){
            throw new IllegalStateException("Cannot add action to a completed maintenance.");
        }
        MaintenanceAction newMaintenanceAction = maintenanceActionMapper.toEntity(newMaintenanceActionDto, existingMaintenance);
        maintenanceActionRepository.save(newMaintenanceAction);
        return maintenanceActionMapper.toResponseDto(newMaintenanceAction);
    }

    public List<MaintenanceActionResponseDto> getMaintenanceActions(UUID maintenancePublicId) {
        Maintenance existingMaintenance = maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId);
        List<MaintenanceAction> actionsList = maintenanceActionRepository.findAllByMaintenanceWithMaterials(existingMaintenance);
        return actionsList.stream()
                .map(maintenanceActionMapper::toResponseDto)
                .toList();
    }
}