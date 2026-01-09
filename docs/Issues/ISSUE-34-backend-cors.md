# [ISSUE #34] Backend CORS Configuration

## Context
Configure Cross-Origin Resource Sharing (CORS) on the Spring Boot backend to allow the frontend application to make API requests without browser security restrictions.

## Current State
- Backend API is being developed
- Frontend will be served from different origin
- No explicit CORS configuration yet
- Spring Boot application uses default security settings

## Target State
- CORS configured via environment variable
- Configurable allowed origins
- Proper handling of OPTIONS preflight requests
- Support for credentials (if needed in future)
- Integration with Docker Compose environment

## Tasks

### Configuration Assessment
- [ ] Verify current Spring Boot version
- [ ] Check if CORS config already exists
- [ ] Identify controller classes that need CORS

### CORS Implementation
- [ ] Create/update CORS configuration class
- [ ] Configure allowed origins via environment variable
- [ ] Configure allowed methods
- [ ] Configure allowed headers
- [ ] Configure credential handling

### Application Properties
- [ ] Update application.yml/properties for CORS settings
- [ ] Add cors.allowed-origins property
- [ ] Add cors.allowed-methods property
- [ ] Add cors.allowed-headers property

### Testing
- [ ] Verify CORS headers in response
- [ ] Test OPTIONS preflight requests
- [ ] Test with frontend development server
- [ ] Verify Docker Compose integration

## Implementation

### Option 1: Using @CrossOrigin Annotations (Per Controller)

Add to each controller class:
```java
@RestController
@RequestMapping("/maintenances")
@CrossOrigin(origins = "${cors.allowed-origins:http://localhost}")
public class MaintenanceController {
    // Controller methods
}
```

### Option 2: Global CORS Configuration (Recommended)

Create `common/config/CorsConfig.java`:
```java
package com.conectareparo.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${cors.allowed-origins:http://localhost}")
    private String[] allowedOrigins;
    
    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;
    
    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;
    
    @Value("${cors.allow-credentials:false}")
    private boolean allowCredentials;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials)
                .maxAge(3600);
    }
}
```

### Update application.yml
```yaml
spring:
  application:
    name: conecta-reparo
  
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost}
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: false
```

## Testing CORS

### Test with curl (Preflight)
```bash
curl -X OPTIONS http://localhost:8080/api/maintenances \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -v
```

Expected response headers:
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
Access-Control-Allow-Headers: *
```

### Test with curl (Actual Request)
```bash
curl http://localhost:8080/api/maintenances \
  -H "Origin: http://localhost:3000" \
  -v
```

### Test with Browser DevTools
1. Start frontend and backend
2. Open browser DevTools (F12)
3. Navigate to frontend page
4. Check Network tab for API requests
5. Verify no CORS errors in Console

### Docker Compose Testing
```bash
# Start services
docker-compose up -d

# Test from host
curl -H "Origin: http://localhost" http://localhost/api/maintenances -v

# Should include CORS headers
```

## Docker Compose Integration

The CORS allowed origins is passed via environment variable in docker-compose.yml:
```yaml
backend:
  environment:
    CORS_ALLOWED_ORIGINS: ${CORS_ORIGINS:-http://localhost}
```

And in .env.example:
```bash
CORS_ORIGINS=http://localhost,http://maintenance.mondai.local
```

## Security Considerations

- Never use `*` for allowed-origins in production
- Specify exact origins that need access
- Review allowed methods - disable unused ones
- Consider rate limiting for public endpoints
- Log CORS violations in production

## Acceptance Criteria

- [ ] CORS configured via environment variable
- [ ] Frontend can make API requests without CORS errors
- [ ] OPTIONS preflight requests handled correctly
- [ ] CORS headers present in responses
- [ ] Docker Compose integration works
- [ ] Configuration documented
- [ ] All tests pass

## Definition of Done

- [ ] CORS configuration class created
- [ ] application.yml updated with CORS properties
- [ ] Environment variable integration works
- [ ] Preflight requests handled correctly
- [ ] Tested with frontend development server
- [ ] Tested with Docker Compose
- [ ] Documentation updated (if needed)
- [ ] Changes committed

## Notes

- For credentials support, allowed-origins cannot be `*`
- Consider adding CORS logging in development
- Spring Security may require additional configuration
- If using Spring Security 6+, CORS is handled before security filters
- Future: Add JWT-based authentication which may affect CORS needs

## Related Issues

- #30: Backend Dockerfile Creation
- #33: Docker Compose Orchestration
- #35: Create API Client Library
