# [ISSUE #28] Backend Package by Feature Refactoring

## Context
Refactor backend codebase from "package by layer" to "package by feature" architecture to improve code organization, maintainability, and domain-driven design alignment.

## Current State
The backend currently uses a layered architecture with directories like:
- `src/main/java/com/conectareparo/controller/`
- `src/main/java/com/conectareparo/service/`
- `src/main/java/com/conectareparo/repository/`
- `src/main/java/com/conectareparo/entity/`

## Target State
Package by feature architecture where each feature is self-contained:

```
backend/src/main/java/com/conectareparo/
├── maintenance/              # Maintenance feature
│   ├── controller/
│   │   └── MaintenanceController.java
│   ├── service/
│   │   └── MaintenanceService.java
│   ├── repository/
│   │   └── MaintenanceRepository.java
│   ├── entity/
│   │   └── Maintenance.java
│   ├── dto/
│   │   ├── MaintenanceRequest.java
│   │   └── MaintenanceResponse.java
│   └── exception/
│       └── MaintenanceNotFoundException.java
├── pledge/                   # Pledge feature
│   ├── controller/
│   │   └── PledgeController.java
│   ├── service/
│   │   └── PledgeService.java
│   ├── repository/
│   │   └── PledgeRepository.java
│   ├── entity/
│   │   └── Pledge.java
│   ├── dto/
│   │   ├── PledgeRequest.java
│   │   └── PledgeResponse.java
│   └── exception/
│       └── PledgeNotFoundException.java
├── admin/                    # Admin feature
│   ├── controller/
│   │   └── AdminController.java
│   ├── service/
│   │   └── AdminService.java
│   └── dto/
│       └── AdminStatsResponse.java
└── common/                   # Shared code across features
    ├── config/
    ├── exception/
    │   └── GlobalExceptionHandler.java
    └── util/
        └── DateUtils.java
```

## Tasks

### Phase 1: Planning and Preparation
- [ ] Create backup branch: `git checkout -b feature/backend-package-by-feature`
- [ ] Analyze current codebase structure
- [ ] Identify all entities, services, controllers, and repositories
- [ ] Map dependencies between layers

### Phase 2: Create Feature Packages
- [ ] Create `maintenance/` package structure
  - [ ] Move `Maintenance` entity
  - [ ] Move/create `MaintenanceController`
  - [ ] Move/create `MaintenanceService`
  - [ ] Move/create `MaintenanceRepository`
  - [ ] Create DTOs (request/response)
  - [ ] Create feature-specific exceptions
- [ ] Create `pledge/` package structure
  - [ ] Move `Pledge` entity
  - [ ] Move/create `PledgeController`
  - [ ] Move/create `PledgeService`
  - [ ] Move/create `PledgeRepository`
  - [ ] Create DTOs (request/response)
  - [ ] Create feature-specific exceptions
- [ ] Create `admin/` package structure (if applicable)
- [ ] Create `common/` package for shared utilities

### Phase 3: Update Dependencies and Imports
- [ ] Update all import statements
- [ ] Update Spring Boot component scanning (if needed)
- [ ] Verify all @EntityScan annotations
- [ ] Update package references in configuration classes

### Phase 4: Testing
- [ ] Run existing unit tests
- [ ] Run existing integration tests
- [ ] Verify all API endpoints still work
- [ ] Verify database migrations still apply correctly

### Phase 5: Cleanup
- [ ] Remove old layer-based directories
- [ ] Update `.gitignore` if needed
- [ ] Update documentation
- [ ] Commit refactored code

## Implementation Steps

### Step 1: Create Feature Directory Structure
```bash
cd backend/src/main/java/com/conectareparo
mkdir -p maintenance/controller maintenance/service maintenance/repository \
       maintenance/entity maintenance/dto maintenance/exception
mkdir -p pledge/controller pledge/service pledge/repository \
       pledge/entity pledge/dto pledge/exception
mkdir -p admin/controller admin/service admin/dto
mkdir -p common/config common/exception common/util
```

### Step 2: Move or Copy Files to Feature Packages

For each entity and related code, move files to appropriate feature package:

```bash
# Example: Move maintenance-related files
mv controller/MaintenanceController.java maintenance/controller/
mv service/MaintenanceService.java maintenance/service/
mv repository/MaintenanceRepository.java maintenance/repository/
mv entity/Maintenance.java maintenance/entity/
```

### Step 3: Create DTOs for Each Feature

Create request and response DTOs to encapsulate API contracts:

```java
// maintenance/dto/MaintenanceRequest.java
public record MaintenanceRequest(
    String title,
    String description,
    String priority,
    String status,
    LocalDateTime createdAt
) {}

// maintenance/dto/MaintenanceResponse.java  
public record MaintenanceResponse(
    Long id,
    String title,
    String description,
    String priority,
    String status,
    LocalDateTime createdAt
) {}
```

### Step 4: Update Service Layer

Update services to use feature-specific repositories and DTOs:

```java
// maintenance/service/MaintenanceService.java
@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {
    private final MaintenanceRepository repository;
    
    public MaintenanceResponse create(MaintenanceRequest request) {
        Maintenance entity = new Maintenance();
        // map request to entity
        Maintenance saved = repository.save(entity);
        return mapToResponse(saved);
    }
    
    private MaintenanceResponse mapToResponse(Maintenance entity) {
        return new MaintenanceResponse(
            entity.getId(),
            entity.getTitle(),
            // ...
        );
    }
}
```

### Step 5: Update Controllers

Update controllers to use feature services:

```java
// maintenance/controller/MaintenanceController.java
@RestController
@RequestMapping("/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {
    private final MaintenanceService service;
    
    @GetMapping
    public ResponseEntity<List<MaintenanceResponse>> list() {
        return ResponseEntity.ok(service.list());
    }
    
    @PostMapping
    public ResponseEntity<MaintenanceResponse> create(
            @Valid @RequestBody MaintenanceRequest request) {
        return ResponseEntity.created(URI.create("/maintenances"))
            .body(service.create(request));
    }
}
```

### Step 6: Handle Cross-Cutting Concerns

Move or update exception handlers and configuration:

```java
// common/exception/GlobalExceptionHandler.java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MaintenanceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(MaintenanceNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
```

## Acceptance Criteria

- [ ] Backend follows package-by-feature architecture
- [ ] Each feature (maintenance, pledge, admin) is self-contained
- [ ] Common utilities moved to `common/` package
- [ ] All existing tests pass
- [ ] All API endpoints work correctly
- [ ] Code compiles without errors
- [ ] Documentation updated with new package structure

## Definition of Done

- [ ] All code refactored to feature packages
- [ ] No circular dependencies between features
- [ ] Unit tests pass (mvn test)
- [ ] Integration tests pass
- [ ] Code compiles successfully (mvn compile)
- [ ] No deprecated layer-based directories remain
- [ ] Git history preserved (use git mv for moves)
- [ ] Changes committed and reviewed

## Notes

- This is a refactoring task, not a feature implementation
- Keep all existing functionality intact
- Use `git mv` to preserve file history where possible
- Consider using Spring Boot's automatic component scanning (no @ComponentScan changes needed if packages are under main.java.com.conectareparo)
- This change improves:
  - Code navigation (related code in one place)
  - Feature isolation (changes contained to one package)
  - Team collaboration (different developers can work on different features)
  - Testing (easier to test features in isolation)
  - Scalability (new features added easily)

## Related Issues

- #27: Test Coverage for Backend Package Refactor
- #10: Módulo de Gestão de Manutenções (related feature)
- #8: Módulo de Engajamento Comunitário (related feature)
