package com.unnamed.conectareparo.service;

import com.unnamed.conectareparo.dto.MaintenanceDto;
import com.unnamed.conectareparo.dto.MaintenanceResponseDto;
import com.unnamed.conectareparo.dto.MaintenanceUpdateDto;
import com.unnamed.conectareparo.entity.Maintenance;
import com.unnamed.conectareparo.entity.MaintenanceCategory;
import com.unnamed.conectareparo.entity.MaintenanceStatus;
import com.unnamed.conectareparo.exception.ResourceNotFoundException;
import com.unnamed.conectareparo.mapper.MaintenanceMapper;
import com.unnamed.conectareparo.repository.MaintenanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceTest {

    @Mock
    private MaintenanceRepository maintenanceRepository;
    @Mock
    private MaintenanceMapper maintenanceMapper;
    @InjectMocks
    private MaintenanceService maintenanceService;

    private Maintenance persistedMaintenance;
    private MaintenanceResponseDto persistedMaintenanceResponseDto;
    private UUID publicId;
    private ZonedDateTime fixedCreationTime;
    private ZonedDateTime scheduledDate;

    @BeforeEach
    void setUp() {
        fixedCreationTime = ZonedDateTime.parse("2025-10-08T10:00:00Z");
        publicId = UUID.fromString("a1b2c3d4-e5f6-a7b8-c9d0-e1f2a3b4c5d6");
        scheduledDate = fixedCreationTime.plusDays(1);

        persistedMaintenance = new Maintenance("Test Title", "Test Description", MaintenanceCategory.ELECTRICAL, scheduledDate);
        ReflectionTestUtils.setField(persistedMaintenance, "publicId", publicId);
        ReflectionTestUtils.setField(persistedMaintenance, "status", MaintenanceStatus.OPEN);
        ReflectionTestUtils.setField(persistedMaintenance, "createdAt", fixedCreationTime);
        ReflectionTestUtils.setField(persistedMaintenance, "updatedAt", fixedCreationTime);

        persistedMaintenanceResponseDto = new MaintenanceResponseDto(
                publicId,
                "Test Title",
                "Test Description",
                MaintenanceCategory.ELECTRICAL,
                scheduledDate,
                MaintenanceStatus.OPEN,
                fixedCreationTime,
                fixedCreationTime
        );
    }

    @DisplayName("Get Maintenance by Public ID - Not Found Exception")
    @Test
    void getMaintenanceByPublicId_whenNotFound_shouldThrowResourceNotFoundException() {
        UUID publicId = UUID.randomUUID();

        when(maintenanceRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            maintenanceService.getMaintenanceByPublicId(publicId);
        });
        verify(maintenanceRepository, times(1)).findByPublicId(publicId);
        verify(maintenanceMapper, never()).toResponseDto(any());
    }


    @DisplayName("Get Empty Page of Maintenances")
    @Test
    void getAllMaintenances_whenNoMaintenances_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Maintenance> emptyPage = Page.empty(pageable);

        when(maintenanceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<MaintenanceResponseDto> resultPage = maintenanceService.getAllMaintenances(null, null, pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        verify(maintenanceRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(maintenanceMapper, never()).toResponseDto(any());
    }

    @DisplayName("Update Maintenance - Not Found Exception")
    @Test
    void updateMaintenance_whenNotFound_shouldThrowResourceNotFoundException() {
        UUID publicId = UUID.randomUUID();
        MaintenanceUpdateDto updateDto = new MaintenanceUpdateDto("Updated Title", null, null, null);

        when(maintenanceRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            maintenanceService.updateMaintenance(publicId, updateDto);
        });
        verify(maintenanceRepository, never()).save(any());
        verify(maintenanceMapper, never()).toResponseDto(any());
    }

    @DisplayName("Get Maintenance Entity by Public ID - Found")
    @Test
    void getMaintenanceEntityByPublicId_whenFound_shouldReturnEntity() {
        UUID publicId = UUID.randomUUID();

        when(maintenanceRepository.findByPublicId(publicId)).thenReturn(Optional.of(persistedMaintenance));

        Maintenance actualMaintenance = maintenanceService.getMaintenanceEntityByPublicId(publicId);

        assertNotNull(actualMaintenance);
        assertSame(persistedMaintenance, actualMaintenance);
        verify(maintenanceRepository, times(1)).findByPublicId(publicId);
    }

    @DisplayName("Get Maintenance Entity by Public ID - Not Found Exception")
    @Test
    void getMaintenanceEntityByPublicId_whenNotFound_shouldThrowResourceNotFoundException() {
        UUID publicId = UUID.randomUUID();

        when(maintenanceRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            maintenanceService.getMaintenanceEntityByPublicId(publicId);
        });
        verify(maintenanceRepository, times(1)).findByPublicId(publicId);
    }

    @Test
    @DisplayName("Should create a maintenance record successfully")
    void createMaintenance_shouldSaveAndReturnDto() {
        MaintenanceDto requestDto = new MaintenanceDto("Test Title", "Test Description", MaintenanceCategory.ELECTRICAL, scheduledDate);
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(persistedMaintenance);
        when(maintenanceMapper.toResponseDto(any(Maintenance.class))).thenReturn(persistedMaintenanceResponseDto);

        MaintenanceResponseDto actualDto = maintenanceService.createMaintenance(requestDto);

        assertNotNull(actualDto);
        assertEquals(persistedMaintenanceResponseDto, actualDto);
        verify(maintenanceRepository).save(any(Maintenance.class));
        verify(maintenanceMapper).toResponseDto(any(Maintenance.class));
    }

    @Test
    @DisplayName("Should return a maintenance DTO when public ID is found")
    void getMaintenanceByPublicId_whenFound_shouldReturnDto() {
        when(maintenanceRepository.findByPublicId(publicId)).thenReturn(Optional.of(persistedMaintenance));
        when(maintenanceMapper.toResponseDto(persistedMaintenance)).thenReturn(persistedMaintenanceResponseDto);

        MaintenanceResponseDto actualDto = maintenanceService.getMaintenanceByPublicId(publicId);

        assertNotNull(actualDto);
        assertEquals(persistedMaintenanceResponseDto, actualDto);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when public ID is not found")
    void getMaintenanceByPublicId_whenNotFound_shouldThrowException() {
        when(maintenanceRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                maintenanceService.getMaintenanceByPublicId(publicId)
        );
        verify(maintenanceMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should return a page of maintenance DTOs")
    void getAllMaintenances_shouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Maintenance> maintenancePage = new PageImpl<>(List.of(persistedMaintenance), pageable, 1);
        when(maintenanceRepository.findAll(any(Specification.class), eq(pageable
        ))).thenReturn(maintenancePage);
        when(maintenanceMapper.toResponseDto(persistedMaintenance)).thenReturn(persistedMaintenanceResponseDto);

        Page<MaintenanceResponseDto> resultPage = maintenanceService.getAllMaintenances(null, null, pageable);

        assertAll(
                () -> assertNotNull(resultPage),
                () -> assertEquals(1, resultPage.getTotalElements()),
                () -> assertEquals(persistedMaintenanceResponseDto, resultPage.getContent().get(0))
        );
    }

    @Test
    @DisplayName("Should successfully update a maintenance record")
    void updateMaintenance_whenFound_shouldUpdateAndReturnDto() {
        MaintenanceUpdateDto updateDto = new MaintenanceUpdateDto(
                "Updated Title",
                "Updated Desc",
                MaintenanceCategory.HVAC,
                MaintenanceStatus.IN_PROGRESS
        );
        Maintenance spiedMaintenance = spy(persistedMaintenance);
        MaintenanceResponseDto expectedResponseDto = new MaintenanceResponseDto(
                publicId, "Updated Title", "Updated Desc", MaintenanceCategory.HVAC,
                scheduledDate, MaintenanceStatus.IN_PROGRESS, fixedCreationTime, ZonedDateTime.now() // `updatedAt` will be different
        );
        when(maintenanceRepository.findByPublicId(publicId)).thenReturn(Optional.of(spiedMaintenance));
        when(maintenanceRepository.save(spiedMaintenance)).thenReturn(spiedMaintenance);
        when(maintenanceMapper.toResponseDto(spiedMaintenance)).thenReturn(expectedResponseDto);

        MaintenanceResponseDto actualResponseDto = maintenanceService.updateMaintenance(publicId, updateDto);

        assertNotNull(actualResponseDto);
        assertEquals(expectedResponseDto, actualResponseDto);
        verify(spiedMaintenance).updateDetails(updateDto.title(), updateDto.description(), updateDto.category());
        verify(spiedMaintenance).changeStatus(updateDto.status());
        verify(maintenanceRepository).save(spiedMaintenance);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when updating a completed maintenance")
    void updateMaintenance_whenStatusTransitionIsInvalid_shouldThrowException() {
        MaintenanceUpdateDto updateDto = new MaintenanceUpdateDto(null, null, null, MaintenanceStatus.OPEN);

        Maintenance completedMaintenance = spy(new Maintenance());
        ReflectionTestUtils.setField(completedMaintenance, "status", MaintenanceStatus.COMPLETED); // Set its state

        when(maintenanceRepository.findByPublicId(publicId)).thenReturn(Optional.of(completedMaintenance));

        assertThrows(IllegalStateException.class, () -> {
            maintenanceService.updateMaintenance(publicId, updateDto);
        });

        verify(maintenanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate exception when repository fails to save")
    void createMaintenance_whenRepositoryFails_shouldThrowException() {
        MaintenanceDto requestDto = new MaintenanceDto("Test Title", "Test Desc", null, null);

        when(maintenanceRepository.save(any(Maintenance.class)))
                .thenThrow(new DataIntegrityViolationException("DB Error"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            maintenanceService.createMaintenance(requestDto);
        });
    }
}