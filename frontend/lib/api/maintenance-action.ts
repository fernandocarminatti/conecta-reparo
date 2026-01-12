import { 
  MaintenanceActionResponseDto, 
  MaintenanceActionDetailResponseDto,
  MaintenanceActionDto, 
  MaintenanceActionUpdateDto,
  MaterialDto
} from '@/lib/types/maintenance';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'An error occurred' }));
    throw new Error(error.message || `HTTP error! status: ${response.status}`);
  }
  return response.json();
}

export const maintenanceActionApi = {
  async getAll(): Promise<MaintenanceActionResponseDto[]> {
    const response = await fetch(`${API_BASE_URL}/api/v1/actions`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    return handleResponse<MaintenanceActionResponseDto[]>(response);
  },

  async getById(maintenanceId: string, actionId: string): Promise<MaintenanceActionResponseDto> {
    const response = await fetch(`${API_BASE_URL}/api/v1/maintenances/${maintenanceId}/actions/${actionId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    return handleResponse<MaintenanceActionResponseDto>(response);
  },

  async getByMaintenanceId(maintenanceId: string): Promise<MaintenanceActionResponseDto[]> {
    const response = await fetch(`${API_BASE_URL}/api/v1/maintenances/${maintenanceId}/actions`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    return handleResponse<MaintenanceActionResponseDto[]>(response);
  },

  async create(maintenanceId: string, data: MaintenanceActionDto): Promise<MaintenanceActionResponseDto> {
    const response = await fetch(`${API_BASE_URL}/api/v1/maintenances/${maintenanceId}/actions`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    return handleResponse<MaintenanceActionResponseDto>(response);
  },

  async update(maintenanceId: string, actionId: string, data: MaintenanceActionUpdateDto): Promise<MaintenanceActionResponseDto> {
    const response = await fetch(`${API_BASE_URL}/api/v1/maintenances/${maintenanceId}/actions/${actionId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    return handleResponse<MaintenanceActionResponseDto>(response);
  },
};

export default maintenanceActionApi;