package com.unnamed.conectareparo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MaintenanceAction Entity Business Logic")
class MaintenanceActionTest {

    private MaintenanceAction maintenanceAction;
    private ZonedDateTime now;

    @BeforeEach
    void setUp() {
        now = ZonedDateTime.parse("2025-10-10T10:00:00Z");
        maintenanceAction = new MaintenanceAction(
                new Maintenance(),
                "Initial Executor",
                now.minusHours(2),
                now,
                "Initial action description.",
                ActionStatus.SUCCESS
        );
    }

    @Nested
    @DisplayName("updateDetails Method")
    class UpdateDetailsTests {

        @Test
        @DisplayName("Should update all provided fields")
        void shouldUpdateAllProvidedFields() {
            ZonedDateTime newStartDate = now.plusDays(1);
            ZonedDateTime newCompletionDate = now.plusDays(1).plusHours(2);

            maintenanceAction.updateDetails(
                    "New Executor",
                    newStartDate,
                    newCompletionDate,
                    "Updated description.",
                    ActionStatus.PARTIAL_SUCCESS
            );

            assertAll(
                    () -> assertEquals("New Executor", maintenanceAction.getExecutedBy()),
                    () -> assertEquals(newStartDate, maintenanceAction.getStartDate()),
                    () -> assertEquals(newCompletionDate, maintenanceAction.getCompletionDate()),
                    () -> assertEquals("Updated description.", maintenanceAction.getActionDescription()),
                    () -> assertEquals(ActionStatus.PARTIAL_SUCCESS, maintenanceAction.getOutcomeStatus())
            );
        }

        @Test
        @DisplayName("Should only update non-null fields for partial update")
        void shouldOnlyUpdateNonNullFields() {
            maintenanceAction.updateDetails("Updated Executor Only", null, null, null, null);

            assertAll(
                    () -> assertEquals("Updated Executor Only", maintenanceAction.getExecutedBy()),
                    () -> assertEquals(now.minusHours(2), maintenanceAction.getStartDate()), // Unchanged
                    () -> assertEquals(now, maintenanceAction.getCompletionDate()), // Unchanged
                    () -> assertEquals("Initial action description.", maintenanceAction.getActionDescription()), // Unchanged
                    () -> assertEquals(ActionStatus.SUCCESS, maintenanceAction.getOutcomeStatus()) // Unchanged
            );
        }

        @Test
        @DisplayName("Should not update completionDate if it is before startDate")
        void shouldNotUpdateCompletionDate_whenItIsBeforeStartDate() {
            ZonedDateTime initialCompletionDate = maintenanceAction.getCompletionDate();
            ZonedDateTime invalidCompletionDate = now.minusHours(3);

            maintenanceAction.updateDetails(null, null, invalidCompletionDate, null, null);

            assertEquals(initialCompletionDate, maintenanceAction.getCompletionDate(), "Completion date should not have been updated.");
        }
    }

    @Nested
    @DisplayName("Material Management Methods")
    class MaterialManagementTests {

        @Test
        @DisplayName("addMaterial should add a material and set the bidirectional relationship")
        void addMaterial_shouldAddAndSetRelationship() {
            ActionMaterial newMaterial = new ActionMaterial("Screw", BigDecimal.TEN, "unit");
            assertTrue(maintenanceAction.getMaterialsUsed().isEmpty(), "Initial material list should be empty.");
            assertNull(newMaterial.getMaintenanceAction(), "New material should not have a parent yet.");

            maintenanceAction.addMaterial(newMaterial);

            assertAll(
                    () -> assertEquals(1, maintenanceAction.getMaterialsUsed().size()),
                    () -> assertSame(newMaterial, maintenanceAction.getMaterialsUsed().get(0)),
                    () -> assertSame(maintenanceAction, newMaterial.getMaintenanceAction(), "Parent should be set on the child.")
            );
        }

        @Test
        @DisplayName("updateMaterialsUsed should replace the entire list of materials")
        void updateMaterialsUsed_shouldReplaceEntireList() {
            ActionMaterial oldMaterial = new ActionMaterial("Old Bolt", BigDecimal.ONE, "unit");
            maintenanceAction.addMaterial(oldMaterial);
            assertEquals(1, maintenanceAction.getMaterialsUsed().size());

            ActionMaterial newMaterial1 = new ActionMaterial("New Screw", BigDecimal.TEN, "unit");
            ActionMaterial newMaterial2 = new ActionMaterial("New Nut", BigDecimal.TEN, "unit");
            List<ActionMaterial> newMaterialsList = List.of(newMaterial1, newMaterial2);

            maintenanceAction.updateMaterialsUsed(newMaterialsList);

            assertAll(
                    () -> assertEquals(2, maintenanceAction.getMaterialsUsed().size()),
                    () -> assertTrue(maintenanceAction.getMaterialsUsed().contains(newMaterial1)),
                    () -> assertTrue(maintenanceAction.getMaterialsUsed().contains(newMaterial2)),
                    () -> assertFalse(maintenanceAction.getMaterialsUsed().contains(oldMaterial), "Old material should be removed.")
            );
        }

        @Test
        @DisplayName("updateMaterialsUsed should clear the list when given an empty list")
        void updateMaterialsUsed_withEmptyList_shouldClearList() {
            maintenanceAction.addMaterial(new ActionMaterial("Item to be removed", BigDecimal.ONE, "unit"));
            assertFalse(maintenanceAction.getMaterialsUsed().isEmpty());

            maintenanceAction.updateMaterialsUsed(new ArrayList<>());

            assertTrue(maintenanceAction.getMaterialsUsed().isEmpty());
        }
    }
}