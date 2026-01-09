# [ISSUE #36] Create Frontend Dockerfile

## Context
Create a Docker container for the Next.js frontend application using multi-stage builds for optimized production deployment.

## Current State
- Next.js frontend is initialized (issue #31)
- Frontend development server runs successfully
- API client library created (issue #35)
- No Docker configuration for frontend yet

## Target State
- Frontend Docker image built using multi-stage build
- Production-ready Next.js standalone output
- Environment variables properly configured
- Optimized image size
- Health check configured

## Tasks

### Dockerfile Creation
- [ ] Create `frontend/Dockerfile`
- [ ] Implement multi-stage build (deps, builder, runner)
- [ ] Configure Next.js standalone output
- [ ] Set up environment variable passing
- [ ] Configure health check
- [ ] Optimize for production

### Next.js Configuration
- [ ] Update next.config.js for standalone output
- [ ] Configure environment variables in Next.js
- [ ] Verify output configuration

### Build Optimization
- [ ] Exclude unnecessary files from build context
- [ ] Minimize layer count
- [ ] Use appropriate base image tags

### Testing
- [ ] Build Docker image
- [ ] Test container startup
- [ ] Verify environment variables work
- [ ] Test with docker-compose

## Implementation

### frontend/Dockerfile
```dockerfile
# Stage 1: Dependencies
FROM node:18-alpine AS deps
WORKDIR /app

# Install dependencies only when needed
RUN apk add --no-cache libc6-compat

# Copy package files
COPY package.json package-lock.json* ./

# Install dependencies
RUN npm ci

# Stage 2: Builder
FROM node:18-alpine AS builder
WORKDIR /app

# Copy dependency layer from deps stage
COPY --from=deps /app/node_modules ./node_modules

# Copy application source
COPY . .

# Build the application
ARG NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_URL=${NEXT_PUBLIC_API_URL}

RUN npm run build

# Stage 3: Runner
FROM node:18-alpine AS runner
WORKDIR /app

ENV NODE_ENV production

# Create non-root user for security
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

# Copy necessary files from builder
COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

# Set ownership
USER nextjs

# Expose the port Next.js runs on
EXPOSE 3000

# Environment variables
ENV PORT 3000
ENV HOSTNAME "0.0.0.0"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:3000 || exit 1

# Start the application
CMD ["node", "server.js"]
```

### frontend/.dockerignore
```
node_modules
.next
.git
.gitignore
Dockerfile
.dockerignore
npm-debug.log
README.md
.env
.env.local
.env.*.local
*.md
```

### Update next.config.js
```javascript
/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone',
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  },
  images: {
    domains: ['localhost'],
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: `${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'}/:path*`,
      },
    ];
  },
};

module.exports = nextConfig;
```

## Build and Test

### Build the image
```bash
cd frontend
docker build -t conectareparo-frontend:test .
```

### Test the container
```bash
# Run in detached mode
docker run -p 3000:3000 \
  -e NEXT_PUBLIC_API_URL=http://localhost:8080 \
  conectareparo-frontend:test

# Check logs
docker logs <container_id>

# Test health endpoint
curl http://localhost:3000

# Test API routing
curl http://localhost:3000/api/maintenances
```

### Verify with Docker Compose
```bash
docker-compose build frontend
docker-compose up -d frontend
docker-compose logs frontend
```

## Environment Variables

The Dockerfile expects these build arguments:
- `NEXT_PUBLIC_API_URL` - API URL for the frontend to connect to (build-time)

Runtime environment variables:
- `NEXT_PUBLIC_API_URL` - API URL (can be overridden at runtime)

## Production Considerations

### Multi-stage Build Benefits
- Smaller final image (no build tools)
- Better caching (dependencies layer cached)
- Security (no npm in production image)

### Image Size Optimization
- Base image: ~50MB (Alpine)
- Final image: ~150MB (includes Node.js, Next.js standalone)
- Compared to: ~900MB (full Node.js image)

### Security
- Runs as non-root user (nextjs)
- Minimal packages installed
- No package managers in production image

## Acceptance Criteria

- [ ] Multi-stage Dockerfile created
- [ ] Frontend Docker image builds successfully
- [ ] Next.js standalone output configured
- [ ] Production build works
- [ ] Environment variables passed correctly
- [ ] Health check configured
- [ ] Image size is reasonable (<200MB)
- [ ] Works with docker-compose
- [ ] Container runs as non-root user

## Definition of Done

- [ ] Dockerfile created and tested
- [ ] next.config.js updated for standalone
- [ ] Image builds without errors
- [ ] Application runs in container
- [ ] Environment variables work
- [ ] Health check responds
- [ ] Integration tested with docker-compose
- [ ] Changes committed

## Notes

- Next.js standalone mode reduces image size significantly
- The `public` directory is still needed for static assets
- Consider adding a base path if deploying to subdirectory
- For serverless deployment, use different output mode
- The rewrites in next.config.js help with API proxying
- Consider adding caching headers in nginx for static assets

## Related Issues

- #31: Initialize Next.js Frontend
- #32: Nginx Configuration
- #33: Docker Compose Orchestration
- #35: API Client Library
