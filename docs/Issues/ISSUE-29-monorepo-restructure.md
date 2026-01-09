# [ISSUE #29] Repository Monorepo Restructure

## Context
Restructure the repository from a single-level structure to a monorepo with separate `backend/` and `frontend/` directories. This enables independent development and deployment of backend and frontend while keeping them in a single repository for hospital IT deployment convenience.

## Current State
```
conecta-reparo/
├── src/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
├── docker-compose.yml
└── README.md
```

## Target State
```
conecta-reparo/
├── backend/              # Spring Boot API
│   ├── src/
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── Dockerfile
├── frontend/             # Next.js application (to be initialized)
├── nginx/                # Nginx configuration
├── docker-compose.yml
├── .env.example
└── README.md
```

## Tasks

### Pre-requisites
- [ ] Create backup branch: `git checkout -b feature/monorepo-setup`
- [ ] Ensure all changes are committed in main branch
- [ ] Backup important files

### Backend Migration
- [ ] Create `backend/` directory
- [ ] Move `src/` directory to `backend/src/`
- [ ] Move `pom.xml` to `backend/`
- [ ] Move `mvnw` to `backend/`
- [ ] Move `mvnw.cmd` to `backend/`
- [ ] Create `backend/.gitkeep` if needed

### Gitignore Updates
- [ ] Update root `.gitignore` to exclude backend build artifacts
- [ ] Create/update `backend/.gitignore` for backend-specific ignores

### Directory Structure Creation
- [ ] Create `frontend/` directory (empty, for Next.js initialization)
- [ ] Create `nginx/` directory (empty, for configuration files)

### Root Level Files
- [ ] Keep `docker-compose.yml` at root level
- [ ] Create `.env.example` at root level
- [ ] Update root `README.md` with new structure overview

### Verification
- [ ] Test backend builds: `cd backend && ./mvnw clean package`
- [ ] Verify no files are missing
- [ ] Test docker-compose build works
- [ ] Commit changes

## Implementation Steps

### Step 1: Create Backend Directory and Move Files
```bash
# Create backend directory
mkdir -p backend

# Move backend-specific files
git mv src backend/
git mv pom.xml backend/
git mv mvnw backend/
git mv mvnw.cmd backend/
```

### Step 2: Create Frontend Directory
```bash
mkdir -p frontend
touch frontend/.gitkeep
```

### Step 3: Create Nginx Directory
```bash
mkdir -p nginx
touch nginx/.gitkeep
```

### Step 4: Update Gitignore Files

Update root `.gitignore`:
```gitignore
# Backend
backend/target/
backend/.mvn/wrapper/maven-wrapper.jar

# Frontend
frontend/node_modules/
frontend/.next/
frontend/out/

# Environment
.env
.env.local
.env.*.local

# IDE
.idea/
.vscode/
*.swp
*.swo

# OS
.DS_Store
Thumbs.db
```

Create/update `backend/.gitignore`:
```gitignore
target/
!.mvn/wrapper/maven-wrapper.jar
*.jar
*.class
```

### Step 5: Test Backend Build
```bash
cd backend
./mvnw clean package -DskipTests
```

If build succeeds, continue. If not, investigate and fix.

### Step 6: Verify Structure
```bash
# Verify backend structure
ls -la backend/
ls -la backend/src/

# Verify frontend directory exists
ls -la frontend/

# Verify nginx directory exists
ls -la nginx/
```

## Acceptance Criteria

- [ ] Backend code successfully moved to `backend/` directory
- [ ] Backend builds successfully from new location
- [ ] `frontend/` and `nginx/` directories created
- [ ] `docker-compose.yml` remains at root level
- [ ] `.env.example` created at root level
- [ ] `.gitignore` updated appropriately
- [ ] No loss of existing functionality
- [ ] Repository structure is clear and documented
- [ ] All changes committed

## Definition of Done

- [ ] All backend files moved to `backend/` directory
- [ ] Backend compiles successfully
- [ ] Frontend directory created (empty)
- [ ] Nginx directory created (empty)
- [ ] Docker compose structure ready for services
- [ ] Root README updated with new structure
- [ ] Git history preserved (using git mv)
- [ ] Changes merged to main branch

## Notes

- Use `git mv` instead of regular `mv` to preserve file history
- This is a structural refactor, not a code change
- Keep docker-compose.yml at root level for simplicity
- Frontend and nginx will be populated in subsequent issues
- This structure enables:
  - Independent builds and deployments
  - Clear separation of concerns
  - Team-based development (frontend/backend teams)
  - Simplified CI/CD pipeline configuration

## Related Issues

- #30: Backend Dockerfile Creation
- #31: Initialize Next.js Frontend
- #32: Nginx Reverse Proxy Configuration
