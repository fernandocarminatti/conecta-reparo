# [ISSUE #38] Create CI/CD Workflows

## Context
Create GitHub Actions workflows for automated testing, building, and releasing the Conecta Reparo application.

## Current State
- Repository is a monorepo with backend and frontend
- Dockerfiles created for all services
- Docker Compose configured for deployment
- No CI/CD automation yet

## Target State
- Test workflow runs on every PR and push to main
- Release workflow creates deployable artifacts on version tags
- All Docker images built and tested
- Release notes auto-generated

## Tasks

### Test Workflow
- [ ] Create `.github/workflows/test.yml`
- [ ] Configure backend build and test job
- [ ] Configure frontend build and lint job
- [ ] Configure Docker build job
- [ ] Configure integration test job
- [ ] Set up workflow triggers

### Release Workflow
- [ ] Create `.github/workflows/release.yml`
- [ ] Configure version tag trigger
- [ ] Configure Docker image builds
- [ ] Configure artifact packaging
- [ ] Configure GitHub release creation
- [ ] Add checksum generation

### Workflow Testing
- [ ] Test workflow syntax
- [ ] Verify all jobs run correctly
- [ ] Test with sample PR
- [ ] Verify release workflow on tag

## Implementation

### .github/workflows/test.yml
```yaml
name: Test Build

on:
  pull_request:
    branches: [ main ]
  push:
    branches: [ main ]

env:
  DB_NAME: conectareparo
  DB_USER: conectareparo
  DB_PASSWORD: test_password

jobs:
  test-backend:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'

      - name: Build backend
        working-directory: ./backend
        run: ./mvnw clean package -DskipTests -B

      - name: Run backend tests
        working-directory: ./backend
        run: ./mvnw test -B

      - name: Upload test results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: backend-test-results
          path: backend/target/surefire-reports/

  test-frontend:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: frontend/package-lock.json

      - name: Install dependencies
        working-directory: ./frontend
        run: npm ci

      - name: Run linter
        working-directory: ./frontend
        run: npm run lint

      - name: TypeScript check
        working-directory: ./frontend
        run: npx tsc --noEmit

      - name: Build frontend
        working-directory: ./frontend
        run: npm run build

      - name: Upload build artifacts
        uses: actions/upload-artifact@v4
        with:
          name: frontend-build
          path: frontend/.next/

  test-docker:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Build backend image
        uses: docker/build-push-action@v5
        with:
          context: ./backend
          push: false
          tags: conectareparo-backend:test
          load: true

      - name: Build frontend image
        uses: docker/build-push-action@v5
        with:
          context: ./frontend
          push: false
          tags: conectareparo-frontend:test
          load: true

      - name: Build nginx image
        uses: docker/build-push-action@v5
        with:
          context: ./nginx
          push: false
          tags: conectareparo-nginx:test
          load: true

  integration-test:
    runs-on: ubuntu-latest
    needs: [test-backend, test-frontend, test-docker]
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Docker Compose
        run: |
          sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
          sudo chmod +x /usr/local/bin/docker-compose

      - name: Start services
        run: docker-compose up -d

      - name: Wait for services
        run: |
          echo "Waiting for services to be ready..."
          sleep 30
          timeout 60 bash -c 'until curl -sf http://localhost/health; do sleep 5; done'

      - name: Run integration tests
        run: |
          # Simple health check test
          curl -sf http://localhost/health
          curl -sf http://localhost/api/maintenances
          echo "Integration tests passed!"

      - name: Check frontend
        run: |
          curl -sf -o /dev/null -w "%{http_code}" http://localhost/

      - name: Cleanup
        if: always()
        run: docker-compose down -v
```

### .github/workflows/release.yml
```yaml
name: Build Deployable Release

on:
  push:
    tags:
      - 'v*'

jobs:
  build-and-release:
    runs-on: ubuntu-latest
    permissions:
      contents: write
      packages: write
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Log in to Container Registry
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Extract metadata for Backend
        id: meta-backend
        uses: docker/metadata-action@v5
        with:
          images: ghcr.io/${{ github.repository }}/backend
          tags: |
            type=semver,pattern={{version}}
            type=sha

      - name: Build and push Backend
        uses: docker/build-push-action@v5
        with:
          context: ./backend
          push: true
          tags: ${{ steps.meta-backend.outputs.tags }}
          labels: ${{ steps.meta-backend.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: Extract metadata for Frontend
        id: meta-frontend
        uses: docker/metadata-action@v5
        with:
          images: ghcr.io/${{ github.repository }}/frontend
          tags: |
            type=semver,pattern={{version}}
            type=sha

      - name: Build and push Frontend
        uses: docker/build-push-action@v5
        with:
          context: ./frontend
          push: true
          tags: ${{ steps.meta-frontend.outputs.tags }}
          labels: ${{ steps.meta-frontend.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: Extract metadata for Nginx
        id: meta-nginx
        uses: docker/metadata-action@v5
        with:
          images: ghcr.io/${{ github.repository }}/nginx
          tags: |
            type=semver,pattern={{version}}
            type=sha

      - name: Build and push Nginx
        uses: docker/build-push-action@v5
        with:
          context: ./nginx
          push: true
          tags: ${{ steps.meta-nginx.outputs.tags }}
          labels: ${{ steps.meta-nginx.outputs.labels }}

      - name: Build all Docker images locally
        run: |
          docker-compose build

      - name: Save Docker images
        run: |
          docker save -o conecta-reparo-images.tar \
            conectareparo-backend:latest \
            conectareparo-frontend:latest \
            conectareparo-nginx:latest \
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
          cat checksums.txt

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v2
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
            - Imagens Docker

            ### Verificação
            Checksum SHA256 disponível em `checksums.txt`

            ### Imagens Docker
            Imagens também disponíveis no GitHub Container Registry:
            - `ghcr.io/${{ github.repository }}/backend:${{ github.ref_name }}`
            - `ghcr.io/${{ github.repository }}/frontend:${{ github.ref_name }}`
            - `ghcr.io/${{ github.repository }}/nginx:${{ github.ref_name }}`
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

      - name: Upload images tarball
        uses: actions/upload-artifact@v4
        with:
          name: release-artifacts
          path: conecta-reparo-${{ github.ref_name }}.tar.gz

      - name: Upload checksums
        uses: actions/upload-artifact@v4
        with:
          name: checksums
          path: checksums.txt
```

## Release Process

### Creating a Release

1. **Prepare changes:**
```bash
git checkout main
git pull origin main

# Make your changes, commit them
git add .
git commit -m "Description of changes"
git push origin main
```

2. **Create and push tag:**
```bash
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

3. **Wait for workflow:**
- GitHub Actions will run tests
- If tests pass, Docker images will be built
- Release will be created automatically

4. **Verify release:**
- Check GitHub Releases page
- Download deployable tarball
- Verify checksums

### Release Checklist

- [ ] All tests pass
- [ ] Version bumped appropriately
- [ ] CHANGELOG.md updated
- [ ] Documentation updated
- [ ] Tag pushed
- [ ] Release created successfully
- [ ] Installation tested

## Workflow Triggers

### Test Workflow
- **PR to main**: Runs all tests
- **Push to main**: Runs all tests
- **Manual dispatch**: Available via GitHub UI

### Release Workflow
- **Version tag pushed**: Creates release
- **Manual dispatch**: Available via GitHub UI

## Acceptance Criteria

- [ ] Test workflow triggers on PR and push
- [ ] All jobs (backend, frontend, docker, integration) run
- [ ] Release workflow triggers on version tags
- [ ] Docker images built and pushed to registry
- [ ] Release artifacts generated
- [ ] Checksums included
- [ ] Release notes auto-generated
- [ ] All tests pass before release

## Definition of Done

- [ ] test.yml created and tested
- [ ] release.yml created and tested
- [ ] Workflows run without errors
- [ ] Release workflow creates proper releases
- [ ] Documentation updated (if needed)
- [ ] Changes committed

## Notes

- GitHub Actions minutes are free for public repositories
- Consider adding branch protection rules
- Require status checks before merging
- Use environments for production deployments
- Consider adding manual approval for releases
- Cache actions for faster builds
- Docker layer caching requires GHA cache backend

## Related Issues

- #29: Repository Monorepo Restructure
- #30: Backend Dockerfile Creation
- #36: Frontend Dockerfile Creation
- #37: Installation Documentation
