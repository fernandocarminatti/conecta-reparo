import { 
  PledgeResponseDto, 
  PledgeDetailResponseDto,
  PledgeDto, 
  PledgeUpdateDto,
  PageResponse,
  PledgeFilter 
} from '@/lib/types/maintenance';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'An error occurred' }));
    throw new Error(error.message || `HTTP error! status: ${response.status}`);
  }
  return response.json();
}

export const pledgeApi = {
  async getAll(filter: PledgeFilter = {}): Promise<PageResponse<PledgeResponseDto>> {
    const params = new URLSearchParams();
    
    if (filter.status) params.append('status', filter.status);
    if (filter.type) params.append('type', filter.type);
    if (filter.search) params.append('search', filter.search);
    if (filter.page !== undefined) params.append('page', filter.page.toString());
    if (filter.size !== undefined) params.append('size', filter.size.toString());
    if (filter.sort) params.append('sort', filter.sort);

    const queryString = params.toString();
    const url = `${API_BASE_URL}/api/v1/pledges${queryString ? `?${queryString}` : ''}`;

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    return handleResponse<PageResponse<PledgeResponseDto>>(response);
  },

  async getById(id: string): Promise<PledgeResponseDto> {
    const response = await fetch(`${API_BASE_URL}/api/v1/pledges/${id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    return handleResponse<PledgeResponseDto>(response);
  },

  async getByMaintenanceId(maintenanceId: string, filter: PledgeFilter = {}): Promise<PledgeResponseDto[]> {
    const params = new URLSearchParams();
    params.append('maintenanceId', maintenanceId);
    
    if (filter.page !== undefined) params.append('page', filter.page.toString());
    if (filter.size !== undefined) params.append('size', filter.size.toString());
    if (filter.sort) params.append('sort', filter.sort);

    const queryString = params.toString();
    const url = `${API_BASE_URL}/api/v1/pledges?${queryString}`;

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    const pageResponse = await handleResponse<PageResponse<PledgeResponseDto>>(response);
    return pageResponse.content;
  },

  async create(data: PledgeDto): Promise<PledgeResponseDto> {
    const response = await fetch(`${API_BASE_URL}/api/v1/pledges`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    return handleResponse<PledgeResponseDto>(response);
  },

  async update(id: string, data: PledgeUpdateDto): Promise<PledgeResponseDto> {
    const response = await fetch(`${API_BASE_URL}/api/v1/pledges/${id}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });

    return handleResponse<PledgeResponseDto>(response);
  },
};

export default pledgeApi;