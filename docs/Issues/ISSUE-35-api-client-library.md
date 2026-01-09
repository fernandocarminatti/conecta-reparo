# [ISSUE #35] Create Frontend API Client Library

## Context
Create a type-safe API client library for the frontend to communicate with the backend. This includes TypeScript interfaces, fetch wrappers, and proper error handling.

## Current State
- Next.js frontend is initialized (issue #31)
- Backend API endpoints are defined
- No API client library exists yet
- Components need to make API calls

## Target State
- TypeScript interfaces matching backend entities
- Centralized API client with proper error handling
- Environment variable configuration
- Reusable fetch wrappers
- Support for public and admin endpoints

## Tasks

### TypeScript Interfaces
- [ ] Create `frontend/lib/types.ts`
- [ ] Define Maintenance type/interface
- [ ] Define Pledge type/interface
- [ ] Define related DTO types
- [ ] Export all types

### API Client Implementation
- [ ] Create `frontend/lib/api.ts`
- [ ] Implement fetch wrapper with error handling
- [ ] Create maintenances API methods
- [ ] Create pledges API methods
- [ ] Create admin API methods
- [ ] Add environment variable for API URL

### Configuration
- [ ] Verify NEXT_PUBLIC_API_URL works
- [ ] Configure base URL for different environments
- [ ] Add type safety for API responses

### Testing
- [ ] Test API client compiles without errors
- [ ] Test with backend API endpoints
- [ ] Verify error handling works
- [ ] Test environment variable loading

## Implementation

### frontend/lib/types.ts
```typescript
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
export type MaintenanceStatus = 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
export type PledgeType = 'MATERIAL' | 'VOLUNTEER';

export interface Maintenance {
  id: number;
  title: string;
  description: string;
  priority: Priority;
  status: MaintenanceStatus;
  createdAt: string;
  updatedAt?: string;
  completedAt?: string;
  estimatedCost?: number;
  actualCost?: number;
  location?: string;
  images?: string[];
}

export interface MaintenanceCreateRequest {
  title: string;
  description: string;
  priority: Priority;
  location?: string;
  estimatedCost?: number;
}

export interface MaintenanceUpdateRequest {
  title?: string;
  description?: string;
  priority?: Priority;
  status?: MaintenanceStatus;
  completedAt?: string;
  actualCost?: number;
}

export interface Pledge {
  id: number;
  maintenanceId: number;
  donorName: string;
  donorContact: string;
  pledgeType: PledgeType;
  description: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'FULFILLED';
  createdAt: string;
  updatedAt?: string;
}

export interface PledgeCreateRequest {
  maintenanceId: number;
  donorName: string;
  donorContact: string;
  pledgeType: PledgeType;
  description: string;
}

export interface PaginatedResponse<T> {
  data: T[];
  page: number;
  pageSize: number;
  totalItems: number;
  totalPages: number;
}

export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
  path: string;
}
```

### frontend/lib/api.ts
```typescript
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

class ApiClient {
  private baseUrl: string;

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl;
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const url = `${this.baseUrl}${endpoint}`;
    
    const defaultHeaders: HeadersInit = {
      'Content-Type': 'application/json',
    };

    const config: RequestInit = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        const error = await response.json().catch(() => ({
          message: 'An error occurred',
          status: response.status,
        }));
        throw new ApiError(error.message || 'Request failed', response.status);
      }

      // Handle empty responses
      if (response.status === 204) {
        return undefined as T;
      }

      return response.json();
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }
      throw new ApiError(
        error instanceof Error ? error.message : 'Network error',
        0
      );
    }
  }
}

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export const api = {
  // Configure base URL
  setBaseUrl(url: string) {
    apiClient.baseUrl = url;
  },

  // Maintenances
  maintenances: {
    list: async (params?: {
      status?: MaintenanceStatus;
      priority?: Priority;
      page?: number;
      pageSize?: number;
    }): Promise<PaginatedResponse<Maintenance>> => {
      const searchParams = new URLSearchParams();
      if (params?.status) searchParams.set('status', params.status);
      if (params?.priority) searchParams.set('priority', params.priority);
      if (params?.page) searchParams.set('page', params.page.toString());
      if (params?.pageSize) searchParams.set('pageSize', params.pageSize.toString());
      
      const query = searchParams.toString();
      return apiClient.request(`/maintenances${query ? `?${query}` : ''}`);
    },

    getById: async (id: number): Promise<Maintenance> => {
      return apiClient.request(`/maintenances/${id}`);
    },

    create: async (data: MaintenanceCreateRequest): Promise<Maintenance> => {
      return apiClient.request('/maintenances', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    },

    update: async (
      id: number,
      data: Partial<MaintenanceUpdateRequest>
    ): Promise<Maintenance> => {
      return apiClient.request(`/maintenances/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      });
    },

    delete: async (id: number): Promise<void> => {
      return apiClient.request(`/maintenances/${id}`, {
        method: 'DELETE',
      });
    },
  },

  // Pledges
  pledges: {
    create: async (data: PledgeCreateRequest): Promise<Pledge> => {
      return apiClient.request('/pledges', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    },

    listByMaintenance: async (maintenanceId: number): Promise<Pledge[]> => {
      return apiClient.request(`/maintenances/${maintenanceId}/pledges`);
    },

    updateStatus: async (
      id: number,
      status: Pledge['status']
    ): Promise<Pledge> => {
      return apiClient.request(`/pledges/${id}/status`, {
        method: 'PATCH',
        body: JSON.stringify({ status }),
      });
    },
  },

  // Admin endpoints (add auth headers when implemented)
  admin: {
    dashboard: async (): Promise<{
      totalMaintenances: number;
      openMaintenances: number;
      completedThisMonth: number;
      totalPledges: number;
    }> => {
      return apiClient.request('/admin/dashboard');
    },

    maintenances: {
      list: async (params?: {
        status?: MaintenanceStatus;
        page?: number;
        pageSize?: number;
      }): Promise<PaginatedResponse<Maintenance>> => {
        const searchParams = new URLSearchParams();
        if (params?.status) searchParams.set('status', params.status);
        if (params?.page) searchParams.set('page', params.page.toString());
        if (params?.pageSize) searchParams.set('pageSize', params.pageSize.toString());
        
        const query = searchParams.toString();
        return apiClient.request(`/admin/maintenances${query ? `?${query}` : ''}`);
      },

      create: async (data: MaintenanceCreateRequest): Promise<Maintenance> => {
        return apiClient.request('/admin/maintenances', {
          method: 'POST',
          body: JSON.stringify(data),
        });
      },

      update: async (
        id: number,
        data: Partial<MaintenanceUpdateRequest>
      ): Promise<Maintenance> => {
        return apiClient.request(`/admin/maintenances/${id}`, {
          method: 'PUT',
          body: JSON.stringify(data),
        });
      },

      history: async (params?: {
        from?: string;
        to?: string;
        page?: number;
      }): Promise<PaginatedResponse<Maintenance>> => {
        const searchParams = new URLSearchParams();
        if (params?.from) searchParams.set('from', params.from);
        if (params?.to) searchParams.set('to', params.to);
        if (params?.page) searchParams.set('page', params.page.toString());
        
        const query = searchParams.toString();
        return apiClient.request(`/admin/maintenances/history${query ? `?${query}` : ''}`);
      },
    },

    pledges: {
      list: async (params?: {
        status?: Pledge['status'];
        page?: number;
      }): Promise<PaginatedResponse<Pledge>> => {
        const searchParams = new URLSearchParams();
        if (params?.status) searchParams.set('status', params.status);
        if (params?.page) searchParams.set('page', params.page.toString());
        
        const query = searchParams.toString();
        return apiClient.request(`/admin/pledges${query ? `?${query}` : ''}`);
      },

      updateStatus: async (
        id: number,
        status: Pledge['status']
      ): Promise<Pledge> => {
        return apiClient.request(`/admin/pledges/${id}/status`, {
          method: 'PATCH',
          body: JSON.stringify({ status }),
        });
      },
    },
  },
};

// Create client instance
const apiClient = new ApiClient(API_URL);

export default api;
```

## Usage Examples

### Fetching Maintenances
```typescript
import { api } from '@/lib/api';

async function loadMaintenances() {
  try {
    const response = await api.maintenances.list({ status: 'OPEN' });
    return response.data;
  } catch (error) {
    console.error('Failed to load maintenances:', error);
    return [];
  }
}
```

### Creating a Pledge
```typescript
import { api, PledgeCreateRequest } from '@/lib/api';

async function submitPledge(data: PledgeCreateRequest) {
  try {
    const pledge = await api.pledges.create(data);
    return pledge;
  } catch (error) {
    console.error('Failed to submit pledge:', error);
    throw error;
  }
}
```

### Error Handling
```typescript
import { api, ApiError } from '@/lib/api';

try {
  const maintenance = await api.maintenances.getById(1);
} catch (error) {
  if (error instanceof ApiError) {
    console.error(`Error ${error.status}: ${error.message}`);
  }
}
```

## Acceptance Criteria

- [ ] TypeScript interfaces match backend entities
- [ ] API client compiles without TypeScript errors
- [ ] TypeScript types are properly exported
- [ ] Environment variable NEXT_PUBLIC_API_URL works
- [ ] Error handling implemented for all requests
- [ ] Public and admin endpoints supported
- [ ] Documentation with usage examples
- [ ] All tests pass

## Definition of Done

- [ ] types.ts created with all entity interfaces
- [ ] api.ts created with full API client
- [ ] TypeScript compiles without errors
- [ ] Tested with backend API
- [ ] Error handling verified
- [ ] Documentation added
- [ ] Changes committed

## Notes

- Consider using React Query or SWR for data fetching
- Add authentication headers when auth is implemented
- Consider adding request/response interceptors
- This is a basic client - enhance with caching later
- Types should match backend DTOs exactly

## Related Issues

- #31: Initialize Next.js Frontend
- #34: Backend CORS Configuration
- #36: Frontend Dockerfile Creation
