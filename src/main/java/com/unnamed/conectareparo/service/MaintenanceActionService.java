package com.unnamed.conectareparo.service;

import com.unnamed.conectareparo.dto.MaintenanceActionResponseDto;
import com.unnamed.conectareparo.dto.NewMaintenanceActionDto;
import com.unnamed.conectareparo.dto.UpdateMaintenanceActionDto;
import com.unnamed.conectareparo.entity.ActionMaterial;
import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.MaintenanceAction;
import com.unnamed.conectareparo.exception.MaintenanceAlreadyCompletedException;
import com.unnamed.conectareparo.exception.ResourceNotFoundException;
import com.unnamed.conectareparo.mapper.MaintenanceActionMapper;
import com.unnamed.conectareparo.repository.MaintenanceActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for managing Maintenance Actions.
 * This service orchestrates the creation, retrieval, and modification of maintenance action reports,
 * enforcing business rules that span across the Maintenance and MaintenanceAction domains.
 */
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

    /**
     * Creates a new maintenance action record for a given maintenance task.
     * Business rule: Actions can only be added to maintenance tasks that are not in a terminal state (e.g., COMPLETED).
     *
     * @param maintenancePublicId The public ID of the parent Maintenance task.
     * @param newMaintenanceActionDto The DTO containing the data for the new action.
     * @return A DTO representing the newly created maintenance action.
     * @throws ResourceNotFoundException if the parent Maintenance is not found.
     * @throws IllegalStateException if the parent Maintenance is already completed.
     */
    @Transactional
    public MaintenanceActionResponseDto createMaintenanceAction(UUID maintenancePublicId, NewMaintenanceActionDto newMaintenanceActionDto) {
        Maintenance existingMaintenance = maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId);
        if (existingMaintenance.isCompleted()){
            throw new MaintenanceAlreadyCompletedException("Cannot add action to a completed maintenance.");
        }
        MaintenanceAction newMaintenanceAction = maintenanceActionMapper.toEntity(newMaintenanceActionDto, existingMaintenance);
        MaintenanceAction savedMaintenance = maintenanceActionRepository.save(newMaintenanceAction);
        return maintenanceActionMapper.toResponseDto(savedMaintenance);
    }

    /**
     * Retrieves a list of all maintenance actions associated with a specific maintenance task.
     * The associated materials for each action are eagerly fetched to prevent N+1 query issues.
     *
     * @param maintenancePublicId The public ID of the parent Maintenance task.
     * @return A list of DTOs representing the maintenance actions.
     * @throws ResourceNotFoundException if the parent Maintenance is not found.
     */
    public List<MaintenanceActionResponseDto> getMaintenanceActions(UUID maintenancePublicId) {
        Maintenance existingMaintenance = maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId);
        List<MaintenanceAction> actionsList = maintenanceActionRepository.findAllByMaintenanceWithMaterials(existingMaintenance);
        return actionsList.stream()
                .map(maintenanceActionMapper::toResponseDto)
                .toList();
    }

    /**
     * Retrieves a single, specific maintenance action by its public ID, ensuring it belongs to the correct parent maintenance task.
     *
     * @param maintenancePublicId The public ID of the parent Maintenance task.
     * @param actionPublicId The public ID of the MaintenanceAction to retrieve.
     * @return A DTO representing the requested maintenance action.
     * @throws ResourceNotFoundException if the Maintenance or the specific MaintenanceAction is not found for the given parent.
     */
    public MaintenanceActionResponseDto getSingleMaintenanceAction(UUID maintenancePublicId, UUID actionPublicId) {
        Maintenance existingMaintenance = maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId);
        MaintenanceAction action = maintenanceActionRepository.findByMaintenanceAndActionPublicId(existingMaintenance, actionPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("Action with ID " + actionPublicId + " not found for the specified maintenance."));
        return maintenanceActionMapper.toResponseDto(action);
    }

    /**
     * Updates an existing maintenance action by replacing its state with the provided data.
     * This method follows a PUT-like semantic, replacing the list of materials entirely.
     * Business rule: Actions cannot be updated if the parent maintenance task is in a terminal state.
     *
     * @param maintenancePublicId The public ID of the parent Maintenance task.
     * @param actionPublicId The public ID of the MaintenanceAction to update.
     * @param updatedActionDto The DTO containing the full, updated state of the action.
     * @return A DTO representing the updated maintenance action.
     * @throws ResourceNotFoundException if the Maintenance or the specific MaintenanceAction is not found.
     * @throws IllegalStateException if the parent Maintenance is already completed.
     */
    @Transactional
    public MaintenanceActionResponseDto updateMaintenanceAction(UUID maintenancePublicId, UUID actionPublicId, UpdateMaintenanceActionDto updatedActionDto) {
        Maintenance existingMaintenance = maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId);
        if (existingMaintenance.isCompleted()){
            throw new MaintenanceAlreadyCompletedException("Cannot update action of a completed maintenance.");
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