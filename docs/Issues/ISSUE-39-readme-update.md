# [ISSUE #39] Update Root README

## Context
Update the root README.md to reflect the new monorepo structure, architecture, and provide accurate information for developers and users.

## Current State
- README exists at root level
- Reflects old single-level structure
- May have outdated information
- Not aligned with new monorepo structure

## Target State
- Updated README reflecting monorepo structure
- Clear architecture diagram
- Accurate technology stack
- Quick start instructions
- Links to documentation

## Tasks

### Content Update
- [ ] Update repository structure diagram
- [ ] Update architecture section
- [ ] Update technology stack versions
- [ ] Add quick start guide
- [ ] Update development instructions
- [ ] Add documentation links
- [ ] Update badges
- [ ] Add contributing guidelines

### Verification
- [ ] Verify all links work
- [ ] Verify code blocks are accurate
- [ ] Check formatting
- [ ] Test quick start commands

## Implementation

### Updated README.md
```markdown
# Conecta Reparo Mondaí

[![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)]()
[![Build](https://github.com/fernandocarminatti/conecta-reparo/actions/workflows/test.yml/badge.svg)]()
[![License](https://img.shields.io/badge/license-Academic--UNINTER-blue)]()

Plataforma de Apoio à Manutenção de Estruturas de Saúde Comunitárias.

## Visão Geral

Sistema para gerenciamento de manutenções em centros de saúde comunitários, conectando necessidades de manutenção com doadores de materiais e voluntários da comunidade local.

### Problema

Associações hospitalares frequentemente enfrentam desafios para:
- Identificar e documentar necessidades de manutenção
- Mobilizar recursos da comunidade
- Rastrear progresso de manutenções
- Gerenciar doações e voluntários

### Solução

O Conecta Reparo oferece uma plataforma digital para:
- Cadastro e acompanhamento de manutenções
- Visualização pública de necessidades
- Sistema de pledges (promessas de contribuição)
- Dashboard administrativo para gestão

## Arquitetura

```
┌─────────────────────────────────────────────────────────────────┐
│                        Nginx (Porta 80)                         │
│                    Reverse Proxy & Roteamento                   │
└─────────────────────────────────────────────────────────────────┘
                               │
              ┌────────────────┴────────────────┐
              │                                 │
     ┌────────▼────────┐              ┌────────▼────────┐
     │   Next.js 14    │              │  Spring Boot 3  │
     │   (Porta 3000)  │              │   (Porta 8080)  │
     │                 │              │                 │
     │  Frontend Web   │◄────────────►│  REST API       │
     │  React + TS     │   HTTP/JSON  │  Java 17        │
     └─────────────────┘              └────────┬────────┘
                                              │
                                    ┌─────────▼─────────┐
                                    │   PostgreSQL 15   │
                                    │   (Porta 5432)    │
                                    │                   │
                                    │   Persistência    │
                                    └───────────────────┘
```

## Estrutura do Projeto

```
conecta-reparo/
├── backend/                  # API REST Spring Boot
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/conectareparo/
│   │       └── resources/
│   │           ├── application.yml
│   │           └── db/migration/
│   ├── pom.xml
│   ├── mvnw
│   └── Dockerfile
│
├── frontend/                 # Interface Web Next.js
│   ├── app/                  # App Router pages
│   │   ├── page.tsx          # Homepage
│   │   ├── maintenance/      # Páginas públicas
│   │   ├── pledge/           # Formulário de pledge
│   │   └── admin/            # Área administrativa
│   ├── components/           # Componentes React
│   ├── lib/                  # Utilitários e API client
│   ├── public/               # Arquivos estáticos
│   ├── next.config.js
│   ├── tailwind.config.ts
│   └── Dockerfile
│
├── nginx/                    # Configuração do proxy
│   └── nginx.conf
│
├── docs/                     # Documentação
│   ├── api.md
│   └── development.md
│
├── docker-compose.yml        # Orquestração de containers
├── .env.example              # Exemplo de variáveis de ambiente
├── INSTALL.md                # Guia de instalação
├── README.md                 # Este arquivo
└── .gitignore
```

## Tecnologias

### Backend

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| Java | 17+ | Linguagem de programação |
| Spring Boot | 3.5.x | Framework de aplicação |
| PostgreSQL | 15 | Banco de dados relacional |
| Flyway | 10.x | Migrações de banco de dados |
| Spring Security | 6.x | Autenticação e autorização |
| Maven | 3.8+ | Build e gerenciamento de dependências |

### Frontend

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| Next.js | 14+ | Framework React |
| TypeScript | 5.x | Tipagem estática |
| Tailwind CSS | 3.x | Estilização |
| React | 18.x | Biblioteca de UI |
| Lucide React | 0.x | Ícones |
| date-fns | 3.x | Manipulação de datas |

### Infraestrutura

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| Docker | 20.10+ | Containerização |
| Docker Compose | 2.0+ | Orquestração |
| Nginx | 1.25 | Reverse proxy |
| GitHub Actions | - | CI/CD |

## Início Rápido

### Pré-requisitos

- Docker Engine 20.10+
- Docker Compose 2.0+
- Git
- 2GB RAM disponível

### Instalação Completa (Docker)

```bash
# Clonar o repositório
git clone https://github.com/fernandocarminatti/conecta-reparo.git
cd conecta-reparo

# Configurar variáveis de ambiente
cp .env.example .env
# Editar .env se necessário

# Iniciar serviços
docker-compose up -d

# Verificar status
docker-compose ps

# Acessar aplicação
# http://localhost
```

### Desenvolvimento Local

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
# API disponível em http://localhost:8080
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
# Frontend disponível em http://localhost:3000
```

**Banco de Dados:**
```bash
# Com Docker Compose
docker-compose up -d db

# Ou localmente (requer PostgreSQL 15)
psql -U postgres -c "CREATE DATABASE conectareparo;"
```

## Uso da Aplicação

### Página Pública

A página inicial (`/`) exibe:
- Lista de manutenções abertas
- Cards com informações resumidas
- Link para detalhes e pledges

### Fazer um Pledge

1. Visualizar manutenção na página inicial
2. Clicar em "Ver Detalhes"
3. Preencher formulário de pledge
4. Confirmar submissão

### Área Administrativa

Acesse `/admin` para:
- Visualizar dashboard com estatísticas
- Criar novas manutenções
- Editar manutenções existentes
- Visualizar histórico
- Gerenciar pledges

## Documentação

| Documento | Descrição |
|-----------|-----------|
| [INSTALL.md](INSTALL.md) | Guia de instalação para produção |
| [docs/api.md](docs/api.md) | Documentação da API REST |
| [docs/development.md](docs/development.md) | Guia de desenvolvimento |

## API Reference

### Endpoints Públicos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/maintenances` | Lista manutenções |
| GET | `/api/maintenances/{id}` | Detalhes de manutenção |
| POST | `/api/pledges` | Criar pledge |

### Endpoints Administrativos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/admin/dashboard` | Estatísticas do dashboard |
| POST | `/api/admin/maintenances` | Criar manutenção |
| PUT | `/api/admin/maintenances/{id}` | Atualizar manutenção |
| GET | `/api/admin/maintenances/history` | Histórico de manutenções |
| GET | `/api/admin/pledges` | Listar pledges |
| PATCH | `/api/admin/pledges/{id}/status` | Atualizar status do pledge |

Consulte [docs/api.md](docs/api.md) para documentação completa.

## Desenvolvimento

### Estrutura de Issues

As funcionalidades são organizadas em épicos:

| Épico | Título | Status |
|-------|--------|--------|
| #28 | Backend Package by Feature | Planejado |
| #29 | Repository Monorepo Restructure | Em Andamento |
| #30 | Backend Dockerfile Creation | Planejado |
| #31 | Initialize Next.js Frontend | Planejado |
| #10 | Módulo de Gestão de Manutenções | Backlog |
| #9 | Módulo de Histórico | Backlog |
| #8 | Módulo de Engajamento Comunitário | Backlog |

Ver [Issues](https://github.com/fernandocarminatti/conecta-reparo/issues) para detalhes.

### Convenções de Código

#### Backend (Java)

- Padrão: Package by Feature
- Estilo: Google Java Format
- Commits: Conventional Commits
- Branches: feature/*, bugfix/*

#### Frontend (TypeScript)

- Padrão: Next.js App Router
- Estilo: ESLint + Prettier
- Commits: Conventional Commits
- Branches: feature/*, bugfix/*

### Contribuindo

1. Fork o repositório
2. Crie branch: `git checkout -b feature/nova-funcionalidade`
3. Commit: `git commit -m 'feat: adiciona nova funcionalidade'`
4. Push: `git push origin feature/nova-funcionalidade`
5. Abra um Pull Request

### Testes

```bash
# Backend
cd backend
./mvnw test

# Frontend
cd frontend
npm test
```

## Releases

Releases são criadas automaticamente quando uma tag é criada:

```bash
# Criar tag de versão
git tag -a v1.0.0 -m "Release v1.0.0"

# Push da tag
git push origin v1.0.0
```

Isso irá:
1. Executar pipeline de testes
2. Construir imagens Docker
3. Criar release no GitHub
4. Gerar artefato deployável

Baixar releases em: [Releases](https://github.com/fernandocarminatti/conecta-reparo/releases)

## Licença

Este projeto é parte da Atividade Extensionista do curso de Análise e Desenvolvimento de Sistemas - UNINTER.

## Contato

**Fernando Carminatti**
- [GitHub](https://github.com/fernandocarminatti)
- [LinkedIn](https://linkedin.com/in/fernandocarminatti)

## Agradecimentos

- Universidade UNINTER
- Associação Hospitalar Mondaí
- Comunidade de desenvolvedores open source
```

## Acceptance Criteria

- [ ] README reflects new monorepo structure
- [ ] Architecture diagram is clear and accurate
- [ ] Technology versions are up to date
- [ ] Quick start instructions work
- [ ] Links to documentation are correct
- [ ] Code blocks are accurate
- [ ] Badges are working
- [ ] Formatting is consistent

## Definition of Done

- [ ] README updated with new structure
- [ ] All links verified
- [ ] Code examples tested
- [ ] Formatting checked
- [ ] Badges verified
- [ ] Changes committed

## Related Issues

- #29: Repository Monorepo Restructure
- #37: Installation Documentation
- #38: CI/CD Workflows
