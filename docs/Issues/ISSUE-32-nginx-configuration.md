# [ISSUE #32] Nginx Reverse Proxy Configuration

## Context
Configure Nginx as a reverse proxy to route traffic between the frontend and backend services, handle SSL termination (future), and provide security headers.

## Current State
- `nginx/` directory exists (created in issue #29)
- Backend runs on port 8080
- Frontend runs on port 3000
- Services will be orchestrated by Docker Compose

## Target State
- Nginx configured to route:
  - `/api/*` → backend:8080
  - `/health` → backend:8080/actuator/health
  - All other requests → frontend:3000
- Security headers configured
- Proper proxy headers for backend
- Docker-ready configuration

## Tasks

### Configuration File
- [ ] Create `nginx/nginx.conf`
- [ ] Configure upstream servers
- [ ] Set up location rules
- [ ] Add security headers
- [ ] Configure proxy headers

### Docker Integration
- [ ] Verify volume mount configuration
- [ ] Test with Docker Compose
- [ ] Verify health check routing

## Implementation

### nginx/nginx.conf
```nginx
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # Logging format
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;
    error_log /var/log/nginx/error.log warn;

    # Performance settings
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml application/json application/javascript application/xml;

    # Upstream servers (defined by Docker Compose service names)
    upstream backend {
        server backend:8080;
        keepalive 32;
    }

    upstream frontend {
        server frontend:3000;
        keepalive 32;
    }

    server {
        listen 80;
        server_name _;

        # Security headers
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;
        add_header Referrer-Policy "strict-origin-when-cross-origin" always;
        add_header Permissions-Policy "geolocation=(), microphone=(), camera=()" always;

        # API requests to backend
        location /api/ {
            proxy_pass http://backend/api/;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Host $host;
            proxy_set_header Connection "";
            
            # Timeouts
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
            
            # CORS headers (if needed)
            # add_header Access-Control-Allow-Origin * always;
            # add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
            # add_header Access-Control-Allow-Headers "Authorization, Content-Type" always;
        }

        # Health check endpoint (bypasses /api prefix)
        location /health {
            proxy_pass http://backend/actuator/health;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Actuator metrics endpoint
        location /actuator {
            proxy_pass http://backend/actuator;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # All other requests to frontend
        location / {
            proxy_pass http://frontend;
            proxy_http_version 1.1;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Host $host;
            proxy_set_header Connection "";

            # WebSocket support (for future Next.js features)
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_cache_bypass $http_upgrade;

            # Timeouts
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }

        # Custom error pages
        error_page 502 503 504 /50x.html;
        location = /50x.html {
            root /usr/share/nginx/html;
        }
    }
}
```

## Build and Test

### Test Configuration Syntax
```bash
nginx -t -c /path/to/nginx.conf
```

### Test with Docker Compose
```bash
docker-compose up -d nginx
docker-compose logs nginx

# Test routing
curl -I http://localhost/health
curl -I http://localhost/api/maintenances
curl -I http://localhost/
```

### Verify Headers
```bash
curl -I http://localhost
# Should include X-Frame-Options, X-Content-Type-Options, etc.
```

## Integration Points

### Docker Compose Service Definition
The nginx service in docker-compose.yml should mount the config:
```yaml
nginx:
  image: nginx:alpine
  ports:
    - "${HOST_PORT:-80}:80"
  volumes:
    - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
  depends_on:
    - frontend
    - backend
```

## Acceptance Criteria

- [ ] Nginx routes `/api/*` to backend correctly
- [ ] Nginx routes `/health` to backend health endpoint
- [ ] Nginx routes all other paths to frontend
- [ ] Security headers are set on all responses
- [ ] Proxy headers are correctly forwarded
- [ ] Health check endpoint is accessible
- [ ] Configuration works with Docker Compose
- [ ] No 502/504 errors under normal operation
- [ ] Gzip compression enabled

## Definition of Done

- [ ] nginx.conf created and tested
- [ ] All routing rules work correctly
- [ ] Security headers verified
- [ ] Health check endpoint accessible
- [ ] Integration tested with docker-compose
- [ ] Documentation updated (if needed)
- [ ] Changes committed

## Notes

- This configuration assumes HTTP (port 80)
- For HTTPS, add SSL certificate configuration
- Keepalive connections improve performance
- Proxy timeout values may need adjustment
- Consider adding rate limiting in future
- Consider adding caching headers for static assets
- The `proxy_set_header Connection ""` is important for HTTP/1.1

## Related Issues

- #29: Repository Monorepo Restructure
- #34: Docker Compose Orchestration
- #30: Backend Dockerfile Creation
- #36: Frontend Dockerfile Creation
