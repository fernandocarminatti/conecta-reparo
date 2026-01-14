import { 
  PledgeResponseDto,
  PledgeDetailResponseDto,
  PledgeDto,
  PledgeUpdateDto,
  PledgeFormData,
  PledgeFilter,
  PledgeStatus,
  PledgeCategory
} from "./pledges";

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

export type CategorySelectOptions = { 
  value: string;
  label: string;
  icon: React.ReactNode
}

export type StatusSelectOptions = { 
  value: string;
  label: string;
  icon: React.ReactNode
}

export type ActionStatus = 'SUCCESS' | 'PARTIAL_SUCCESS' | 'FAILURE';

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
  maintenanceId: string;
  maintenanceTitle: string;
  executedBy: string;
  startDate: string;
  completionDate: string | null;
  actionDescription: string;
  materialsUsed: MaterialResponseDto[];
  outcomeStatus: ActionStatus;
  createdAt: string;
  updatedAt: string;
}

export interface MaintenanceActionDetailResponseDto extends MaintenanceActionResponseDto {
  maintenance: {
    id: string;
    title: string;
    category: MaintenanceCategory;
    status: MaintenanceStatus;
    scheduledDate: string;
  };
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

export type MaintenanceFilter = {
  status?: string;
  search?: string;
  category?: string;
  page?: number;
  size?: number;
  sort?: string;
};
