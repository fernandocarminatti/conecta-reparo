package com.unnamed.conectareparo.maintenance.entity;

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

        @Test
        @DisplayName("Should not update title when it contains only whitespace")
        void shouldNotUpdateTitle_whenItIsOnlyWhitespace() {
            maintenance.updateDetails("     ", "New Description", null);

            assertAll(
                    () -> assertEquals("Initial Title", maintenance.getTitle()),
                    () -> assertEquals("New Description", maintenance.getDescription())
            );
        }

        @Test
        @DisplayName("Should not change description when null is passed")
        void shouldNotChangeDescription_whenNullIsPassed() {
            Maintenance newMaintenance = new Maintenance("Title", "Original Description", MaintenanceCategory.ELECTRICAL, ZonedDateTime.now());
            newMaintenance.updateDetails("New Title", null, null);

            assertAll(
                    () -> assertEquals("New Title", newMaintenance.getTitle()),
                    () -> assertEquals("Original Description", newMaintenance.getDescription())
            );
        }
    }

    @Nested
    @DisplayName("isCompleted Method")
    class IsCompletedTests {

        @Test
        @DisplayName("Should return true when status is COMPLETED")
        void isCompleted_shouldReturnTrue_whenStatusIsCompleted() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.COMPLETED);

            assertTrue(maintenance.isCompleted());
        }

        @Test
        @DisplayName("Should return false when status is OPEN")
        void isCompleted_shouldReturnFalse_whenStatusIsOpen() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.OPEN);

            assertFalse(maintenance.isCompleted());
        }

        @Test
        @DisplayName("Should return false when status is IN_PROGRESS")
        void isCompleted_shouldReturnFalse_whenStatusIsInProgress() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.IN_PROGRESS);

            assertFalse(maintenance.isCompleted());
        }

        @Test
        @DisplayName("Should return false when status is CANCELED")
        void isCompleted_shouldReturnFalse_whenStatusIsCanceled() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.CANCELED);

            assertFalse(maintenance.isCompleted());
        }
    }

    @Nested
    @DisplayName("Additional Status Transitions")
    class AdditionalStatusTransitionTests {

        @Test
        @DisplayName("Should allow status transition from OPEN to CANCELED")
        void shouldAllow_OpenToCanceled() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.OPEN);

            maintenance.changeStatus(MaintenanceStatus.CANCELED);

            assertEquals(MaintenanceStatus.CANCELED, maintenance.getStatus());
        }

        @Test
        @DisplayName("Should allow status transition from IN_PROGRESS to CANCELED")
        void shouldAllow_InProgressToCanceled() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.IN_PROGRESS);

            maintenance.changeStatus(MaintenanceStatus.CANCELED);

            assertEquals(MaintenanceStatus.CANCELED, maintenance.getStatus());
        }

        @Test
        @DisplayName("Should allow status transition from OPEN to COMPLETED directly")
        void shouldAllow_OpenToCompleted() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.OPEN);

            maintenance.changeStatus(MaintenanceStatus.COMPLETED);

            assertEquals(MaintenanceStatus.COMPLETED, maintenance.getStatus());
        }

        @Test
        @DisplayName("Should allow status transition from CANCELED to IN_PROGRESS (edge case)")
        void shouldNotAllow_CanceledToInProgress() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.CANCELED);

            assertThrows(IllegalStateException.class, () ->
                    maintenance.changeStatus(MaintenanceStatus.IN_PROGRESS)
            );
        }

        @Test
        @DisplayName("Should allow status transition from COMPLETED to IN_PROGRESS (edge case)")
        void shouldNotAllow_CompletedToInProgress() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.COMPLETED);

            assertThrows(IllegalStateException.class, () ->
                    maintenance.changeStatus(MaintenanceStatus.IN_PROGRESS)
            );
        }

        @Test
        @DisplayName("Should not allow status transition from COMPLETED to CANCELED")
        void shouldNotAllow_CompletedToCanceled() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.COMPLETED);

            assertThrows(IllegalStateException.class, () ->
                    maintenance.changeStatus(MaintenanceStatus.CANCELED)
            );
        }

        @Test
        @DisplayName("Should not allow status transition from CANCELED to COMPLETED")
        void shouldNotAllow_CanceledToCompleted() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.CANCELED);

            assertThrows(IllegalStateException.class, () ->
                    maintenance.changeStatus(MaintenanceStatus.COMPLETED)
            );
        }

        @Test
        @DisplayName("Should not allow status transition from CANCELED to OPEN")
        void shouldNotAllow_CanceledToOpen() {
            ReflectionTestUtils.setField(maintenance, "status", MaintenanceStatus.CANCELED);

            assertThrows(IllegalStateException.class, () ->
                    maintenance.changeStatus(MaintenanceStatus.OPEN)
            );
        }
    }
}