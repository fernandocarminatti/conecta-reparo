'use client';

import { useState, useEffect, useCallback } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { Loader2 } from 'lucide-react';
import { pledgeApi } from '@/lib/api/pledge';
import { maintenanceApi } from '@/lib/api/maintenance';
import { PledgeForm } from '@/components/pledge/PledgeForm';
import { PledgeResponseDto, PledgeUpdateDto } from '@/lib/types/pledges';
import { MaintenanceResponseDto } from '@/lib/types/maintenance';

export default function PledgeFormPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const id = searchParams.get('id');
  const mode = id ? 'edit' : 'create';

  const [pledge, setPledge] = useState<PledgeResponseDto | null>(null);
  const [maintenances, setMaintenances] = useState<MaintenanceResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const [maintenancesData, pledgeData] = await Promise.all([
        maintenanceApi.getAll({ status: 'active', size: 100 }),
        id ? pledgeApi.getById(id) : null,
      ]);

      setMaintenances(maintenancesData.content);
      setPledge(pledgeData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar dados');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleSubmit = async (data: any) => {
    setIsSubmitting(true);
    setError(null);

    try {
      if (mode === 'create') {
        const createData = {
          volunteerName: data.volunteerName,
          volunteerContact: data.volunteerContact,
          description: data.description,
          type: data.type,
          maintenanceId: data.maintenanceId,
        };
        const created = await pledgeApi.create(createData);
        router.push(`/admin/pledges/${created.id}`);
      } else {
        const updateDto: PledgeUpdateDto = {};

        if (data.volunteerName !== pledge?.volunteerName) updateDto.volunteerName = data.volunteerName;
        if (data.volunteerContact !== pledge?.volunteerContact) updateDto.volunteerContact = data.volunteerContact;
        if (data.description !== pledge?.description) updateDto.description = data.description;
        if (data.type !== pledge?.type) updateDto.type = data.type;
        if (data.status !== pledge?.status) updateDto.status = data.status;

        if (Object.keys(updateDto).length > 0) {
          await pledgeApi.update(id!, updateDto);
        }

        router.push(`/admin/pledges/${id}`);
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

  if (error && !pledge && mode === 'edit') {
    return (
      <div className="bg-destructive/10 border border-destructive/20 rounded-lg p-6">
        <div className="flex items-center gap-3 text-destructive">
          <span className="font-medium">Erro ao carregar oferta</span>
          <p className="text-sm mt-1">{error}</p>
        </div>
        <button
          onClick={fetchData}
          className="mt-4 px-4 py-2 bg-primary text-primary-foreground rounded-md"
        >
          Tentar novamente
        </button>
      </div>
    );
  }

  return (
    <PledgeForm
      initialData={pledge}
      mode={mode}
      maintenances={maintenances}
      onSubmit={handleSubmit}
      isSubmitting={isSubmitting}
      error={error}
    />
  );
}
