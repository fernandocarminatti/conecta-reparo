package com.unnamed.conectareparo.mapper;

import com.unnamed.conectareparo.dto.ActionMaterialResponseDto;
import com.unnamed.conectareparo.dto.MaintenanceActionResponseDto;
import com.unnamed.conectareparo.dto.NewActionMaterialDto;
import com.unnamed.conectareparo.dto.NewMaintenanceActionDto;
import com.unnamed.conectareparo.entity.ActionMaterial;
import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.MaintenanceAction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaintenanceActionMapper {

    public MaintenanceActionResponseDto toResponseDto(MaintenanceAction action) {
        if (action == null) {
            return null;
        }

        List<ActionMaterialResponseDto> materialDtos = action.getMaterialsUsed()
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

    public MaintenanceAction toEntity(NewMaintenanceActionDto dto, Maintenance maintenance) {
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

    private ActionMaterialResponseDto toMaterialResponseDto(ActionMaterial material) {
        return new ActionMaterialResponseDto(
                material.getPublicId(),
                material.getItemName(),
                material.getQuantity(),
                material.getUnitOfMeasure()
        );
    }

    private ActionMaterial toMaterialEntity(NewActionMaterialDto materialDto) {
        return new ActionMaterial(
                materialDto.itemName(),
                materialDto.quantity(),
                materialDto.unitOfMeasure()
        );
    }
}