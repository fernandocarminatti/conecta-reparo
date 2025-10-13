package com.unnamed.conectareparo.service;

import com.unnamed.conectareparo.dto.*;
import com.unnamed.conectareparo.entity.*;
import com.unnamed.conectareparo.exception.ResourceNotFoundException;
import com.unnamed.conectareparo.mapper.MaintenanceActionMapper;
import com.unnamed.conectareparo.repository.MaintenanceActionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceActionServiceTest {
    @Mock
    private MaintenanceActionRepository maintenanceActionRepository;
    @Mock
    private MaintenanceService maintenanceService;
    @Mock
    private MaintenanceActionMapper maintenanceActionMapper;

    @InjectMocks
    private MaintenanceActionService maintenanceActionService;

    private Maintenance maintenance;
    private MaintenanceAction maintenanceAction;
    private UUID maintenancePublicId;
    private UUID actionPublicId;
    private ZonedDateTime startDate;
    private ZonedDateTime completionDate;
    private List<ActionMaterial> materialList;

    @BeforeEach
    void setUp() {
        maintenancePublicId = UUID.randomUUID();
        actionPublicId = UUID.randomUUID();
        startDate = ZonedDateTime.now();
        completionDate = startDate.plusHours(1);
        materialList = List.of(
                new ActionMaterial(
                    "Sample Material 01",
                    BigDecimal.TEN, "pcs"),
                new ActionMaterial(
                        "Sample Material 02",
                        BigDecimal.TEN, "kg")
                );

        maintenance = new Maintenance();
        ReflectionTestUtils.setField(maintenance, "id", 1L);
        ReflectionTestUtils.setField(maintenance, "publicId", maintenancePublicId);
        ReflectionTestUtils.setField(maintenance, "publicId", maintenancePublicId);

        maintenanceAction = new MaintenanceAction(
                maintenance,
                "John Doe",
                startDate,
                completionDate,
                "Fixed it",
                ActionOutcomeStatus.SUCCESS);
        ReflectionTestUtils.setField(maintenanceAction, "publicId", actionPublicId);
    }

    @Test
    @DisplayName("Should create action when maintenance is OPEN")
    void createMaintenanceAction_whenMaintenanceIsOpen_shouldSucceed() {
        ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.OPEN);
        NewMaintenanceActionDto requestDto = new NewMaintenanceActionDto(
                "John Doe",
                startDate,
                completionDate,
                "Fixed it",
                Collections.emptyList(),
                ActionOutcomeStatus.SUCCESS
        );
        MaintenanceActionResponseDto responseDto = new MaintenanceActionResponseDto(
                actionPublicId,
                "John Doe",
                startDate,
                completionDate,
                "Fixed it",
                new ArrayList<>(),
                ActionOutcomeStatus.SUCCESS,
                ZonedDateTime.now()
        );

        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId)).thenReturn(maintenance);
        when(maintenanceActionMapper.toEntity(requestDto, maintenance)).thenReturn(maintenanceAction);
        when(maintenanceActionRepository.save(maintenanceAction)).thenReturn(maintenanceAction);
        when(maintenanceActionMapper.toResponseDto(maintenanceAction)).thenReturn(responseDto);

        MaintenanceActionResponseDto result = maintenanceActionService.createMaintenanceAction(maintenancePublicId, requestDto);

        assertNotNull(result);
        verify(maintenanceActionRepository).save(maintenanceAction);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when creating action for a completed Maintenance")
    void createMaintenanceAction_whenMaintenanceIsCompleted_shouldThrowException() {
        ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.COMPLETED);
        NewMaintenanceActionDto requestDto = new NewMaintenanceActionDto(
                "John Doe",
                startDate,
                completionDate,
                "Fixed it",
                Collections.emptyList(),
                ActionOutcomeStatus.SUCCESS
        );

        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId)).thenReturn(maintenance);

        assertThrows(IllegalStateException.class, () ->
                maintenanceActionService.createMaintenanceAction(maintenancePublicId, requestDto)
        );
        verify(maintenanceActionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return single action when both IDs are valid and associated")
    void getSingleMaintenanceAction_whenFound_shouldReturnDto() {
        MaintenanceActionResponseDto responseDto = new MaintenanceActionResponseDto(
                actionPublicId,
                "John Doe",
                startDate,
                completionDate,
                "Fixed it",
                Collections.emptyList(),
                ActionOutcomeStatus.SUCCESS,
                ZonedDateTime.now()
        );
        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId))
                .thenReturn(maintenance);
        when(maintenanceActionRepository.findByMaintenanceAndActionPublicId(maintenance, actionPublicId))
                .thenReturn(Optional.of(maintenanceAction));
        when(maintenanceActionMapper.toResponseDto(maintenanceAction)).thenReturn(responseDto);

        MaintenanceActionResponseDto result = maintenanceActionService.getSingleMaintenanceAction(maintenancePublicId, actionPublicId);

        assertNotNull(result);
        assertEquals(responseDto, result);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when getting single action and it's not found")
    void getSingleMaintenanceAction_whenNotFound_shouldThrowException() {
        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId))
                .thenReturn(maintenance);
        when(maintenanceActionRepository.findByMaintenanceAndActionPublicId(maintenance, actionPublicId))
                .thenThrow(new ResourceNotFoundException("Action with ID " + actionPublicId + " not found for the specified maintenance."));

        assertThrows(ResourceNotFoundException.class, () ->
                maintenanceActionService.getSingleMaintenanceAction(maintenancePublicId, actionPublicId)
        );
    }

    @Test
    @DisplayName("Should update action when maintenance is OPEN")
    void updateMaintenanceAction_whenMaintenanceIsOpen_shouldSucceed() {
        ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.OPEN);
        var updateDto = new UpdateMaintenanceActionDto(
                "Jane Doe",
                startDate.plusHours(1),
                completionDate.plusHours(1),
                "Re-fixed it",
                List.of(new NewActionMaterialDto(
                        "Sample Material 03",
                        BigDecimal.ONE, "ltr"
                )),
                ActionOutcomeStatus.PARTIAL_SUCCESS
        );
        var responseDto = new MaintenanceActionResponseDto(
                actionPublicId,
                "Jane Doe",
                startDate.plusHours(1),
                completionDate.plusHours(1),
                "Re-fixed it",
                List.of(new ActionMaterialResponseDto(
                        UUID.randomUUID(),
                        "Sample Material 03",
                        BigDecimal.ONE, "ltr"
                )),
                ActionOutcomeStatus.PARTIAL_SUCCESS,
                ZonedDateTime.now()
        );

        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId)).thenReturn(maintenance);
        when(maintenanceActionRepository.findByMaintenanceAndActionPublicId(maintenance, actionPublicId))
                .thenReturn(Optional.of(maintenanceAction));
        when(maintenanceActionRepository.save(maintenanceAction)).thenReturn(maintenanceAction);
        when(maintenanceActionMapper.toResponseDto(maintenanceAction)).thenReturn(responseDto);

        MaintenanceActionResponseDto result = maintenanceActionService.updateMaintenanceAction(maintenancePublicId, actionPublicId, updateDto);

        assertNotNull(result);
        verify(maintenanceActionRepository).save(maintenanceAction);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when updating action for a completed maintenance")
    void updateMaintenanceAction_whenMaintenanceIsCompleted_shouldThrowException() {
        ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.COMPLETED);
        UpdateMaintenanceActionDto requestDto = new UpdateMaintenanceActionDto(
                "John Doe",
                startDate,
                completionDate,
                "Fixed it",
                Collections.emptyList(),
                ActionOutcomeStatus.SUCCESS
        );

        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId)).thenReturn(maintenance);

        assertThrows(IllegalStateException.class, () ->
                maintenanceActionService.updateMaintenanceAction(maintenancePublicId, actionPublicId, requestDto)
        );
        verify(maintenanceActionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating an action that is not found")
    void updateMaintenanceAction_whenActionNotFound_shouldThrowException() {
        UpdateMaintenanceActionDto requestDto = new UpdateMaintenanceActionDto(
                "John Doe",
                startDate,
                completionDate,
                "Fixed it",
                Collections.emptyList(),
                ActionOutcomeStatus.SUCCESS
        );

        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId)).thenReturn(maintenance);
        when(maintenanceActionRepository.findByMaintenanceAndActionPublicId(maintenance, actionPublicId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                maintenanceActionService.updateMaintenanceAction(maintenancePublicId, actionPublicId, requestDto)
        );
    }
}