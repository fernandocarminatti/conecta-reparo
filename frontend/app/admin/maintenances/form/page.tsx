'use client';

import { useState, useEffect, useCallback } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { Loader2 } from 'lucide-react';
import { maintenanceApi } from '@/lib/api/maintenance';
import { MaintenanceForm } from '@/components/maintenance/MaintenanceForm';
import { MaintenanceResponseDto, MaintenanceUpdateDto } from '@/lib/types/maintenance';

export default function MaintenanceFormPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const id = searchParams.get('id');
  const mode = id ? 'edit' : 'create';

  const [maintenance, setMaintenance] = useState<MaintenanceResponseDto | null>(null);
  const [loading, setLoading] = useState(mode === 'edit');
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchMaintenance = useCallback(async () => {
    if (!id) {
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const data = await maintenanceApi.getDetail(id);
      setMaintenance(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar manutenção');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchMaintenance();
  }, [fetchMaintenance]);

  const handleSubmit = async (data: any) => {
    setIsSubmitting(true);
    setError(null);

    try {
      if (mode === 'create') {
        const createData = {
          ...data,
          scheduledDate: new Date(data.scheduledDate).toISOString(),
        };
        const created = await maintenanceApi.create(createData);
        router.push(`/admin/maintenances/${created.id}`);
      } else {
        const updateDto: MaintenanceUpdateDto = {};

        if (data.title !== maintenance?.title) updateDto.title = data.title;
        if (data.description !== maintenance?.description) updateDto.description = data.description;
        if (data.category !== maintenance?.category) updateDto.category = data.category;
        if (data.status !== maintenance?.status) updateDto.status = data.status;

        if (Object.keys(updateDto).length > 0) {
          await maintenanceApi.update(id!, updateDto);
        }

        router.push(`/admin/maintenances/${id}`);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao salvar');
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <Loader2 className="w-8 h-8 animate-spin text-primary" />
      </div>
    );
  }

  if (error && !maintenance && mode === 'edit') {
    return (
      <div className="bg-destructive/10 border border-destructive/20 rounded-lg p-6">
        <div className="flex items-center gap-3 text-destructive">
          <span className="font-medium">Erro ao carregar manutenção</span>
          <p className="text-sm mt-1">{error}</p>
        </div>
        <button
          onClick={fetchMaintenance}
          className="mt-4 px-4 py-2 bg-primary text-primary-foreground rounded-md"
        >
          Tentar novamente
        </button>
      </div>
    );
  }

  return (
    <MaintenanceForm
      initialData={maintenance}
      mode={mode}
      onSubmit={handleSubmit}
      isSubmitting={isSubmitting}
      error={error}
    />
  );
}
