import { MaintenanceCategory, MaintenanceStatus } from "./maintenance";

export type PledgeStatus = 'OFFERED' | 'PENDING' | 'REJECTED' | 'COMPLETED' | 'CANCELED';
export type PledgeCategory = 'MATERIAL' | 'LABOR';

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

export interface PledgeResponseDto {
    id: string;
    volunteerName: string;
    volunteerContact: string;
    description: string;
    type: PledgeCategory;
    status: PledgeStatus;
    createdAt: string;
    updatedAt: string;
    maintenanceId?: string;
    maintenanceTitle?: string;
}



export interface PledgeDetailResponseDto extends PledgeResponseDto {
    maintenance: {
        id: string;
        title: string;
        category: MaintenanceCategory;
        status: MaintenanceStatus;
        scheduledDate: string;
    };
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
    volunteerName?: string;
    volunteerContact?: string;
    type?: PledgeCategory;
}

export interface PledgeFormData {
    volunteerName: string;
    volunteerContact: string;
    description: string;
    type: PledgeCategory;
    status: PledgeStatus;
}

export type PledgeFilter = {
    status?: string;
    type?: string;
    search?: string;
    page?: number;
    size?: number;
    sort?: string;
};