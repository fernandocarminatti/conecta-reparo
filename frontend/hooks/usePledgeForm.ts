import { useState, useCallback } from 'react';
import { 
  PledgeResponseDto, 
  PledgeUpdateDto, 
  PledgeFormData 
} from '@/lib/types/maintenance';
import { pledgeApi } from '@/lib/api/pledge';

interface UsePledgeFormOptions {
  onSuccess?: (data: PledgeResponseDto) => void;
  onError?: (error: Error) => void;
}

interface UsePledgeFormReturn {
  formData: PledgeFormData;
  originalData: PledgeResponseDto | null;
  isDirty: boolean;
  isLoading: boolean;
  isSaving: boolean;
  error: string | null;
  hasChanges: (newData: PledgeFormData) => boolean;
  updateField: (field: keyof PledgeFormData, value: any) => void;
  resetForm: () => void;
  save: () => Promise<PledgeResponseDto | null>;
  setFormData: (data: PledgeFormData) => void;
}

export function usePledgeForm(
  initialData?: PledgeResponseDto,
  options: UsePledgeFormOptions = {}
): UsePledgeFormReturn {
  const { onSuccess, onError } = options;

  const [formData, setFormDataState] = useState<PledgeFormData>({
    volunteerName: initialData?.volunteerName || '',
    volunteerContact: initialData?.volunteerContact || '',
    description: initialData?.description || '',
    type: initialData?.type || 'MATERIAL',
    status: initialData?.status || 'OFFERED',
  });

  const [originalData, setOriginalData] = useState<PledgeResponseDto | null>(initialData || null);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const hasChanges = useCallback((newData: PledgeFormData): boolean => {
    if (!originalData) return true;
    
    const formFields: (keyof PledgeFormData)[] = ['volunteerName', 'volunteerContact', 'description', 'type', 'status'];
    
    return formFields.some(field => newData[field] !== originalData[field as keyof PledgeResponseDto]);
  }, [originalData]);

  const isDirty = originalData ? hasChanges(formData) : false;

  const updateField = useCallback((field: keyof PledgeFormData, value: any) => {
    setFormDataState(prev => ({ ...prev, [field]: value }));
  }, []);

  const resetForm = useCallback(() => {
    if (originalData) {
      setFormDataState({
        volunteerName: originalData.volunteerName,
        volunteerContact: originalData.volunteerContact,
        description: originalData.description,
        type: originalData.type,
        status: originalData.status,
      });
    }
  }, [originalData]);

  const save = useCallback(async () => {
    if (!originalData || !isDirty) return null;

    setIsSaving(true);
    setError(null);

    try {
      const updateDto: PledgeUpdateDto = {};
      
      if (formData.status !== originalData.status) updateDto.status = formData.status;
      if (formData.description !== originalData.description) updateDto.description = formData.description;
      if (formData.volunteerName !== originalData.volunteerName) updateDto.volunteerName = formData.volunteerName;
      if (formData.volunteerContact !== originalData.volunteerContact) updateDto.volunteerContact = formData.volunteerContact;
      if (formData.type !== originalData.type) updateDto.type = formData.type;
      
      const saved = await pledgeApi.update(originalData.id, updateDto);
      
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

export default usePledgeForm;