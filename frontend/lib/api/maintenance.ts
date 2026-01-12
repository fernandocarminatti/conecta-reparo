import { 
  MaintenanceResponseDto, 
  MaintenanceDto, 
  MaintenanceUpdateDto,
  PageResponse,
  MaintenanceFilter 
} from '@/lib/types/maintenance';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'An error occurred' }));
    throw new Error(error.message || `HTTP error! status: ${response.status}`);
  }
  return response.json();
}

export const maintenanceApi = {
  async getAll(filter: MaintenanceFilter = {}): Promise<PageResponse<MaintenanceResponseDto>> {
    const params = new URLSearchParams();
    
    if (filter.status) params.append('status', filter.status);
    if (filter.search) params.append('search', filter.search);
    if (filter.page !== undefined) params.append('page', filter.page.toString());
    if (filter.size !== undefined) params.append('size', filter.size.toString());
    if (filter.sort) params.append('sort', filter.sort);

    const queryString = params.toString();
    const url = `${API_BASE_URL}/api/v1/maintenances${queryString ? `?${queryString}` : ''}`;

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    return handleResponse<PageResponse<MaintenanceResponseDto>>(response);
  },

  async getById(id: string): Promise<MaintenanceResponseDto> {
    const response = await fetch(`${API_BASE_URL}/api/v1/maintenances/${id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    return handleResponse<MaintenanceResponseDto>(response);
  },

  async create(data: MaintenanceDto): Promise<MaintenanceResponseDto> {
    const response = await fetch(`${API_BASE_URL}/api/v1/maintenances`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    return handleResponse<MaintenanceResponseDto>(response);
  },

  async update(id: string, data: MaintenanceUpdateDto): Promise<MaintenanceResponseDto> {
    const response = await fetch(`${API_BASE_URL}/api/v1/maintenances/${id}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    return handleResponse<MaintenanceResponseDto>(response);
  },

  async getActions(maintenanceId: string): Promise<any[]> {
    const response = await fetch(`${API_BASE_URL}/api/v1/maintenances/${maintenanceId}/actions`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    return handleResponse<any[]>(response);
  },
};

export default maintenanceApi;