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

export type ActionStatus = 'SUCCESS' | 'PARTIAL_SUCCESS' | 'FAILURE';

export type PledgeStatus = 'OFFERED' | 'PENDING' | 'REJECTED' | 'COMPLETED' | 'CANCELED';
export type PledgeCategory = 'MATERIAL' | 'LABOR';

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

export interface MaintenanceDetailResponseDto extends MaintenanceResponseDto {
  actions: MaintenanceActionResponseDto[];
  pledges: PledgeResponseDto[];
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

export interface MaintenanceFormData {
  title: string;
  description: string;
  category: MaintenanceCategory;
  scheduledDate: string;
  status: MaintenanceStatus;
}

export interface MaintenanceActionResponseDto {
  id: string;
  executedBy: string;
  startDate: string;
  completionDate: string | null;
  actionDescription: string;
  materialsUsed: MaterialResponseDto[];
  outcomeStatus: ActionStatus;
  createdAt: string;
}

export interface MaterialResponseDto {
  id: string;
  itemName: string;
  quantity: number;
  unitOfMeasure: string;
}

export interface MaintenanceActionDto {
  executedBy: string;
  startDate: string;
  completionDate?: string;
  actionDescription: string;
  materials?: MaterialDto[];
  outcomeStatus: ActionStatus;
}

export interface MaterialDto {
  itemName: string;
  quantity: number;
  unitOfMeasure: string;
}

export interface MaintenanceActionUpdateDto {
  executedBy?: string;
  startDate?: string;
  completionDate?: string;
  actionDescription?: string;
  outcomeStatus?: ActionStatus;
}

export interface PledgeResponseDto {
  id: string;
  volunteerName: string;
  volunteerContact: string;
  description: string;
  type: PledgeCategory;
  status: PledgeStatus;
  createdAt: string;
  updatedAt: string;
}

export interface PledgeDto {
  volunteerName: string;
  volunteerContact: string;
  description: string;
  type: PledgeCategory;
  maintenanceId: string;
}

export interface PledgeUpdateDto {
  status?: PledgeStatus;
  description?: string;
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
