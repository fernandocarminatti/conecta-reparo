export type MaintenanceStatus = 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELED';
export type MaintenanceCategory = 
  | 'BUILDING' 
  | 'ELECTRICAL' 
  | 'PLUMBING' 
  | 'HVAC' 
  | 'FURNITURE' 
  | 'GARDENING' 
  | 'SECURITY' 
  | 'OTHERS';

export interface MaintenanceResponseDto {
  id: string;
  title: string;
  description: string;
  category: MaintenanceCategory;
  scheduledDate: string;
  status: MaintenanceStatus;
  createdAt: string;
  updatedAt: string;
}

export interface MaintenanceDto {
  title: string;
  description: string;
  category: MaintenanceCategory;
  scheduledDate: string;
}

export interface MaintenanceUpdateDto {
  title?: string;
  description?: string;
  category?: MaintenanceCategory;
  status?: MaintenanceStatus;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  first: boolean;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  numberOfElements: number;
  empty: boolean;
}

export type MaintenanceFilter = {
  status?: string;
  search?: string;
  category?: string;
  page?: number;
  size?: number;
  sort?: string;
};
