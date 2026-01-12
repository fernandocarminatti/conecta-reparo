package com.unnamed.conectareparo.maintenance.specification;

import com.unnamed.conectareparo.maintenance.entity.Maintenance;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceCategory;
import com.unnamed.conectareparo.maintenance.entity.MaintenanceStatus;
import com.unnamed.conectareparo.maintenance.repository.MaintenanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Maintenance Specification Tests")
class MaintenanceSpecificationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MaintenanceRepository repository;

    private Maintenance openMaintenance;
    private Maintenance inProgressMaintenance;
    private Maintenance completedMaintenance;
    private Maintenance canceledMaintenance;

    @BeforeEach
    void setUp() {
        openMaintenance = createAndPersistMaintenance("Open Task", MaintenanceStatus.OPEN, MaintenanceCategory.ELECTRICAL);
        inProgressMaintenance = createAndPersistMaintenance("In Progress Task", MaintenanceStatus.IN_PROGRESS, MaintenanceCategory.PLUMBING);
        completedMaintenance = createAndPersistMaintenance("Completed Task", MaintenanceStatus.COMPLETED, MaintenanceCategory.HVAC);
        canceledMaintenance = createAndPersistMaintenance("Canceled Task", MaintenanceStatus.CANCELED, MaintenanceCategory.BUILDING);
        entityManager.flush();
    }

    private Maintenance createAndPersistMaintenance(String title, MaintenanceStatus status, MaintenanceCategory category) {
        Maintenance maintenance = new Maintenance(title, "Description for " + title, category, ZonedDateTime.now().plusDays(1));
        maintenance.changeStatus(status);
        entityManager.persist(maintenance);
        return maintenance;
    }

    @Nested
    @DisplayName("hasStatus(String status)")
    class HasStatusTests {

        @Test
        @DisplayName("Should return all statuses when status is 'all'")
        void shouldReturnAll_whenStatusIsAll() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasStatus("all");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(4, results.size());
        }

        @Test
        @DisplayName("Should return all statuses when status is null")
        void shouldReturnAll_whenStatusIsNull() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasStatus(null);

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(4, results.size());
        }

        @Test
        @DisplayName("Should return OPEN and IN_PROGRESS when status is 'active'")
        void shouldReturnActive_whenStatusIsActive() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasStatus("active");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(2, results.size());
            assertTrue(results.stream().allMatch(m -> m.getStatus() == MaintenanceStatus.OPEN || m.getStatus() == MaintenanceStatus.IN_PROGRESS));
        }

        @Test
        @DisplayName("Should return COMPLETED and CANCELED when status is 'inactive'")
        void shouldReturnInactive_whenStatusIsInactive() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasStatus("inactive");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(2, results.size());
            assertTrue(results.stream().allMatch(m -> m.getStatus() == MaintenanceStatus.COMPLETED || m.getStatus() == MaintenanceStatus.CANCELED));
        }

        @ParameterizedTest
        @ValueSource(strings = {"OPEN", "open", "Open"})
        @DisplayName("Should return OPEN status regardless of case")
        void shouldReturnOpen_statusCaseInsensitive(String statusValue) {
            Specification<Maintenance> spec = MaintenanceSpecification.hasStatus(statusValue);

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals(MaintenanceStatus.OPEN, results.get(0).getStatus());
        }

        @Test
        @DisplayName("Should return IN_PROGRESS status")
        void shouldReturnInProgress_status() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasStatus("IN_PROGRESS");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals(MaintenanceStatus.IN_PROGRESS, results.get(0).getStatus());
        }

        @Test
        @DisplayName("Should return COMPLETED status")
        void shouldReturnCompleted_status() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasStatus("COMPLETED");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals(MaintenanceStatus.COMPLETED, results.get(0).getStatus());
        }

        @Test
        @DisplayName("Should return CANCELED status")
        void shouldReturnCanceled_status() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasStatus("CANCELED");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals(MaintenanceStatus.CANCELED, results.get(0).getStatus());
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALID_STATUS", "unknown", "PENDING"})
        @DisplayName("Should return all records for invalid status value (no filter applied)")
        void shouldReturnAll_forInvalidStatus(String invalidStatus) {
            Specification<Maintenance> spec = MaintenanceSpecification.hasStatus(invalidStatus);

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(4, results.size());
        }

        @Test
        @DisplayName("Should return all when status is empty string")
        void shouldReturnAll_whenStatusIsEmptyString() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasStatus("");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(4, results.size());
        }
    }

    @Nested
    @DisplayName("searchByTerm(String search)")
    class SearchByTermTests {

        @Test
        @DisplayName("Should return all records when search is null")
        void shouldReturnAll_whenSearchIsNull() {
            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm(null);

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(4, results.size());
        }

        @Test
        @DisplayName("Should return all records when search is empty string")
        void shouldReturnAll_whenSearchIsEmpty() {
            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm("");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(4, results.size());
        }

        @Test
        @DisplayName("Should return all records when search is whitespace only")
        void shouldReturnAll_whenSearchIsWhitespace() {
            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm("   ");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(4, results.size());
        }

        @Test
        @DisplayName("Should find maintenance by title (case insensitive)")
        void shouldFindByTitle_caseInsensitive() {
            Maintenance task = createAndPersistMaintenance("Fix The Window", MaintenanceStatus.OPEN, MaintenanceCategory.BUILDING);
            entityManager.flush();

            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm("window");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals("Fix The Window", results.get(0).getTitle());
        }

        @Test
        @DisplayName("Should find maintenance by description")
        void shouldFindByDescription() {
            Maintenance task = createAndPersistMaintenance("Some Title", MaintenanceStatus.OPEN, MaintenanceCategory.ELECTRICAL);
            entityManager.flush();

            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm("Description for Some Title");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("Should find maintenance by category name in search term")
        void shouldFindByCategoryInSearchTerm() {
            Maintenance task = createAndPersistMaintenance("Task Name", MaintenanceStatus.OPEN, MaintenanceCategory.BUILDING);
            entityManager.flush();

            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm("building");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(2, results.size());  // BUILDING from setUp + new one
        }

        @Test
        @DisplayName("Should find maintenance with partial match")
        void shouldFindWithPartialMatch() {
            Maintenance task = createAndPersistMaintenance("Test Task Alpha", MaintenanceStatus.OPEN, MaintenanceCategory.PLUMBING);
            entityManager.flush();

            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm("alpha");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("Should return empty result when no matches found")
        void shouldReturnEmpty_whenNoMatch() {
            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm("nonexistent search term xyz");

            List<Maintenance> results = repository.findAll(spec);

            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Should handle special characters in search term")
        void shouldHandleSpecialCharacters() {
            Maintenance task = createAndPersistMaintenance("Task @#$%", MaintenanceStatus.OPEN, MaintenanceCategory.ELECTRICAL);
            entityManager.flush();

            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm("@#$%");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals("Task @#$%", results.get(0).getTitle());
        }

        @Test
        @DisplayName("Should search across multiple fields (OR logic)")
        void shouldSearchAcrossMultipleFields() {
            Maintenance task = createAndPersistMaintenance("UniqueSearchTerm123", MaintenanceStatus.OPEN, MaintenanceCategory.PLUMBING);
            entityManager.flush();

            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm("UniqueSearchTerm123");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
        }
    }

    @Nested
    @DisplayName("hasCategory(String category)")
    class HasCategoryTests {

        @Test
        @DisplayName("Should return all records when category is null (no filter)")
        void shouldReturnAll_whenCategoryIsNull() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasCategory(null);

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(4, results.size());
        }

        @Test
        @DisplayName("Should return all records when category is empty string (no filter)")
        void shouldReturnAll_whenCategoryIsEmpty() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasCategory("");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(4, results.size());
        }

        @ParameterizedTest
        @ValueSource(strings = {"ELECTRICAL", "electrical", "Electrical"})
        @DisplayName("Should find by category case insensitive")
        void shouldFindByCategory_caseInsensitive(String categoryValue) {
            Specification<Maintenance> spec = MaintenanceSpecification.hasCategory(categoryValue);

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals(MaintenanceCategory.ELECTRICAL, results.get(0).getCategory());
        }

        @Test
        @DisplayName("Should find by PLUMBING category")
        void shouldFindByPlumbingCategory() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasCategory("plumbing");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals(MaintenanceCategory.PLUMBING, results.get(0).getCategory());
        }

        @Test
        @DisplayName("Should find maintenance by category name in search term")
        void shouldFindByCategoryInSearchTerm() {
            Specification<Maintenance> spec = MaintenanceSpecification.searchByTerm("hvac");

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("Should return empty for non-existent category")
        void shouldReturnEmpty_forNonExistentCategory() {
            Specification<Maintenance> spec = MaintenanceSpecification.hasCategory("NONEXISTENT");

            List<Maintenance> results = repository.findAll(spec);

            assertTrue(results.isEmpty());
        }
    }

    @Nested
    @DisplayName("Combined Specifications")
    class CombinedSpecificationTests {

        @Test
        @DisplayName("Should filter by status and category together")
        void shouldFilterByStatusAndCategory() {
            Maintenance electricalInProgress = createAndPersistMaintenance("Electrical IP", MaintenanceStatus.IN_PROGRESS, MaintenanceCategory.ELECTRICAL);
            Maintenance electricalOpen = createAndPersistMaintenance("Electrical Open", MaintenanceStatus.OPEN, MaintenanceCategory.ELECTRICAL);
            Maintenance plumbingInProgress = createAndPersistMaintenance("Plumbing IP", MaintenanceStatus.IN_PROGRESS, MaintenanceCategory.PLUMBING);
            entityManager.flush();

            Specification<Maintenance> spec = Specification.allOf(
                    MaintenanceSpecification.hasStatus("IN_PROGRESS"),
                    MaintenanceSpecification.hasCategory("electrical")
            );

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals(MaintenanceStatus.IN_PROGRESS, results.get(0).getStatus());
            assertEquals(MaintenanceCategory.ELECTRICAL, results.get(0).getCategory());
        }

        @Test
        @DisplayName("Should filter by status and search term together")
        void shouldFilterByStatusAndSearch() {
            Maintenance openTask = createAndPersistMaintenance("Searchable Open Task", MaintenanceStatus.OPEN, MaintenanceCategory.PLUMBING);
            Maintenance completedTask = createAndPersistMaintenance("Searchable Completed Task", MaintenanceStatus.COMPLETED, MaintenanceCategory.PLUMBING);
            entityManager.flush();

            Specification<Maintenance> spec = Specification.allOf(
                    MaintenanceSpecification.hasStatus("OPEN"),
                    MaintenanceSpecification.searchByTerm("Searchable")
            );

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals(MaintenanceStatus.OPEN, results.get(0).getStatus());
        }

        @Test
        @DisplayName("Should filter by all three criteria")
        void shouldFilterByAllCriteria() {
            Maintenance target = createAndPersistMaintenance("Target Task", MaintenanceStatus.IN_PROGRESS, MaintenanceCategory.ELECTRICAL);
            Maintenance other1 = createAndPersistMaintenance("Target Task", MaintenanceStatus.OPEN, MaintenanceCategory.ELECTRICAL);
            Maintenance other2 = createAndPersistMaintenance("Target Task", MaintenanceStatus.IN_PROGRESS, MaintenanceCategory.PLUMBING);
            entityManager.flush();

            Specification<Maintenance> spec = Specification.allOf(
                    MaintenanceSpecification.hasStatus("IN_PROGRESS"),
                    MaintenanceSpecification.hasCategory("ELECTRICAL"),
                    MaintenanceSpecification.searchByTerm("Target")
            );

            List<Maintenance> results = repository.findAll(spec);

            assertEquals(1, results.size());
            assertEquals(target.getId(), results.get(0).getId());
        }
    }
}
