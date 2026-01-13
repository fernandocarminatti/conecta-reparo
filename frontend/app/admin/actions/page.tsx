'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { 
  Plus, 
  Download,
  RefreshCw
} from 'lucide-react';
import { MaintenanceActionTable } from '@/components/table';
import { maintenanceActionApi } from '@/lib/api/maintenance-action';
import { MaintenanceActionResponseDto, ActionStatus } from '@/lib/types/maintenance';
import { Button } from '@/components/ui/button';
import { FilterBar } from '@/components/ui/filter-bar';

const statusOptions: { value: string; label: string }[] = [
  { value: '', label: 'Todos os Status' },
  { value: 'SUCCESS', label: 'Sucesso' },
  { value: 'PARTIAL_SUCCESS', label: 'Sucesso Parcial' },
  { value: 'FAILURE', label: 'Falha' },
];

export default function ActionsPage() {
  const [data, setData] = useState<MaintenanceActionResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState('');
  
  const [sortKey, setSortKey] = useState('createdAt');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const actions = await maintenanceActionApi.getAll();
      setData(actions);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar ações');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleSort = (key: string, direction: 'asc' | 'desc') => {
    setSortKey(key);
    setSortDirection(direction);
  };

  const filteredData = statusFilter
    ? data.filter(action => action.outcomeStatus === statusFilter)
    : data;

  const sortedData = [...filteredData].sort((a, b) => {
    let aVal: any = a[sortKey as keyof MaintenanceActionResponseDto];
    let bVal: any = b[sortKey as keyof MaintenanceActionResponseDto];
    
    if (sortKey === 'createdAt' || sortKey === 'startDate') {
      aVal = new Date(aVal).getTime();
      bVal = new Date(bVal).getTime();
    }
    
    if (sortDirection === 'asc') {
      return aVal > bVal ? 1 : -1;
    }
    return aVal < bVal ? 1 : -1;
  });

  const handleRefresh = () => {
    fetchData();
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Ações de Manutenção</h2>
          <p className="text-gray-500 mt-1">
            Visualize todas as ações de manutenção ({sortedData.length} registros)
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Button variant="outline" onClick={handleRefresh} disabled={loading}>
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </Button>
          <Button variant="outline">
            <Download className="w-4 h-4" />
            Exportar
          </Button>
          <Button asChild>
            <Link href="/admin/maintenances/new">
              <Plus className="w-4 h-4" />
              Nova Manutenção
            </Link>
          </Button>
        </div>
      </div>

      <FilterBar
        filters={[
          {
            key: 'status',
            label: 'Status',
            options: statusOptions,
            value: statusFilter,
          },
        ]}
        onFilterChange={(key, value) => setStatusFilter(value)}
      />

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-600 text-sm">{error}</p>
        </div>
      )}

      <MaintenanceActionTable
        data={sortedData}
        loading={loading}
        onSort={handleSort}
        sortKey={sortKey}
        sortDirection={sortDirection}
      />
    </div>
  );
}
