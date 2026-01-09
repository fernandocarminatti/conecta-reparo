# [ÉPICO] Setup Frontend Infrastructure and Monorepo Structure

## Context
Restructure repository to support both backend and frontend as a single deployable unit for localhost/hospital internal network deployment. The system needs to serve both public-facing pages (for community to view maintenance needs and pledge help) and internal admin pages (for hospital staff to manage maintenances).

## Objectives
- [ ] Restructure repository as monorepo with separate backend/frontend directories
- [ ] Setup Next.js frontend with TypeScript and Tailwind CSS
- [ ] Configure Docker Compose orchestration for all services
- [ ] Implement Nginx reverse proxy for routing
- [ ] Create deployment documentation for hospital IT
- [ ] Setup CI/CD for release artifact generation

---

## Task Breakdown

### 1. Repository Restructure
**Priority: HIGH** | **Estimated: 1h**

- [ ] Move existing Spring Boot code to `backend/` directory
  - Move `src/`, `pom.xml`, `mvnw*` to `backend/`
  - Update `.gitignore` for new structure
  - Test that backend still builds: `cd backend && ./mvnw clean package`
- [ ] Create `frontend/` directory structure
- [ ] Create `nginx/` directory for reverse proxy config
- [ ] Update root `README.md` with new structure overview
- [ ] Keep `docker-compose.yml` at root level

**Acceptance Criteria:**
- Backend builds successfully from new location
- Repository structure is clear and documented
- No loss of existing functionality

---

### 2. Backend Dockerfile Creation
**Priority: HIGH** | **Estimated: 30min**

Create `backend/Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline
COPY src src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Acceptance Criteria:**
- Backend Docker image builds successfully
- Application starts and serves API endpoints
- Health check endpoint responds

---

### 3. Initialize Next.js Frontend
**Priority: HIGH** | **Estimated: 1h**

```bash
cd frontend
npx create-next-app@latest . --typescript --tailwind --app --no-src-dir
```

Install additional dependencies:
```bash
npm install lucide-react date-fns
npm install -D @types/node
```

Create basic folder structure:
```
frontend/
├── app/
│   ├── layout.tsx
│   ├── page.tsx                 # Public: List open maintenances
│   ├── maintenance/
│   │   └── [id]/
│   │       └── page.tsx         # Public: Maintenance details
│   ├── pledge/
│   │   └── page.tsx             # Public: Submit pledge
│   └── admin/
│       ├── layout.tsx           # Admin wrapper with auth check
│       ├── page.tsx             # Admin dashboard
│       ├── maintenance/
│       │   ├── new/page.tsx     # Create maintenance
│       │   └── [id]/edit/page.tsx
│       └── history/
│           └── page.tsx         # Maintenance history
├── components/
│   ├── MaintenanceCard.tsx
│   ├── PledgeForm.tsx
│   └── ui/                      # Reusable UI components
├── lib/
│   ├── api.ts                   # API client for backend
│   └── types.ts                 # TypeScript types for entities
├── public/
└── next.config.js
```

**Acceptance Criteria:**
- Next.js dev server runs: `npm run dev`
- Basic routing works (/, /maintenance/1, /pledge, /admin)
- TypeScript configured correctly
- Tailwind CSS working

---

### 4. Create API Client Library
**Priority: HIGH** | **Estimated: 1h**

Create `frontend/lib/api.ts`:

```typescript
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface Maintenance {
  id: number;
  title: string;
  description: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  status: 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  createdAt: string;
  // Add other fields based on your entity
}

export interface Pledge {
  id: number;
  maintenanceId: number;
  donorName: string;
  donorContact: string;
  pledgeType: 'MATERIAL' | 'VOLUNTEER';
  description: string;
  createdAt: string;
}

export const api = {
  // Public endpoints
  maintenances: {
    list: async (): Promise<Maintenance[]> => {
      const res = await fetch(`${API_URL}/maintenances`);
      if (!res.ok) throw new Error('Failed to fetch maintenances');
      return res.json();
    },
    
    getById: async (id: number): Promise<Maintenance> => {
      const res = await fetch(`${API_URL}/maintenances/${id}`);
      if (!res.ok) throw new Error('Failed to fetch maintenance');
      return res.json();
    },
  },
  
  pledges: {
    create: async (pledge: Omit<Pledge, 'id' | 'createdAt'>): Promise<Pledge> => {
      const res = await fetch(`${API_URL}/pledges`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(pledge),
      });
      if (!res.ok) throw new Error('Failed to create pledge');
      return res.json();
    },
  },
  
  // Admin endpoints (add authentication headers later)
  admin: {
    maintenances: {
      create: async (data: Partial<Maintenance>): Promise<Maintenance> => {
        const res = await fetch(`${API_URL}/admin/maintenances`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(data),
        });
        if (!res.ok) throw new Error('Failed to create maintenance');
        return res.json();
      },
    },
  },
};
```

**Acceptance Criteria:**
- API client compiles without errors
- TypeScript types match backend entities
- Error handling implemented
- Environment variable for API URL works

---

### 5. Frontend Dockerfile Creation
**Priority: HIGH** | **Estimated: 30min**

Create `frontend/Dockerfile`:

```dockerfile
FROM node:18-alpine AS deps
WORKDIR /app
COPY package*.json ./
RUN npm ci

FROM node:18-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
ARG NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_URL=${NEXT_PUBLIC_API_URL}
RUN npm run build

FROM node:18-alpine AS runner
WORKDIR /app
ENV NODE_ENV production
COPY --from=builder /app/next.config.js ./
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
EXPOSE 3000
CMD ["node", "server.js"]
```

Update `frontend/next.config.js`:
```javascript
/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone',
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  },
}

module.exports = nextConfig
```

**Acceptance Criteria:**
- Frontend Docker image builds successfully
- Production build works
- Environment variables passed correctly

---

### 6. Nginx Reverse Proxy Configuration
**Priority: HIGH** | **Estimated: 30min**

Create `nginx/nginx.conf`:

```nginx
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    upstream backend {
        server backend:8080;
    }

    upstream frontend {
        server frontend:3000;
    }

    server {
        listen 80;
        server_name _;

        # Security headers
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;

        # API requests to backend
        location /api/ {
            proxy_pass http://backend/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Health check endpoint
        location /health {
            proxy_pass http://backend/actuator/health;
            proxy_set_header Host $host;
        }

        # All other requests to frontend
        location / {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection 'upgrade';
            proxy_cache_bypass $http_upgrade;
        }
    }
}
```

**Acceptance Criteria:**
- Nginx routes /api/* to backend correctly
- Nginx routes all other paths to frontend
- Security headers are set
- Health check endpoint accessible

---

### 7. Docker Compose Orchestration
**Priority: HIGH** | **Estimated: 1h**

Update root `docker-compose.yml`:

```yaml
version: '3.8'

services:
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
    depends_on:
      db:
        condition: service_healthy
    networks:
      - conectareparo-net
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      args:
        NEXT_PUBLIC_API_URL: ${NEXT_PUBLIC_API_URL:-http://localhost/api}
    container_name: conectareparo-frontend
    environment:
      NEXT_PUBLIC_API_URL: ${NEXT_PUBLIC_API_URL:-http://localhost/api}
    depends_on:
      - backend
    networks:
      - conectareparo-net
    restart: unless-stopped

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

volumes:
  postgres_data:
    driver: local

networks:
  conectareparo-net:
    driver: bridge
```

Create `.env.example`:
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

**Acceptance Criteria:**
- `docker-compose up -d` starts all services
- Services start in correct order (db → backend → frontend → nginx)
- Health checks work
- Services can communicate via internal network
- Application accessible at http://localhost

---

### 8. Update Backend CORS Configuration
**Priority: HIGH** | **Estimated: 15min**

Update `backend/src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: conecta-reparo
  
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost}
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true
```

Create CORS config class if not exists:
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

**Acceptance Criteria:**
- CORS configured via environment variable
- Frontend can make API requests without CORS errors
- OPTIONS preflight requests handled correctly

---

### 9. Create Installation Documentation
**Priority: MEDIUM** | **Estimated: 1h**

Create `INSTALL.md` in root:

```markdown
# Guia de Instalação - Conecta Reparo Mondaí

## Visão Geral
Este guia descreve como instalar e configurar o sistema Conecta Reparo para uso na Associação Hospitalar Mondaí.

## Pré-requisitos

### Software Necessário
- Docker Engine 20.10 ou superior
- Docker Compose 2.0 ou superior
- Mínimo 2GB RAM disponível
- Mínimo 5GB espaço em disco

### Verificar Instalação
```bash
docker --version
docker-compose --version
```

## Instalação Rápida

### 1. Obter o Sistema
```bash
# Opção A: Via Git (requer Git instalado)
git clone https://github.com/fernandocarminatti/conecta-reparo.git
cd conecta-reparo

# Opção B: Via arquivo de release (sem Git)
# Baixar arquivo .tar.gz da página de releases
tar -xzf conecta-reparo-v1.0.0.tar.gz
cd conecta-reparo-v1.0.0
```

### 2. Configuração (Opcional)
```bash
cp .env.example .env
nano .env  # Ajustar configurações se necessário
```

Configurações importantes:
- `DB_PASSWORD`: Alterar senha padrão do banco
- `HOST_PORT`: Porta de acesso (padrão: 80)
- `CORS_ORIGINS`: Domínios permitidos

### 3. Iniciar Sistema
```bash
docker-compose up -d
```

### 4. Verificar Funcionamento
- Acessar: http://localhost
- Health check: http://localhost/health
- Logs: `docker-compose logs -f`

## Configuração de Domínio Interno

### Opção A: Arquivo Hosts (Teste/Desenvolvimento)
Editar arquivo hosts do sistema:

**Linux/Mac:** `/etc/hosts`
**Windows:** `C:\Windows\System32\drivers\etc\hosts`

Adicionar linha:
```
192.168.x.x  maintenance.mondai.local
```

### Opção B: Servidor DNS Interno (Produção)
Configurar registro A no DNS interno:
```
maintenance.mondai.local → IP_DO_SERVIDOR
```

## Comandos Úteis

### Parar Sistema
```bash
docker-compose down
```

### Ver Logs
```bash
# Todos os serviços
docker-compose logs -f

# Serviço específico
docker-compose logs -f backend
docker-compose logs -f frontend
```

### Backup do Banco de Dados
```bash
docker-compose exec db pg_dump -U conectareparo conectareparo > backup.sql
```

### Restaurar Banco de Dados
```bash
docker-compose exec -T db psql -U conectareparo conectareparo < backup.sql
```

### Atualizar Sistema
```bash
git pull origin main
docker-compose down
docker-compose build
docker-compose up -d
```

## Solução de Problemas

### Porta 80 já em uso
Editar `.env` e alterar `HOST_PORT`:
```bash
HOST_PORT=8080
```

### Serviços não iniciam
```bash
# Ver logs de erro
docker-compose logs

# Reiniciar serviços
docker-compose restart
```

### Problemas de conexão backend-frontend
Verificar se todos os serviços estão rodando:
```bash
docker-compose ps
```

## Suporte
Para problemas ou dúvidas:
- Abrir issue em: https://github.com/fernandocarminatti/conecta-reparo/issues
- Contato: fernando.carminatti@example.com
```

**Acceptance Criteria:**
- Documentation is clear and complete
- Installation steps are tested and work
- Troubleshooting section covers common issues
- Portuguese language appropriate for hospital IT staff

---

### 10. Create CI/CD Workflow for Releases
**Priority: MEDIUM** | **Estimated: 1h**

Create `.github/workflows/release.yml`:

```yaml
name: Build Deployable Release

on:
  push:
    tags:
      - 'v*'

jobs:
  build-and-release:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2
      
      - name: Build Docker images
        run: |
          docker-compose build
      
      - name: Save Docker images
        run: |
          docker save -o conecta-reparo-images.tar \
            conectareparo-backend:latest \
            conectareparo-frontend:latest \
            nginx:alpine \
            postgres:15-alpine
      
      - name: Create deployment package
        run: |
          mkdir -p release
          mv conecta-reparo-images.tar release/
          cp docker-compose.yml release/
          cp .env.example release/.env.example
          cp -r nginx release/
          cp INSTALL.md release/
          cp README.md release/
          tar -czf conecta-reparo-${{ github.ref_name }}.tar.gz release/
      
      - name: Generate checksums
        run: |
          sha256sum conecta-reparo-${{ github.ref_name }}.tar.gz > checksums.txt
      
      - name: Create GitHub Release
        uses: softprops/action-gh-release@v1
        with:
          files: |
            conecta-reparo-${{ github.ref_name }}.tar.gz
            checksums.txt
          body: |
            ## Conecta Reparo ${{ github.ref_name }}
            
            ### Instalação
            1. Baixar `conecta-reparo-${{ github.ref_name }}.tar.gz`
            2. Extrair: `tar -xzf conecta-reparo-${{ github.ref_name }}.tar.gz`
            3. Seguir instruções em `INSTALL.md`
            
            ### O que está incluído
            - Backend (Spring Boot)
            - Frontend (Next.js)
            - Banco de dados (PostgreSQL)
            - Proxy reverso (Nginx)
            - Documentação completa
            
            ### Verificação
            Checksum SHA256 disponível em `checksums.txt`
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

Create `.github/workflows/test.yml` for PR validation:

```yaml
name: Test Build

on:
  pull_request:
    branches: [ main ]
  push:
    branches: [ main ]

jobs:
  test-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Build backend
        run: |
          cd backend
          ./mvnw clean package -DskipTests
      
      - name: Run tests
        run: |
          cd backend
          ./mvnw test

  test-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
      
      - name: Install dependencies
        run: |
          cd frontend
          npm ci
      
      - name: Build frontend
        run: |
          cd frontend
          npm run build
      
      - name: Run linter
        run: |
          cd frontend
          npm run lint

  test-docker:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker images
        run: docker-compose build
      
      - name: Start services
        run: docker-compose up -d
      
      - name: Wait for services
        run: sleep 30
      
      - name: Check health
        run: |
          curl -f http://localhost/health || exit 1
      
      - name: Cleanup
        run: docker-compose down
```

**Acceptance Criteria:**
- Release workflow triggers on version tags
- All Docker images saved to deployable tarball
- Release notes auto-generated
- Checksums included for verification
- Test workflow validates PRs before merge

---

### 11. Update Root README
**Priority: MEDIUM** | **Estimated: 30min**

Update root `README.md` with new structure:

```markdown
# Conecta Reparo Mondaí

[![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)]()
[![Build](https://github.com/fernandocarminatti/conecta-reparo/actions/workflows/test.yml/badge.svg)]()

Plataforma de Apoio à Manutenção de Estruturas de Saúde Comunitárias.

## Visão Geral

Sistema para gerenciamento de manutenções em centros de saúde comunitários, conectando necessidades de manutenção com doadores de materiais e voluntários da comunidade local.

## Arquitetura

```
┌─────────────────┐
│     Nginx       │  ← Porta 80
│  Reverse Proxy  │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
┌───▼───┐ ┌──▼──────┐
│Frontend│ │ Backend │
│Next.js │ │ Spring  │
└───────┘ └────┬────┘
              │
         ┌────▼────┐
         │PostgreSQL│
         └─────────┘
```

## Estrutura do Projeto

```
conecta-reparo/
├── backend/          # API Spring Boot
├── frontend/         # Interface Next.js
├── nginx/            # Configuração do proxy
├── docs/             # Documentação
├── docker-compose.yml
└── INSTALL.md        # Guia de instalação
```

## Tecnologias

### Backend
- Java 17
- Spring Boot 3.5.6
- PostgreSQL 15
- Flyway (migrations)

### Frontend
- Next.js 14
- TypeScript
- Tailwind CSS
- React

### Infraestrutura
- Docker & Docker Compose
- Nginx (reverse proxy)
- GitHub Actions (CI/CD)

## Início Rápido

### Desenvolvimento
```bash
# Backend
cd backend
./mvnw spring-boot:run

# Frontend (em outro terminal)
cd frontend
npm install
npm run dev
```

### Produção (Docker)
```bash
docker-compose up -d
```

Acessar: http://localhost

## Instalação Completa

Ver [INSTALL.md](INSTALL.md) para instruções detalhadas de instalação em ambiente de produção.

## Desenvolvimento

### Requisitos
- Java 17+
- Node.js 18+
- Docker 20.10+
- Maven 3.8+

### Estrutura de Issues

As funcionalidades são organizadas em épicos:
- **#10**: Módulo de Gestão de Manutenções
- **#9**: Módulo de Histórico
- **#8**: Módulo de Engajamento Comunitário
- **#21**: Cobertura de Testes
- **#27**: Testes de Integração

Ver [Issues](https://github.com/fernandocarminatti/conecta-reparo/issues) para detalhes.

## Documentação

- [Instalação](INSTALL.md)
- [API Documentation](docs/api.md) (em desenvolvimento)
- [Guia de Desenvolvimento](docs/development.md) (em desenvolvimento)

## Contribuindo

Este projeto segue uma abordagem de desenvolvimento orientada a documentação (docs-first). Antes de implementar funcionalidades:

1. Revisar issues relacionadas
2. Atualizar/criar documentação necessária
3. Implementar seguindo padrões do projeto
4. Incluir testes apropriados

## Releases

Releases são criadas automaticamente quando uma tag é criada:
```bash
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

Baixar releases em: [Releases](https://github.com/fernandocarminatti/conecta-reparo/releases)

## Licença

Este projeto é parte da Atividade Extensionista do curso de Análise e Desenvolvimento de Sistemas - UNINTER.

## Contato

Fernando Carminatti - [GitHub](https://github.com/fernandocarminatti)
```

**Acceptance Criteria:**
- README reflects new monorepo structure
- Architecture diagram is clear
- Quick start instructions work
- Links to documentation are correct

---

## Definition of Done

- [ ] All subtasks completed and tested
- [ ] Repository restructured and all services run via docker-compose
- [ ] Frontend initialized with basic pages structure
- [ ] API client library created with TypeScript types
- [ ] CORS configured on backend
- [ ] All services communicate correctly
- [ ] Installation documentation complete and tested
- [ ] CI/CD workflows created and tested
- [ ] README updated with new structure
- [ ] Changes committed and pushed to main branch
- [ ] Tag created for first integrated version (v0.1.0)

## Testing Checklist

- [ ] `docker-compose up -d` starts all services
- [ ] Navigate to http://localhost and see frontend
- [ ] API calls from frontend to backend work (check browser console)
- [ ] Backend endpoints accessible via /api prefix
- [ ] Health check endpoint responds
- [ ] Database migrations run automatically
- [ ] Services restart correctly after `docker-compose restart`
- [ ] Data persists after `docker-compose down` and `up`

## Notes

- This is a large refactor. Consider creating a `feature/monorepo-setup` branch
- Test thoroughly before merging to main
- Consider creating intermediate commits for each major subtask
- Update other open issues to reflect new repository structure
- The public endpoints (#33, #34) should be implemented after this infrastructure is in place