package com.unnamed.conectareparo.maintenanceaction.repository;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceCategory;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceStatus;
import com.unnamed.conectareparo.maintenance.repository.MaintenanceRepository;
import com.unnamed.conectareparo.maintenanceaction.entity.ActionMaterial;
import com.unnamed.conectareparo.maintenanceaction.entity.ActionStatus;
import com.unnamed.conectareparo.maintenanceaction.entity.MaintenanceAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MaintenanceAction Repository Tests")
class MaintenanceActionRepositoryTest {

    @Autowired
    private MaintenanceActionRepository maintenanceActionRepository;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    private Maintenance maintenance1;
    private Maintenance maintenance2;
    private MaintenanceAction action1;
    private MaintenanceAction action2;
    private MaintenanceAction action3;
    private UUID maintenance1PublicId;
    private UUID maintenance2PublicId;

    @BeforeEach
    void setUp() {
        maintenance1PublicId = UUID.randomUUID();
        maintenance2PublicId = UUID.randomUUID();

        maintenance1 = new Maintenance("Maintenance 1", "Description 1", MaintenanceCategory.ELECTRICAL, ZonedDateTime.now().plusDays(5));
        ReflectionTestUtils.setField(maintenance1, "publicId", maintenance1PublicId);
        ReflectionTestUtils.setField(maintenance1, "status", MaintenanceStatus.OPEN);

        maintenance2 = new Maintenance("Maintenance 2", "Description 2", MaintenanceCategory.PLUMBING, ZonedDateTime.now().plusDays(10));
        ReflectionTestUtils.setField(maintenance2, "publicId", maintenance2PublicId);
        ReflectionTestUtils.setField(maintenance2, "status", MaintenanceStatus.OPEN);

        maintenanceRepository.save(maintenance1);
        maintenanceRepository.save(maintenance2);

        action1 = new MaintenanceAction(maintenance1, "John Doe", ZonedDateTime.now().minusHours(2), ZonedDateTime.now(), "Action 1", ActionStatus.SUCCESS);
        action1.addMaterial(new ActionMaterial("Material 1", BigDecimal.TEN, "pcs"));
        maintenanceActionRepository.save(action1);

        action2 = new MaintenanceAction(maintenance1, "Jane Doe", ZonedDateTime.now().minusHours(1), ZonedDateTime.now().plusHours(1), "Action 2", ActionStatus.PARTIAL_SUCCESS);
        action2.addMaterial(new ActionMaterial("Material 2", BigDecimal.ONE, "kg"));
        maintenanceActionRepository.save(action2);

        action3 = new MaintenanceAction(maintenance2, "Bob Smith", ZonedDateTime.now(), ZonedDateTime.now().plusHours(2), "Action 3", ActionStatus.FAILURE);
        maintenanceActionRepository.save(action3);
    }

    @Nested
    @DisplayName("findAllWithMaterials() Tests")
    class FindAllWithMaterialsTests {

        @Test
        @DisplayName("Should return all actions with materials")
        void findAllWithMaterials_shouldReturnAllActions() {
            List<MaintenanceAction> result = maintenanceActionRepository.findAllWithMaterials();

            assertNotNull(result);
            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("Should eagerly fetch materials")
        void findAllWithMaterials_shouldEagerlyFetchMaterials() {
            List<MaintenanceAction> result = maintenanceActionRepository.findAllWithMaterials();

            MaintenanceAction action = result.stream()
                    .filter(a -> a.getPublicId().equals(action1.getPublicId()))
                    .findFirst()
                    .orElseThrow();

            assertNotNull(action.getMaterialsUsed());
            assertEquals(1, action.getMaterialsUsed().size());
            assertEquals("Material 1", action.getMaterialsUsed().get(0).getItemName());
        }

        @Test
        @DisplayName("Should return empty list when no actions exist")
        void findAllWithMaterials_whenNoActions_shouldReturnEmptyList() {
            maintenanceActionRepository.deleteAll();

            List<MaintenanceAction> result = maintenanceActionRepository.findAllWithMaterials();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findAllByMaintenanceWithMaterials(Maintenance) Tests")
    class FindAllByMaintenanceWithMaterialsTests {

        @Test
        @DisplayName("Should return actions for specific maintenance")
        void findAllByMaintenanceWithMaterials_shouldReturnActionsForMaintenance() {
            List<MaintenanceAction> result = maintenanceActionRepository.findAllByMaintenanceWithMaterials(maintenance1);

            assertNotNull(result);
            assertEquals(2, result.size());
            result.forEach(action -> assertEquals(maintenance1PublicId, action.getMaintenance().getPublicId()));
        }

        @Test
        @DisplayName("Should return actions with materials")
        void findAllByMaintenanceWithMaterials_shouldReturnActionsWithMaterials() {
            List<MaintenanceAction> result = maintenanceActionRepository.findAllByMaintenanceWithMaterials(maintenance1);

            MaintenanceAction action = result.stream()
                    .filter(a -> a.getPublicId().equals(action1.getPublicId()))
                    .findFirst()
                    .orElseThrow();

            assertNotNull(action.getMaterialsUsed());
            assertEquals(1, action.getMaterialsUsed().size());
        }

        @Test
        @DisplayName("Should return empty list when maintenance has no actions")
        void findAllByMaintenanceWithMaterials_whenNoActions_shouldReturnEmptyList() {
            Maintenance newMaintenance = new Maintenance("New Maintenance", "New Description", MaintenanceCategory.OTHERS, ZonedDateTime.now().plusDays(5));
            maintenanceRepository.save(newMaintenance);

            List<MaintenanceAction> result = maintenanceActionRepository.findAllByMaintenanceWithMaterials(newMaintenance);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findByMaintenanceAndActionPublicId(Maintenance, UUID) Tests")
    class FindByMaintenanceAndActionPublicIdTests {

        @Test
        @DisplayName("Should return action when found")
        void findByMaintenanceAndActionPublicId_whenFound_shouldReturnAction() {
            Optional<MaintenanceAction> result = maintenanceActionRepository.findByMaintenanceAndActionPublicId(maintenance1, action1.getPublicId());

            assertTrue(result.isPresent());
            assertEquals(action1.getPublicId(), result.get().getPublicId());
            assertEquals("Action 1", result.get().getActionDescription());
        }

        @Test
        @DisplayName("Should return empty when action not found for maintenance")
        void findByMaintenanceAndActionPublicId_whenNotFound_shouldReturnEmpty() {
            Optional<MaintenanceAction> result = maintenanceActionRepository.findByMaintenanceAndActionPublicId(maintenance1, action3.getPublicId());

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should return empty when action ID doesn't exist")
        void findByMaintenanceAndActionPublicId_whenIdNotExists_shouldReturnEmpty() {
            UUID randomId = UUID.randomUUID();
            Optional<MaintenanceAction> result = maintenanceActionRepository.findByMaintenanceAndActionPublicId(maintenance1, randomId);

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should find action belonging to different maintenance")
        void findByMaintenanceAndActionPublicId_shouldFindActionForDifferentMaintenance() {
            Optional<MaintenanceAction> result = maintenanceActionRepository.findByMaintenanceAndActionPublicId(maintenance2, action3.getPublicId());

            assertTrue(result.isPresent());
            assertEquals(action3.getPublicId(), result.get().getPublicId());
        }
    }
}
