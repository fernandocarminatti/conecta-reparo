package com.unnamed.conectareparo.pledge.repository;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceCategory;
import com.unnamed.conectareparo.maintenance.repository.MaintenanceRepository;
import com.unnamed.conectareparo.pledge.entity.Pledge;
import com.unnamed.conectareparo.pledge.entity.PledgeCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Pledge Repository Tests")
class PledgeRepositoryTest {

    @Autowired
    private PledgeRepository pledgeRepository;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    private Maintenance maintenance1;
    private Maintenance maintenance2;
    private Pledge pledge1;
    private Pledge pledge2;
    private Pledge pledge3;
    private UUID maintenance1PublicId;
    private UUID maintenance2PublicId;

    @BeforeEach
    void setUp() {
        maintenance1PublicId = UUID.randomUUID();
        maintenance2PublicId = UUID.randomUUID();

        maintenance1 = new Maintenance("Test Maintenance 1", "Description 1", MaintenanceCategory.ELECTRICAL, ZonedDateTime.now().plusDays(5));
        ReflectionTestUtils.setField(maintenance1, "publicId", maintenance1PublicId);

        maintenance2 = new Maintenance("Test Maintenance 2", "Description 2", MaintenanceCategory.PLUMBING, ZonedDateTime.now().plusDays(10));
        ReflectionTestUtils.setField(maintenance2, "publicId", maintenance2PublicId);

        maintenanceRepository.save(maintenance1);
        maintenanceRepository.save(maintenance2);

        pledge1 = new Pledge(maintenance1, "Volunteer 1", "contact1@example.com", "Can help with electrical", PledgeCategory.LABOR);
        pledge2 = new Pledge(maintenance1, "Volunteer 2", "contact2@example.com", "Can provide materials", PledgeCategory.MATERIAL);
        pledge3 = new Pledge(maintenance2, "Volunteer 3", "contact3@example.com", "Can help with plumbing", PledgeCategory.LABOR);

        pledgeRepository.save(pledge1);
        pledgeRepository.save(pledge2);
        pledgeRepository.save(pledge3);
    }

    @Nested
    @DisplayName("findAll(Pageable) Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all pledges with pagination")
        void findAll_shouldReturnAllPledges() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pledge> result = pledgeRepository.findAll(pageable);

            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
            assertEquals(3, result.getContent().size());
        }

        @Test
        @DisplayName("Should return paginated results")
        void findAll_shouldReturnPaginatedResults() {
            Pageable pageable = PageRequest.of(0, 2);
            Page<Pledge> result = pledgeRepository.findAll(pageable);

            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            assertEquals(2, result.getTotalPages());
        }

        @Test
        @DisplayName("Should return empty page when no pledges exist")
        void findAll_whenNoPledges_shouldReturnEmptyPage() {
            pledgeRepository.deleteAll();

            Pageable pageable = PageRequest.of(0, 10);
            Page<Pledge> result = pledgeRepository.findAll(pageable);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("findByPublicId(UUID) Tests")
    class FindByPublicIdTests {

        @Test
        @DisplayName("Should return pledge when found by public ID")
        void findByPublicId_whenFound_shouldReturnPledge() {
            Optional<Pledge> result = pledgeRepository.findByPublicId(pledge1.getPublicId());

            assertTrue(result.isPresent());
            assertEquals(pledge1.getPublicId(), result.get().getPublicId());
            assertEquals("Volunteer 1", result.get().getVolunteerName());
        }

        @Test
        @DisplayName("Should return empty when pledge not found")
        void findByPublicId_whenNotFound_shouldReturnEmpty() {
            UUID randomId = UUID.randomUUID();
            Optional<Pledge> result = pledgeRepository.findByPublicId(randomId);

            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("findAllByMaintenancePublicId(UUID, Pageable) Tests")
    class FindAllByMaintenancePublicIdTests {

        @Test
        @DisplayName("Should return pledges for specific maintenance")
        void findAllByMaintenancePublicId_shouldReturnPledgesForMaintenance() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pledge> result = pledgeRepository.findAllByMaintenancePublicId(maintenance1PublicId, pageable);

            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            result.getContent().forEach(pledge -> {
                assertEquals(maintenance1PublicId, pledge.getMaintenanceId().getPublicId());
            });
        }

        @Test
        @DisplayName("Should return empty page when maintenance has no pledges")
        void findAllByMaintenancePublicId_whenNoPledges_shouldReturnEmptyPage() {
            Maintenance newMaintenance = new Maintenance("New Maintenance", "New Description", MaintenanceCategory.OTHERS, ZonedDateTime.now().plusDays(5));
            UUID newMaintenancePublicId = UUID.randomUUID();
            ReflectionTestUtils.setField(newMaintenance, "publicId", newMaintenancePublicId);

            Pageable pageable = PageRequest.of(0, 10);
            Page<Pledge> result = pledgeRepository.findAllByMaintenancePublicId(newMaintenancePublicId, pageable);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());
        }

        @Test
        @DisplayName("Should return pledges for different maintenance")
        void findAllByMaintenancePublicId_shouldReturnPledgesForDifferentMaintenance() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pledge> result = pledgeRepository.findAllByMaintenancePublicId(maintenance2PublicId, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            assertEquals(pledge3.getPublicId(), result.getContent().get(0).getPublicId());
        }

        @Test
        @DisplayName("Should paginate results correctly")
        void findAllByMaintenancePublicId_shouldPaginateResults() {
            Pledge pledge4 = new Pledge(maintenance1, "Volunteer 4", "contact4@example.com", "Another pledge", PledgeCategory.LABOR);
            Pledge pledge5 = new Pledge(maintenance1, "Volunteer 5", "contact5@example.com", "Yet another pledge", PledgeCategory.MATERIAL);
            pledgeRepository.save(pledge4);
            pledgeRepository.save(pledge5);

            Pageable pageable = PageRequest.of(0, 2);
            Page<Pledge> result = pledgeRepository.findAllByMaintenancePublicId(maintenance1PublicId, pageable);

            assertNotNull(result);
            assertEquals(4, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            assertEquals(2, result.getTotalPages());
        }
    }
}
