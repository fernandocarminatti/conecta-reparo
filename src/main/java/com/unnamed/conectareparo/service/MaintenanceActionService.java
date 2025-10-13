package com.unnamed.conectareparo.service;

import com.unnamed.conectareparo.dto.MaintenanceActionResponseDto;
import com.unnamed.conectareparo.dto.NewMaintenanceActionDto;
import com.unnamed.conectareparo.dto.UpdateMaintenanceActionDto;
import com.unnamed.conectareparo.entity.ActionMaterial;
import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.MaintenanceAction;
import com.unnamed.conectareparo.exception.ResourceNotFoundException;
import com.unnamed.conectareparo.mapper.MaintenanceActionMapper;
import com.unnamed.conectareparo.repository.MaintenanceActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
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
        MaintenanceAction savedMaintenance = maintenanceActionRepository.save(newMaintenanceAction);
        return maintenanceActionMapper.toResponseDto(savedMaintenance);
    }

    public List<MaintenanceActionResponseDto> getMaintenanceActions(UUID maintenancePublicId) {
        Maintenance existingMaintenance = maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId);
        List<MaintenanceAction> actionsList = maintenanceActionRepository.findAllByMaintenanceWithMaterials(existingMaintenance);
        return actionsList.stream()
                .map(maintenanceActionMapper::toResponseDto)
                .toList();
    }

    public MaintenanceActionResponseDto getSingleMaintenanceAction(UUID maintenancePublicId, UUID actionPublicId) {
        Maintenance existingMaintenance = maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId);
        MaintenanceAction action = maintenanceActionRepository.findByMaintenanceAndActionPublicId(existingMaintenance, actionPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Action with ID " + actionPublicId + " not found for the specified maintenance."));
        return maintenanceActionMapper.toResponseDto(action);
    }

    @Transactional
    public MaintenanceActionResponseDto updateMaintenanceAction(UUID maintenancePublicId, UUID actionPublicId, UpdateMaintenanceActionDto updatedActionDto) {
        Maintenance existingMaintenance = maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId);
        if (existingMaintenance.isCompleted()){
            throw new IllegalStateException("Cannot update action of a completed maintenance.");
        }
        MaintenanceAction existingAction = maintenanceActionRepository.findByMaintenanceAndActionPublicId(existingMaintenance, actionPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Action with ID " + actionPublicId + " not found for the specified maintenance."));
        existingAction.updateDetails(
                updatedActionDto.executedBy(),
                updatedActionDto.startDate(),
                updatedActionDto.completionDate(),
                updatedActionDto.actionDescription(),
                updatedActionDto.outcomeStatus()
        );
        List<ActionMaterial> newMaterials = updatedActionDto.materialsUsed().stream()
                .map(maintenanceActionMapper::toMaterialEntity)
                .toList();
        existingAction.updateMaterialsUsed(newMaterials);
        MaintenanceAction updatedAction = maintenanceActionRepository.save(existingAction);
        return maintenanceActionMapper.toResponseDto(updatedAction);
    }
}