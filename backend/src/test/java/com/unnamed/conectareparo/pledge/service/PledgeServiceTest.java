package com.unnamed.conectareparo.pledge.service;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceStatus;
import com.unnamed.conectareparo.maintenance.service.MaintenanceService;
import com.unnamed.conectareparo.pledge.dto.PledgeDto;
import com.unnamed.conectareparo.pledge.dto.PledgeResponseDto;
import com.unnamed.conectareparo.pledge.dto.PledgeUpdateDto;
import com.unnamed.conectareparo.pledge.entity.*;
import com.unnamed.conectareparo.pledge.mapper.PledgeMapper;
import com.unnamed.conectareparo.pledge.repository.PledgeRepository;
import com.unnamed.conectareparo.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PledgeServiceTest {
    @Mock
    private PledgeRepository pledgeRepository;
    @Mock
    private PledgeMapper pledgeMapper;
    @Mock
    private MaintenanceService maintenanceService;

    @InjectMocks
    private PledgeService pledgeService;

    private Maintenance maintenance;
    private Pledge pledge;
    private PledgeResponseDto pledgeResponseDto;
    private UUID maintenancePublicId;
    private UUID pledgePublicId;
    private ZonedDateTime fixedCreationTime;

    @BeforeEach
    void setUp() {
        maintenancePublicId = UUID.randomUUID();
        pledgePublicId = UUID.randomUUID();
        fixedCreationTime = ZonedDateTime.parse("2025-10-10T10:00:00Z");

        maintenance = new Maintenance();
        ReflectionTestUtils.setField(maintenance, "publicId", maintenancePublicId);

        pledge = new Pledge(
                maintenance,
                "John Doe",
                "555-1234",
                "I can help with painting",
                PledgeCategory.LABOR);
        ReflectionTestUtils.setField(pledge, "publicId", pledgePublicId);
        ReflectionTestUtils.setField(pledge, "status", PledgeStatus.PENDING);

        pledgeResponseDto = new PledgeResponseDto(
                pledgePublicId,
                "John Doe",
                "555-1234",
                "I can help with painting",
                null, PledgeStatus.PENDING,
                fixedCreationTime,
                fixedCreationTime
        );
    }

    @Test
    @DisplayName("Should create a pledge successfully when maintenance exists")
    void createPledge_whenMaintenanceExists_shouldCreateAndReturnDto() {
        PledgeDto requestDto = new PledgeDto(
                maintenancePublicId,
                "John Doe",
                "555-1234",
                "I can help",
                PledgeCategory.LABOR,
                PledgeStatus.OFFERED);

        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId)).thenReturn(maintenance);
        when(pledgeMapper.toEntity(maintenance, requestDto)).thenReturn(pledge);
        when(pledgeRepository.save(pledge)).thenReturn(pledge);

        when(pledgeMapper.toResponseDto(pledge)).thenReturn(pledgeResponseDto);

        PledgeResponseDto result = pledgeService.createPledge(requestDto);

        assertNotNull(result);
        assertEquals(pledgeResponseDto, result);
        verify(maintenanceService).getMaintenanceEntityByPublicId(maintenancePublicId);
        verify(pledgeRepository).save(pledge);
        verify(pledgeMapper).toResponseDto(pledge);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when creating a pledge for a non-existent maintenance")
    void createPledge_whenMaintenanceNotFound_shouldThrowException() {
        PledgeDto requestDto = new PledgeDto(
                maintenancePublicId,
                "John Doe",
                "555-1234",
                "I can help",
                PledgeCategory.LABOR,
                PledgeStatus.OFFERED);

        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId))
                .thenThrow(new ResourceNotFoundException("Maintenance not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                pledgeService.createPledge(requestDto)
        );
        verify(pledgeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return a page of pledges for a given maintenance ID")
    void getPledgesByMaintenanceId_shouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Pledge> pledgePage = new PageImpl<>(List.of(pledge), pageable, 1);

        when(pledgeRepository.findAllByMaintenancePublicId(maintenancePublicId, pageable)).thenReturn(pledgePage);
        when(pledgeMapper.toResponseDto(pledge)).thenReturn(pledgeResponseDto);

        Page<PledgeResponseDto> resultPage = pledgeService.getPledgesByMaintenanceId(pageable, maintenancePublicId);

        assertAll(
                () -> assertNotNull(resultPage),
                () -> assertEquals(1, resultPage.getTotalElements()),
                () -> assertEquals(pledgeResponseDto, resultPage.getContent().get(0))
        );
    }

    @Test
    @DisplayName("Should update a pledge successfully when found")
    void updatePledge_whenFound_shouldUpdateAndReturnDto() {
        PledgeUpdateDto updateDto = new PledgeUpdateDto(
                "Jane Doe",
                "555-5678",
                "Updated description",
                PledgeCategory.LABOR, PledgeStatus.PENDING);

        Pledge spiedPledge = spy(pledge);

        PledgeResponseDto updatedResponseDto = new PledgeResponseDto(
                pledgePublicId,
                "Jane Doe",
                "555-5678",
                "Updated description",
                PledgeCategory.LABOR,
                PledgeStatus.PENDING,
                fixedCreationTime,
                fixedCreationTime);

        when(pledgeRepository.findByPublicId(pledgePublicId)).thenReturn(Optional.of(spiedPledge));
        when(pledgeMapper.toResponseDto(spiedPledge)).thenReturn(updatedResponseDto);

        PledgeResponseDto result = pledgeService.updatePledge(pledgePublicId, updateDto);

        assertNotNull(result);
        assertEquals(updatedResponseDto, result);
        verify(spiedPledge).updateDetails(updateDto.volunteerName(), updateDto.volunteerContact(), updateDto.description(), updateDto.type());
        verify(spiedPledge).updateStatus(updateDto.status());
        verify(pledgeRepository).save(spiedPledge);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating a non-existent pledge")
    void updatePledge_whenNotFound_shouldThrowException() {
        PledgeUpdateDto updateDto = new PledgeUpdateDto(null, null, null, null, PledgeStatus.COMPLETED);

        when(pledgeRepository.findByPublicId(pledgePublicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                pledgeService.updatePledge(pledgePublicId, updateDto)
        );
        verify(pledgeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when updating a pledge in a terminal state")
    void updatePledge_whenInTerminalState_shouldThrowException() {
        PledgeUpdateDto updateDto = new PledgeUpdateDto(null, null, null, null, PledgeStatus.REJECTED);
        ReflectionTestUtils.setField(pledge, "status", PledgeStatus.COMPLETED);

        when(pledgeRepository.findByPublicId(pledgePublicId)).thenReturn(Optional.of(pledge));

        assertThrows(IllegalStateException.class, () ->
                pledgeService.updatePledge(pledgePublicId, updateDto)
        );
        verify(pledgeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when creating pledge for a completed maintenance")
    void createPledge_whenMaintenanceIsCompleted_shouldThrowException() {
        PledgeDto requestDto = new PledgeDto(
                maintenancePublicId,
                "John Doe",
                "555-1234",
                "Too late", PledgeCategory.LABOR,
                PledgeStatus.OFFERED);
        ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.COMPLETED);

        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId)).thenReturn(maintenance);

        assertThrows(IllegalStateException.class, () -> pledgeService.createPledge(requestDto));
        verify(pledgeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when creating pledge for a canceled maintenance")
    void createPledge_whenMaintenanceIsCanceled_shouldThrowException() {
        PledgeDto requestDto = new PledgeDto(
                maintenancePublicId,
                "John Doe",
                "555-1234",
                "Too late", PledgeCategory.LABOR,
                PledgeStatus.OFFERED);
        ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.CANCELED);

        when(maintenanceService.getMaintenanceEntityByPublicId(maintenancePublicId)).thenReturn(maintenance);

        assertThrows(IllegalStateException.class, () -> pledgeService.createPledge(requestDto));
        verify(pledgeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return a page of all pledges")
    void getAllPledges_shouldReturnPageOfDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pledge> pledgePage = new PageImpl<>(List.of(pledge), pageable, 1);
        when(pledgeRepository.findAll(pageable)).thenReturn(pledgePage);
        when(pledgeMapper.toResponseDto(pledge)).thenReturn(pledgeResponseDto);

        Page<PledgeResponseDto> resultPage = pledgeService.getAllPledges(pageable);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(pledgeResponseDto, resultPage.getContent().get(0));
    }

    @Test
    @DisplayName("Should return empty page when no pledges exist")
    void getAllPledges_whenNoPledges_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pledge> emptyPage = Page.empty(pageable);
        when(pledgeRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<PledgeResponseDto> resultPage = pledgeService.getAllPledges(pageable);

        assertNotNull(resultPage);
        assertTrue(resultPage.isEmpty());
        verify(pledgeMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when pledge not found by public ID")
    void getPledgeByPublicId_whenNotFound_shouldThrowException() {
        UUID randomId = UUID.randomUUID();
        when(pledgeRepository.findByPublicId(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                pledgeService.getPledgeByPublicId(randomId)
        );
        verify(pledgeMapper, never()).toResponseDto(any());
    }

    @Test
    @DisplayName("Should return pledge DTO when found by public ID")
    void getPledgeByPublicId_whenFound_shouldReturnDto() {
        when(pledgeRepository.findByPublicId(pledgePublicId)).thenReturn(Optional.of(pledge));
        when(pledgeMapper.toResponseDto(pledge)).thenReturn(pledgeResponseDto);

        PledgeResponseDto result = pledgeService.getPledgeByPublicId(pledgePublicId);

        assertNotNull(result);
        assertEquals(pledgeResponseDto, result);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when updating pledge in REJECTED state")
    void updatePledge_whenInRejectedState_shouldThrowException() {
        PledgeUpdateDto updateDto = new PledgeUpdateDto(null, null, null, null, PledgeStatus.COMPLETED);
        ReflectionTestUtils.setField(pledge, "status", PledgeStatus.REJECTED);

        when(pledgeRepository.findByPublicId(pledgePublicId)).thenReturn(Optional.of(pledge));

        assertThrows(IllegalStateException.class, () ->
                pledgeService.updatePledge(pledgePublicId, updateDto)
        );
        verify(pledgeRepository, never()).save(any());
    }
}