package com.unnamed.conectareparo.maintenanceaction.mapper;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionDto;
import com.unnamed.conectareparo.maintenanceaction.dto.MaintenanceActionResponseDto;
import com.unnamed.conectareparo.maintenanceaction.dto.MaterialDto;
import com.unnamed.conectareparo.maintenanceaction.dto.MaterialResponseDto;
import com.unnamed.conectareparo.maintenanceaction.entity.ActionMaterial;
import com.unnamed.conectareparo.maintenanceaction.entity.MaintenanceAction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaintenanceActionMapper {

    public MaintenanceActionResponseDto toResponseDto(MaintenanceAction action) {
        if (action == null) {
            return null;
        }

        List<MaterialResponseDto> materialDtos = action.getMaterialsUsed()
                .stream()
                .map(this::toMaterialResponseDto)
                .collect(Collectors.toList());

        return new MaintenanceActionResponseDto(
                action.getPublicId(),
                action.getExecutedBy(),
                action.getStartDate(),
                action.getCompletionDate(),
                action.getActionDescription(),
                materialDtos,
                action.getOutcomeStatus(),
                action.getCreatedAt()
        );
    }

    public MaintenanceAction toEntity(MaintenanceActionDto dto, Maintenance maintenance) {
        if (dto == null) {
            return null;
        }

        MaintenanceAction newAction = new MaintenanceAction(
                maintenance,
                dto.executedBy(),
                dto.startDate(),
                dto.completionDate(),
                dto.actionDescription(),
                dto.outcomeStatus()
        );

        if (dto.materialsUsed() != null) {
            dto.materialsUsed().forEach(materialDto -> {
                ActionMaterial newMaterial = toMaterialEntity(materialDto);
                newAction.addMaterial(newMaterial);
            });
        }

        return newAction;
    }

    public MaterialResponseDto toMaterialResponseDto(ActionMaterial material) {
        return new MaterialResponseDto(
                material.getPublicId(),
                material.getItemName(),
                material.getQuantity(),
                material.getUnitOfMeasure()
        );
    }

    public ActionMaterial toMaterialEntity(MaterialDto materialDto) {
        return new ActionMaterial(
                materialDto.itemName(),
                materialDto.quantity(),
                materialDto.unitOfMeasure()
        );
    }
}