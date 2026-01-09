# [ISSUE #33] Docker Compose Orchestration

## Context
Configure Docker Compose to orchestrate all services (database, backend, frontend, nginx) for easy deployment and development. All services should start in the correct order with proper health checks and networking.

## Current State
- Backend Dockerfile exists (issue #30)
- Frontend Dockerfile will be created (issue #36)
- Nginx configuration exists (issue #32)
- No unified orchestration yet

## Target State
- All services defined in docker-compose.yml
- Services start in correct order (db → backend → frontend → nginx)
- Health checks configured for all services
- Proper networking between services
- Environment variable configuration
- Volume persistence for database

## Tasks

### Service Definitions
- [ ] Define PostgreSQL database service
- [ ] Define backend service
- [ ] Define frontend service
- [ ] Define nginx service
- [ ] Configure service dependencies

### Network Configuration
- [ ] Create internal bridge network
- [ ] Configure service networking
- [ ] Verify inter-service communication

### Volume Configuration
- [ ] Configure PostgreSQL data volume
- [ ] Verify data persistence

### Environment Configuration
- [ ] Create .env.example file
- [ ] Configure environment variables for each service
- [ ] Document required environment variables

### Health Checks
- [ ] Configure database health check
- [ ] Configure backend health check
- [ ] Configure service dependency conditions

## Implementation

### docker-compose.yml
```yaml
version: '3.8'

services:
  # PostgreSQL Database
  db:
    image: postgres:15-alpine
    container_name: conectareparo-db
    environment:
      POSTGRES_DB: ${DB_NAME:-conectareparo}
      POSTGRES_USER: ${DB_USER:-conectareparo}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-changeme}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - conectareparo-net
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER:-conectareparo}"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s

  # Backend API (Spring Boot)
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: conectareparo-backend
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILE:-prod}
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${DB_NAME:-conectareparo}
      SPRING_DATASOURCE_USERNAME: ${DB_USER:-conectareparo}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-changeme}
      CORS_ALLOWED_ORIGINS: ${CORS_ORIGINS:-http://localhost}
      SERVER_PORT: 8080
    depends_on:
      db:
        condition: service_healthy
    networks:
      - conectareparo-net
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval:      timeout:  30s
10s
      retries: 3
      start_period: 30s

  # Frontend (Next.js)
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      args:
        NEXT_PUBLIC_API_URL: ${NEXT_PUBLIC_API_URL:-http://localhost/api}
    container_name: conectareparo-frontend
    environment:
      NEXT_PUBLIC_API_URL: ${NEXT_PUBLIC_API_URL:-http://localhost/api}
      NODE_ENV: production
    depends_on:
      - backend
    networks:
      - conectareparo-net
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:3000"]
      interval: 30s
      timeout: 10s
      retries: 3

  # Nginx Reverse Proxy
  nginx:
    image: nginx:alpine
    container_name: conectareparo-nginx
    ports:
      - "${HOST_PORT:-80}:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - frontend
      - backend
    networks:
      - conectareparo-net
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  postgres_data:
    driver: local

networks:
  conectareparo-net:
    driver: bridge
    ipam:
      config:
        - subnet: 172.28.0.0/16
```

### .env.example
```bash
# Database Configuration
DB_NAME=conectareparo
DB_USER=conectareparo
DB_PASSWORD=changeme_in_production

# Spring Profile
SPRING_PROFILE=prod

# CORS Configuration
CORS_ORIGINS=http://localhost,http://maintenance.mondai.local

# Frontend API URL (as seen from browser)
NEXT_PUBLIC_API_URL=http://localhost/api

# Host Port Mapping
HOST_PORT=80
```

## Usage

### Start All Services
```bash
docker-compose up -d
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f nginx
docker-compose logs -f db
```

### Check Status
```bash
docker-compose ps
```

### Stop All Services
```bash
docker-compose down
```

### Stop and Remove Volumes
```bash
docker-compose down -v
```

### Rebuild Services
```bash
docker-compose build
docker-compose up -d
```

## Testing

### Verify All Services Running
```bash
docker-compose ps
```

### Test Health Endpoints
```bash
# Database
docker-compose exec db pg_isready -U conectareparo

# Backend
curl http://localhost/health

# Frontend
curl http://localhost/

# Nginx
curl http://localhost/api/maintenances
```

### Verify Network Connectivity
```bash
# From backend container
docker-compose exec backend ping -c 1 db
docker-compose exec backend ping -c 1 nginx

# From frontend container
docker-compose exec frontend ping -c 1 backend
```

## Environment Variables Reference

| Variable | Default | Description |
|----------|---------|-------------|
| DB_NAME | conectareparo | PostgreSQL database name |
| DB_USER | conectareparo | PostgreSQL username |
| DB_PASSWORD | changeme | PostgreSQL password (change in production!) |
| SPRING_PROFILE | prod | Spring profile (prod/dev) |
| CORS_ORIGINS | http://localhost | Allowed CORS origins |
| NEXT_PUBLIC_API_URL | http://localhost/api | Frontend API URL |
| HOST_PORT | 80 | Host port for nginx |

## Acceptance Criteria

- [ ] `docker-compose up -d` starts all services
- [ ] Services start in correct order (db → backend → frontend → nginx)
- [ ] Health checks work for all services
- [ ] Services can communicate via internal network
- [ ] Application accessible at http://localhost
- [ ] Data persists after `docker-compose down`
- [ ] Services restart correctly after `docker-compose restart`
- [ ] Environment variables load correctly
- [ ] No errors in logs on startup

## Definition of Done

- [ ] docker-compose.yml created and tested
- [ ] All services start successfully
- [ ] Health checks configured and working
- [ ] Networking verified between all services
- [ ] Database persistence confirmed
- [ ] Environment configuration documented
- [ ] Changes committed

## Notes

- Always use `docker-compose down -v` with caution (deletes data)
- Change default passwords for production
- Consider adding limits for production deployment
- The `depends_on` with `condition: service_healthy` ensures proper startup order
- Consider using Docker secrets for sensitive data in production
- For production, consider using external database or volume backup

## Related Issues

- #29: Repository Monorepo Restructure
- #30: Backend Dockerfile Creation
- #32: Nginx Configuration
- #36: Frontend Dockerfile Creation
- #37: Installation Documentation
