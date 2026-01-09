package com.unnamed.conectareparo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Maintenance entity business logic tests")
class MaintenanceTest {
    private Maintenance maintenance;

    @BeforeEach
    void setUp() {
        ;
        maintenance = new Maintenance(
                "Initial Title",
                "Initial Description",
                MaintenanceCategory.ELECTRICAL,
                ZonedDateTime.parse("2025-10-10T10:00:00Z"));
    }

    @Nested
    @DisplayName("changeStatus Method")
    class ChangeStatusTests {

        @Test
        @DisplayName("Should allow valid status transition from OPEN to IN_PROGRESS")
        void shouldAllowValidTransition_OpenToInProgress() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.OPEN);

            maintenance.changeStatus(MaintenanceStatus.IN_PROGRESS);

            assertEquals(MaintenanceStatus.IN_PROGRESS, maintenance.getStatus());
        }

        @Test
        @DisplayName("Should allow valid status transition from IN_PROGRESS to COMPLETED")
        void shouldAllowValidTransition_InProgressToCompleted() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.IN_PROGRESS);

            maintenance.changeStatus(MaintenanceStatus.COMPLETED);

            assertEquals(MaintenanceStatus.COMPLETED, maintenance.getStatus());
        }

        @ParameterizedTest
        @EnumSource(value = MaintenanceStatus.class, names = {"COMPLETED", "CANCELED"})
        @DisplayName("Should throw IllegalStateException when changing status from a terminal state")
        void shouldThrowException_whenChangingFromTerminalState(MaintenanceStatus terminalStatus) {
            ReflectionTestUtils.setField(maintenance, "status", terminalStatus);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    maintenance.changeStatus(MaintenanceStatus.OPEN)
            );
            assertTrue(exception.getMessage().contains("Cannot change status"));
        }

        @Test
        @DisplayName("Should throw IllegalStateException when reverting from IN_PROGRESS to OPEN")
        void shouldThrowException_whenRevertingFromInProgressToOpen() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.IN_PROGRESS);

            assertThrows(IllegalStateException.class, () ->
                    maintenance.changeStatus(MaintenanceStatus.OPEN)
            );
        }

        @Test
        @DisplayName("Should do nothing when new status is null")
        void shouldDoNothing_whenNewStatusIsNull() {
            MaintenanceStatus initialStatus = maintenance.getStatus();

            maintenance.changeStatus(null);

            assertEquals(initialStatus, maintenance.getStatus());
        }
    }

    @Nested
    @DisplayName("updateDetails Method")
    class UpdateDetailsTests {

        @Test
        @DisplayName("Should update all provided fields")
        void shouldUpdateAllProvidedFields() {
            maintenance.updateDetails("New Title", "New Description", MaintenanceCategory.PLUMBING);

            assertAll(
                    () -> assertEquals("New Title", maintenance.getTitle()),
                    () -> assertEquals("New Description", maintenance.getDescription()),
                    () -> assertEquals(MaintenanceCategory.PLUMBING, maintenance.getCategory())
            );
        }

        @Test
        @DisplayName("Should only update non-null fields (partial update)")
        void shouldOnlyUpdateNonNullFields() {
            maintenance.updateDetails("New Title Only", null, null);

            assertAll(
                    () -> assertEquals("New Title Only", maintenance.getTitle()),
                    () -> assertEquals("Initial Description", maintenance.getDescription()),
                    () -> assertEquals(MaintenanceCategory.ELECTRICAL, maintenance.getCategory())
            );
        }

        @Test
        @DisplayName("Should not update title if it is blank")
        void shouldNotUpdateTitle_whenItIsBlank() {
            maintenance.updateDetails(" ", "New Description", null);

            assertAll(
                    () -> assertEquals("Initial Title", maintenance.getTitle()),
                    () -> assertEquals("New Description", maintenance.getDescription())
            );
        }
    }
}