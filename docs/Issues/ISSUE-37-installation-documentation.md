# [ISSUE #37] Create Installation Documentation

## Context
Create comprehensive installation documentation for hospital IT staff to deploy and maintain the Conecta Reparo system in their internal network.

## Current State
- System architecture defined
- Docker Compose orchestration configured (issue #33)
- All Dockerfiles created (issues #30, #36)
- Nginx configured (issue #32)

## Target State
- Complete INSTALL.md in Portuguese
- Step-by-step installation guide
- Configuration options documented
- Troubleshooting section included
- Maintenance procedures documented

## Tasks

### Documentation Structure
- [ ] Create INSTALL.md file
- [ ] Write overview and prerequisites
- [ ] Document installation steps
- [ ] Document configuration options
- [ ] Write troubleshooting section
- [ ] Document maintenance procedures
- [ ] Add support information

### Review and Test
- [ ] Review documentation for accuracy
- [ ] Test installation steps
- [ ] Verify all commands work
- [ ] Get feedback from IT staff (if possible)

## Implementation

### INSTALL.md
```markdown
# Guia de Instalação - Conecta Reparo Mondaí

## Visão Geral

Este guia descreve como instalar e configurar o sistema Conecta Reparo para uso na Associação Hospitalar Mondaí. O sistema é entregue como um conjunto de containers Docker que podem ser executados em qualquer servidor com Docker instalado.

### O que está incluído

- **Backend**: API REST Spring Boot para gerenciamento de manutenções
- **Frontend**: Interface web Next.js para visualização e cadastro
- **Banco de Dados**: PostgreSQL para persistência de dados
- **Proxy Reverso**: Nginx para roteamento de requisições

### Requisitos do Sistema

#### Software Necessário

- Docker Engine 20.10 ou superior
- Docker Compose 2.0 ou superior
- Mínimo 2GB RAM disponível
- Mínimo 10GB espaço em disco
- Porta 80 (ou outra configurável) liberada

#### Verificar Instalação

```bash
docker --version
docker-compose --version
```

#### Recursos Recomendados

- 4GB RAM
- 2 CPUs
- 20GB SSD

## Instalação Rápida

### 1. Obter o Sistema

**Opção A: Via Git (requer Git instalado)**
```bash
git clone https://github.com/fernandocarminatti/conecta-reparo.git
cd conecta-reparo
```

**Opção B: Via arquivo de release (sem Git)**
```bash
# Baixar arquivo .tar.gz da página de releases
tar -xzf conecta-reparo-v1.0.0.tar.gz
cd conecta-reparo-v1.0.0
```

### 2. Configuração (Opcional)

```bash
cp .env.example .env
nano .env
```

**Configurações importantes no arquivo .env:**

```bash
# Senha do banco de dados - ALTERAR EM PRODUÇÃO!
DB_PASSWORD=senha_segura_aqui

# Porta de acesso ao sistema
HOST_PORT=80

# Domínios permitidos para CORS
CORS_ORIGINS=http://localhost,http://manutencao.mondai.local

# URL da API (como vista pelo navegador)
NEXT_PUBLIC_API_URL=http://localhost/api
```

### 3. Iniciar o Sistema

```bash
docker-compose up -d
```

### 4. Verificar Funcionamento

Aguarde aproximadamente 2 minutos para todos os serviços iniciarem.

```bash
# Verificar status dos serviços
docker-compose ps

# Verificar saúde do sistema
curl http://localhost/health

# Acessar a aplicação
# Abra o navegador em http://localhost
```

**Status esperado:**
- Todos os serviços devem estar "Up"
- Health check deve retornar status "UP"
- Frontend acessível na porta configurada

## Configuração de Domínio Interno

Para usar um domínio interno (ex: manutencao.mondai.local):

### Opção A: Arquivo Hosts (Teste/Desenvolvimento)

Editar arquivo hosts do sistema:

**Linux/Mac:** `/etc/hosts`
**Windows:** `C:\Windows\System32\drivers\etc\hosts`

Adicionar linha:
```
IP_DO_SERVIDOR  manutencao.mondai.local
```

Para descobrir o IP do servidor:
```bash
hostname -I
```

### Opção B: Servidor DNS Interno (Produção)

Configurar registro A no DNS interno:
```
manutencao.mondai.local → IP_DO_SERVIDOR
```

### Atualizar CORS

Após configurar o domínio, atualizar o arquivo .env:
```bash
CORS_ORIGINS=http://localhost,http://manutencao.mondai.local
```

Reiniciar os serviços:
```bash
docker-compose restart
```

## Configurações Avançadas

### Alterar Porta de Acesso

Por padrão, o sistema usa a porta 80. Para alterar:

1. Editar arquivo .env:
```bash
HOST_PORT=8080
```

2. Reiniciar:
```bash
docker-compose restart nginx
```

3. Acessar em: http://localhost:8080

### Backup do Banco de Dados

**Criar backup:**
```bash
docker-compose exec db pg_dump -U conectareparo conectareparo > backup_$(date +%Y%m%d).sql
```

**Agendar backups automáticos (cron):**
```bash
# Adicionar ao crontab
0 2 * * * docker-compose exec -T db pg_dump -U conectareparo conectareparo | gzip > /backup/conectareparo_$(date +\%Y\%m\%d).sql.gz
```

**Rota de backup recomendada:** /backup/diario/

### Restaurar Banco de Dados

```bash
docker-compose exec -T db psql -U conectareparo conectareparo < backup.sql
```

### Alterar Senha do Banco

1. Parar serviços:
```bash
docker-compose down
```

2. Editar .env:
```bash
DB_PASSWORD=nova_senha_segura
```

3. Iniciar:
```bash
docker-compose up -d
```

### Atualizar SSL/HTTPS

Para produção, recomenda-se usar HTTPS. Existem duas opções:

**Opção A: Terminação SSL no Nginx**

1. Obter certificado SSL (Let's Encrypt):
```bash
certbot certonly --standalone -d manutencao.mondai.local
```

2. Atualizar configuração do nginx:
```nginx
server {
    listen 443 ssl;
    ssl_certificate /etc/letsencrypt/live/manutencao.mondai.local/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/manutencao.mondai.local/privkey.pem;
    # ... resto da configuração
}

server {
    listen 80;
    return 301 https://$host$request_uri;
}
```

**Opção B: Usar reverse proxy existente**

Configure o sistema atrás do reverse proxy existente da rede do hospital.

## Comandos Úteis

### Parar o Sistema
```bash
docker-compose down
```

### Parar e Remover Volumes (PERDE DADOS!)
```bash
docker-compose down -v
```

### Reiniciar Serviços
```bash
# Reiniciar todos
docker-compose restart

# Reiniciar serviço específico
docker-compose restart backend
docker-compose restart frontend
docker-compose restart nginx
```

### Ver Logs
```bash
# Todos os serviços em tempo real
docker-compose logs -f

# Serviço específico
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f nginx
docker-compose logs -f db

# Últimas 100 linhas
docker-compose logs --tail 100
```

### Ver Uso de Recursos
```bash
docker-compose top
docker stats
```

### Atualizar o Sistema

**Via Git:**
```bash
git pull origin main
docker-compose down
docker-compose build
docker-compose up -d
```

**Via arquivo de release:**
```bash
# Baixar nova versão
tar -xzf conecta-reparo-nova-versao.tar.gz
cd conecta-reparo-nova-versao

# Copiar configuração antiga
cp ../conecta-reparo-antiga/.env .

# Atualizar
docker-compose down
docker-compose up -d
```

## Solução de Problemas

### Porta 80 já em uso

```bash
# Verificar o que está usando a porta 80
sudo netstat -tulpn | grep :80

# Alterar porta no .env
HOST_PORT=8080

# Reiniciar nginx
docker-compose restart nginx
```

### Serviços não iniciam

```bash
# Verificar logs de erro
docker-compose logs

# Verificar status
docker-compose ps

# Soluções comuns:
# 1. Porta do banco em uso
docker-compose down -v
docker-compose up -d

# 2. Reiniciar todos os serviços
docker-compose down
docker-compose up -d
```

### Problemas de conexão backend-frontend

```bash
# Verificar se todos os serviços estão rodando
docker-compose ps

# Testar conectividade backend
docker-compose exec backend curl http://localhost:8080/actuator/health

# Testar conectividade frontend
docker-compose exec frontend curl http://localhost:3000

# Verificar logs do nginx
docker-compose logs nginx
```

### Health check falhando

```bash
# Verificar logs do backend
docker-compose logs backend

# Verificar configuração do banco
docker-compose exec backend env | grep SPRING_DATASOURCE

# Reiniciar backend
docker-compose restart backend
```

### Banco de dados não inicia

```bash
# Verificar logs do banco
docker-compose logs db

# Verificar espaço em disco
df -h

# Verificar permissões
sudo chmod -R 775 postgres_data/
```

### Frontend lento ou com erros

```bash
# Verificar recursos do sistema
docker stats

# Verificar logs do frontend
docker-compose logs frontend

# Limpar cache do navegador
# ou acessar em modo anônimo
```

### Container em loop de restart

```bash
# Verificar motivo do erro
docker-compose logs backend

# Problemas comuns:
# - Banco não disponível (verificar ordem de início)
# - Variáveis de ambiente incorretas
# - Falta de memória
```

## Manutenção de Rotina

### Verificações Diárias

1. Verificar status dos serviços:
```bash
docker-compose ps
```

2. Verificar espaço em disco:
```bash
df -h
docker system df
```

3. Verificar logs de erros:
```bash
docker-compose logs --since 24h | grep -i error
```

### Verificações Semanais

1. Limpar recursos não utilizados:
```bash
docker system prune -a
```

2. Verificar atualizações de segurança:
```bash
docker-compose pull
docker-compose up -d
```

3. Testar backup:
```bash
# Restaurar backup em ambiente de teste
```

### Verificações Mensais

1. Rotação de logs:
```bash
# Verificar tamanho dos logs
du -sh /var/lib/docker/containers/*/*-json.log

# Limpar logs antigos
truncate -s 0 /var/lib/docker/containers/*/*-json.log
```

2. Atualização do sistema:
```bash
docker-compose pull
docker-compose down
docker-compose up -d
```

## Monitoramento

### Endpoints de Saúde

- **Geral**: http://localhost/health
- **Backend detalhado**: http://localhost/actuator/health
- **Métricas**: http://localhost/actuator/prometheus (se configurado)

### Logs Centralizados

Para ambiente de produção, recomenda-se:
- Usar ELK Stack ou similar para agregação de logs
- Configurar log rotation
- Definir alertas para erros

## Segurança

### Recomendações

1. **Alterar senhas padrão** antes de colocar em produção
2. **Configurar SSL** para acesso HTTPS
3. **Restringir acesso** ao servidor
4. **Manter atualizado** com atualizações de segurança
5. **Fazer backups regulares**
6. **Auditar acessos** regularmente
7. **Usar firewall** para restringir portas

### Ports Abertos

Apenas as seguintes portas devem estar acessíveis:
- 80 (HTTP) ou 443 (HTTPS)
- SSH (22) - apenas para administração

### Auditoria

Ativar logs de auditoria do banco de dados:
```sql
ALTER DATABASE conectareparo SET log_statement = 'all';
```

## Suporte

### Antes de Solicitar Suporte

1. Verificar este documento
2. Verificar logs dos serviços
3. Tentar reiniciar os serviços
4. Documentar o erro exact

### Informações para Suporte

Ao abrir uma issue ou solicitar suporte, incluir:

1. **Versão do sistema:**
```bash
git log --oneline -1
docker-compose --version
docker --version
```

2. **Status dos serviços:**
```bash
docker-compose ps
```

3. **Logs relevantes:**
```bash
docker-compose logs --tail=100 > logs.txt
```

4. **Configuração (.env):**
```bash
cat .env (sem senhas!)
```

5. **Rede:**
```bash
docker network ls
docker network inspect conectareparo_conectareparo-net
```

### Contatos

- **GitHub Issues**: https://github.com/fernandocarminatti/conecta-reparo/issues
- **Email**: fernando.carminatti@example.com

## Licença

Este projeto é parte da Atividade Extensionista do curso de Análise e Desenvolvimento de Sistemas - UNINTER.

---

**Última atualização:** Janeiro 2025
**Versão:** 1.0.0
```

## Acceptance Criteria

- [ ] Documentation is clear and complete
- [ ] Installation steps are tested and work
- [ ] Configuration options are documented
- [ ] Troubleshooting section covers common issues
- [ ] Portuguese language appropriate for hospital IT staff
- [ ] All commands are verified
- [ ] Security recommendations included
- [ ] Support information provided

## Definition of Done

- [ ] INSTALL.md created
- [ ] All commands tested
- [ ] Step-by-step installation verified
- [ ] Troubleshooting section complete
- [ ] Documentation reviewed for accuracy
- [ ] Changes committed

## Related Issues

- #33: Docker Compose Orchestration
- #29: Repository Monorepo Restructure
- #38: CI/CD Workflows
