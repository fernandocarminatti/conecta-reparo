package com.unnamed.conectareparo.pledge.entity;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pledge Entity Business Logic")
class PledgeTest {

    private Pledge pledge;
    private Maintenance dummyMaintenance;

    @BeforeEach
    void setUp() {
        dummyMaintenance = new Maintenance();
        pledge = new Pledge(
                dummyMaintenance,
                "Initial Volunteer",
                "initial@contact.com",
                "Initial Description",
                PledgeCategory.LABOR
        );
    }

    @Nested
    @DisplayName("updateStatus Method")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should allow valid status transition from OFFERED to PENDING")
        void shouldAllowValidTransition_PendingToAccepted() {
            pledge.updateStatus(PledgeStatus.PENDING);

            assertEquals(PledgeStatus.PENDING, pledge.getStatus());
        }

        @Test
        @DisplayName("Should allow valid status transition from PENDING to COMPLETED")
        void shouldAllowValidTransition_AcceptedToCompleted() {
            ReflectionTestUtils.setField(pledge, "status", PledgeStatus.PENDING);

            pledge.updateStatus(PledgeStatus.COMPLETED);

            assertEquals(PledgeStatus.COMPLETED, pledge.getStatus());
        }

        @ParameterizedTest
        @EnumSource(value = PledgeStatus.class, names = {"CANCELED", "COMPLETED", "REJECTED"})
        @DisplayName("Should throw IllegalStateException when changing status from a terminal state")
        void shouldThrowException_whenChangingFromTerminalState(PledgeStatus terminalStatus) {
            ReflectionTestUtils.setField(pledge, "status", terminalStatus);

            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    pledge.updateStatus(PledgeStatus.PENDING)
            );
            assertTrue(exception.getMessage().contains("Cannot change status"));
        }

        @Test
        @DisplayName("Should do nothing when new status is null")
        void shouldDoNothing_whenNewStatusIsNull() {
            PledgeStatus initialStatus = pledge.getStatus();

            pledge.updateStatus(null);

            assertEquals(initialStatus, pledge.getStatus());
        }
    }

    @Nested
    @DisplayName("updateDetails Method")
    class UpdateDetailsTests {

        @Test
        @DisplayName("Should update all provided fields")
        void shouldUpdateAllProvidedFields() {
            pledge.updateDetails("New Volunteer", "new@contact.com", "New Description", PledgeCategory.MATERIAL);

            assertAll(
                    () -> assertEquals("New Volunteer", pledge.getVolunteerName()),
                    () -> assertEquals("new@contact.com", pledge.getVolunteerContact()),
                    () -> assertEquals("New Description", pledge.getDescription()),
                    () -> assertEquals(PledgeCategory.MATERIAL, pledge.getType())
            );
        }

        @Test
        @DisplayName("Should only update non-null fields (partial update)")
        void shouldOnlyUpdateNonNullFields() {
            pledge.updateDetails("Updated Volunteer Only", null, null, null);

            assertAll(
                    () -> assertEquals("Updated Volunteer Only", pledge.getVolunteerName()),
                    () -> assertEquals("initial@contact.com", pledge.getVolunteerContact()),
                    () -> assertEquals("Initial Description", pledge.getDescription()),
                    () -> assertEquals(PledgeCategory.LABOR, pledge.getType())
            );
        }

        @Test
        @DisplayName("Should not update fields if they are blank")
        void shouldNotUpdateFields_whenTheyAreBlank() {
            pledge.updateDetails(" ", " ", " ", null);

            assertAll(
                    () -> assertEquals("Initial Volunteer", pledge.getVolunteerName()),
                    () -> assertEquals("initial@contact.com", pledge.getVolunteerContact()),
                    () -> assertEquals("Initial Description", pledge.getDescription())
            );
        }

        @Test
        @DisplayName("Should not update volunteerName when it is whitespace only")
        void shouldNotUpdateVolunteerName_whenWhitespaceOnly() {
            pledge.updateDetails("     ", "new@contact.com", "New Description", null);

            assertEquals("Initial Volunteer", pledge.getVolunteerName());
            assertEquals("new@contact.com", pledge.getVolunteerContact());
        }

        @Test
        @DisplayName("Should not update volunteerContact when it is whitespace only")
        void shouldNotUpdateVolunteerContact_whenWhitespaceOnly() {
            pledge.updateDetails("New Name", "     ", "New Description", null);

            assertEquals("New Name", pledge.getVolunteerName());
            assertEquals("initial@contact.com", pledge.getVolunteerContact());
        }

        @Test
        @DisplayName("Should not change description when blank is passed")
        void shouldNotChangeDescription_whenBlankIsPassed() {
            pledge.updateDetails("Name", "Contact", "", null);

            assertEquals("Initial Description", pledge.getDescription());
        }

        @Test
        @DisplayName("Should not change description when null is passed")
        void shouldNotChangeDescription_whenNullIsPassed() {
            pledge.updateDetails("Name", "Contact", null, null);

            assertEquals("Initial Description", pledge.getDescription());
        }

        @Test
        @DisplayName("Should update category when provided")
        void shouldUpdateCategory_whenProvided() {
            pledge.updateDetails(null, null, null, PledgeCategory.MATERIAL);

            assertEquals(PledgeCategory.MATERIAL, pledge.getType());
        }

        @Test
        @DisplayName("Should not update category when null")
        void shouldNotUpdateCategory_whenNull() {
            pledge.updateDetails(null, null, null, null);

            assertEquals(PledgeCategory.LABOR, pledge.getType());
        }
    }

    @Nested
    @DisplayName("Additional Status Transitions")
    class AdditionalStatusTransitionTests {

        @Test
        @DisplayName("Should allow OFFERED → PENDING transition")
        void shouldAllow_OfferedToPending() {
            pledge.updateStatus(PledgeStatus.PENDING);
            assertEquals(PledgeStatus.PENDING, pledge.getStatus());
        }

        @Test
        @DisplayName("Should allow OFFERED → REJECTED transition (admin rejects)")
        void shouldAllow_OfferedToRejected() {
            pledge.updateStatus(PledgeStatus.REJECTED);
            assertEquals(PledgeStatus.REJECTED, pledge.getStatus());
        }

        @Test
        @DisplayName("Should allow OFFERED → CANCELED transition (volunteer cancels)")
        void shouldAllow_OfferedToCanceled() {
            pledge.updateStatus(PledgeStatus.CANCELED);
            assertEquals(PledgeStatus.CANCELED, pledge.getStatus());
        }

        @Test
        @DisplayName("Should allow PENDING → CANCELED transition")
        void shouldAllow_PendingToCanceled() {
            ReflectionTestUtils.setField(pledge, "status", PledgeStatus.PENDING);
            pledge.updateStatus(PledgeStatus.CANCELED);
            assertEquals(PledgeStatus.CANCELED, pledge.getStatus());
        }

        @Test
        @DisplayName("Should not allow REJECTED → any other status")
        void shouldNotAllow_RejectedToAny() {
            ReflectionTestUtils.setField(pledge, "status", PledgeStatus.REJECTED);
            assertThrows(IllegalStateException.class, () -> pledge.updateStatus(PledgeStatus.PENDING));
            assertThrows(IllegalStateException.class, () -> pledge.updateStatus(PledgeStatus.COMPLETED));
        }

        @Test
        @DisplayName("Should not allow CANCELED → any other status")
        void shouldNotAllow_CanceledToAny() {
            ReflectionTestUtils.setField(pledge, "status", PledgeStatus.CANCELED);
            assertThrows(IllegalStateException.class, () -> pledge.updateStatus(PledgeStatus.PENDING));
            assertThrows(IllegalStateException.class, () -> pledge.updateStatus(PledgeStatus.COMPLETED));
        }

        @Test
        @DisplayName("Should not allow COMPLETED → any other status")
        void shouldNotAllow_CompletedToAny() {
            ReflectionTestUtils.setField(pledge, "status", PledgeStatus.COMPLETED);
            assertThrows(IllegalStateException.class, () -> pledge.updateStatus(PledgeStatus.PENDING));
            assertThrows(IllegalStateException.class, () -> pledge.updateStatus(PledgeStatus.CANCELED));
        }

        @Test
        @DisplayName("Should do nothing when new status is null")
        void shouldDoNothing_whenNewStatusIsNull() {
            PledgeStatus initialStatus = pledge.getStatus();
            pledge.updateStatus(null);
            assertEquals(initialStatus, pledge.getStatus());
        }
    }
}