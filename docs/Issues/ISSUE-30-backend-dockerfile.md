# [ISSUE #30] Create Backend Dockerfile

## Context
Create a Docker container for the Spring Boot backend application. The Dockerfile should support multi-stage builds for optimized image size and production deployment.

## Current State
- Backend code is located in `backend/` directory
- Backend builds successfully with Maven
- Backend requires Java 17 and PostgreSQL database

## Target State
- Backend runs in Docker container
- Container exposes port 8080
- Multi-stage build for production optimization
- Health check endpoint configured

## Tasks

### Dockerfile Creation
- [ ] Create `backend/Dockerfile`
- [ ] Implement multi-stage build (builder + runtime)
- [ ] Configure proper port exposure
- [ ] Set up health check
- [ ] Optimize for production deployment

### Build Optimization
- [ ] Use incremental builds with dependency caching
- [ ] Minimize layer count
- [ ] Exclude unnecessary files from build context
- [ ] Use appropriate base image tags

## Implementation

### backend/Dockerfile
```dockerfile
# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper and configuration
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application (skip tests for Docker build)
RUN ./mvnw package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy built artifact from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Set working directory
WORKDIR /home/spring

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

### .dockerignore (backend/)
Create `backend/.dockerignore`:
```text
target/
.git/
.gitignore
*.md
.DS_Store
*.log
```

## Build and Test

### Build the image
```bash
cd backend
docker build -t conectareparo-backend:test .
```

### Test the container
```bash
# Run in detached mode (requires PostgreSQL)
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/conectareparo \
  -e SPRING_DATASOURCE_USERNAME=conectareparo \
  -e SPRING_DATASOURCE_PASSWORD=changeme \
  conectareparo-backend:test

# Check logs
docker logs <container_id>

# Test health endpoint
curl http://localhost:8080/actuator/health
```

### Verify with Docker Compose
```bash
docker-compose build backend
docker-compose up -d backend
docker-compose logs backend
```

## Environment Variables

The Dockerfile expects these environment variables:
- `SPRING_PROFILES_ACTIVE` (default: prod)
- `SPRING_DATASOURCE_URL` (JDBC URL for PostgreSQL)
- `SPRING_DATASOURCE_USERNAME` (database user)
- `SPRING_DATASOURCE_PASSWORD` (database password)
- `CORS_ALLOWED_ORIGINS` (for CORS configuration)

## Acceptance Criteria

- [ ] Docker image builds successfully
- [ ] Multi-stage build implemented (optimized image size)
- [ ] Application starts and serves API endpoints
- [ ] Health check endpoint responds
- [ ] Container runs as non-root user
- [ ] Port 8080 properly exposed
- [ ] Image works with docker-compose
- [ ] Build time is reasonable (dependencies cached)

## Definition of Done

- [ ] Dockerfile created and tested
- [ ] Image builds without errors
- [ ] Application starts successfully in container
- [ ] Health check configured and working
- [ ] Documentation updated (if needed)
- [ ] Changes committed

## Notes

- Consider using Spring Boot's layer-based jar for better caching
- Multi-stage build reduces final image size significantly
- Non-root user improves security posture
- Health check helps container orchestrators manage lifecycle
- Consider adding JVM tuning flags for container environments

## Related Issues

- #29: Repository Monorepo Restructure
- #34: Docker Compose Orchestration
- #33: Backend CORS Configuration
