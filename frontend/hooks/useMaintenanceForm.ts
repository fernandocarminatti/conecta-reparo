import { useState, useCallback } from 'react';
import { 
  MaintenanceResponseDto, 
  MaintenanceUpdateDto, 
  MaintenanceFormData 
} from '@/lib/types/maintenance';
import { maintenanceApi } from '@/lib/api/maintenance';

interface UseMaintenanceFormOptions {
  onSuccess?: (data: MaintenanceResponseDto) => void;
  onError?: (error: Error) => void;
}

interface UseMaintenanceFormReturn {
  formData: MaintenanceFormData;
  originalData: MaintenanceResponseDto | null;
  isDirty: boolean;
  isLoading: boolean;
  isSaving: boolean;
  error: string | null;
  hasChanges: (newData: MaintenanceFormData) => boolean;
  updateField: (field: keyof MaintenanceFormData, value: any) => void;
  resetForm: () => void;
  save: () => Promise<MaintenanceResponseDto | null>;
  setFormData: (data: MaintenanceFormData) => void;
}

export function useMaintenanceForm(
  initialData?: MaintenanceResponseDto,
  options: UseMaintenanceFormOptions = {}
): UseMaintenanceFormReturn {
  const { onSuccess, onError } = options;

  const [formData, setFormDataState] = useState<MaintenanceFormData>({
    title: initialData?.title || '',
    description: initialData?.description || '',
    category: initialData?.category || 'OTHERS',
    scheduledDate: initialData?.scheduledDate ? initialData.scheduledDate.split('T')[0] : '',
    status: initialData?.status || 'OPEN',
  });

  const [originalData, setOriginalData] = useState<MaintenanceResponseDto | null>(initialData || null);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const hasChanges = useCallback((newData: MaintenanceFormData): boolean => {
    if (!originalData) return true;
    
    const formFields: (keyof MaintenanceFormData)[] = ['title', 'description', 'category', 'scheduledDate', 'status'];
    
    return formFields.some(field => {
      const formValue = newData[field];
      const originalValue = originalData[field as keyof MaintenanceResponseDto];
      
      if (field === 'scheduledDate') {
        return formValue !== (originalValue as string)?.split('T')[0];
      }
      
      return formValue !== originalValue;
    });
  }, [originalData]);

  const isDirty = originalData ? hasChanges(formData) : false;

  const updateField = useCallback((field: keyof MaintenanceFormData, value: any) => {
    setFormDataState(prev => ({ ...prev, [field]: value }));
  }, []);

  const resetForm = useCallback(() => {
    if (originalData) {
      setFormDataState({
        title: originalData.title,
        description: originalData.description,
        category: originalData.category,
        scheduledDate: originalData.scheduledDate ? originalData.scheduledDate.split('T')[0] : '',
        status: originalData.status,
      });
    }
  }, [originalData]);

  const save = useCallback(async () => {
    if (!originalData || !isDirty) return null;

    setIsSaving(true);
    setError(null);

    try {
      const updateDto: MaintenanceUpdateDto = {};
      
      if (formData.title !== originalData.title) updateDto.title = formData.title;
      if (formData.description !== originalData.description) updateDto.description = formData.description;
      if (formData.category !== originalData.category) updateDto.category = formData.category;
      if (formData.status !== originalData.status) updateDto.status = formData.status;
      
      const saved = await maintenanceApi.update(originalData.id, updateDto);
      
      setOriginalData(saved);
      onSuccess?.(saved);
      
      return saved;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to save');
      setError(error.message);
      onError?.(error);
      return null;
    } finally {
      setIsSaving(false);
    }
  }, [formData, originalData, isDirty, onSuccess, onError]);

  return {
    formData,
    originalData,
    isDirty,
    isLoading: false,
    isSaving,
    error,
    hasChanges,
    updateField,
    resetForm,
    save,
    setFormData: setFormDataState,
  };
}

export default useMaintenanceForm;