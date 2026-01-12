# Conecta Reparo Mondaí

[![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)]()
[![Build](https://github.com/fernandocarminatti/conecta-reparo/actions/workflows/test.yml/badge.svg)]()

Plataforma de Apoio à Manutenção de Estruturas de Saúde Comunitárias.

## Visão Geral

Sistema para gerenciamento de manutenções em centros de saúde comunitários, conectando necessidades de manutenção com doadores de materiais e voluntários da comunidade local.

### Problema

Associações hospitalares e Entidades Beneficentes de Assistência Social frequentemente enfrentam desafios para:
- Identificar e documentar necessidades de manutenção
- Mobilizar recursos da comunidade
- Rastrear progresso de manutenções
- Gerenciar doações e voluntários

### Solução

O Conecta Reparo oferece uma plataforma digital para:
- Cadastro e acompanhamento de manutenções
- Visualização pública de necessidades
- Sistema de Ofertas de Contribuição (Pledges)
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
     │   Next.js       │              │  Spring Boot    │
     │   (Porta 3000)  │              │   (Porta 8080)  │
     │                 │              │                 │
     │  Frontend Web   │◄────────────►│  REST API       │
     │  React + TS     │   HTTP/JSON  │  Java 17        │
     └─────────────────┘              └────────┬────────┘
                                               │
                                     ┌─────────▼─────────┐
                                     │   PostgreSQL      │
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
- ~2GB RAM disponível

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
- Link para detalhes e ofertas

### Fazer uma Oferta

1. Visualizar Ofertas na página inicial
2. Clicar em Nova Oferta
3. Preencher formulário de Oferta
4. Confirmar submissão

### Área Administrativa

Acesse `/admin` para:
- Visualizar dashboard com estatísticas
- Criar novas manutenções
- Editar manutenções existentes
- Visualizar histórico
- Gerenciar pledges

## API Reference

### Endpoints Públicos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/maintenances` | Lista manutenções |
| GET | `/api/maintenances/{id}` | Detalhes de manutenção |
| POST | `/api/pledges` | Criar oferta |

### Endpoints Administrativos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/admin/dashboard` | Estatísticas do dashboard |
| POST | `/api/admin/maintenances` | Criar manutenção |
| PUT | `/api/admin/maintenances/{id}` | Atualizar manutenção |
| GET | `/api/admin/maintenances/history` | Histórico de manutenções |
| GET | `/api/admin/pledges` | Listar pledges |
| PATCH | `/api/admin/pledges/{id}/status` | Atualizar status do pledge |

## Desenvolvimento

### Estrutura de Issues

Todas as funcionalidades do projeto são gerenciadas exclusivamente via **issues**.

Ver [Issues](https://github.com/fernandocarminatti/conecta-reparo/issues) para detalhes.

## Contribuições

Este é um projeto acadêmico de extensão. **Submissões externas via pull requests não são aceitas e serão recusadas sem revisão**.

---

## Contributing

This is an academic extension project. **External pull request submissions are not accepted and will be rejected without review.**


## Licença

Este projeto é disponibilizado sob a Licença MIT, permitindo uso, modificação e distribuição para qualquer finalidade, com ou sem fins comerciais.

## License

This project is licensed under the MIT License, allowing use, modification, and distribution for any purpose, including commercial use.


## Contato

**Fernando Carminatti**
- [GitHub](https://github.com/fernandocarminatti)
