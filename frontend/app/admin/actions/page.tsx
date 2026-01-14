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
import { MaintenanceActionResponseDto } from '@/lib/types/maintenance';
import { Button } from '@/components/ui/button';
import { FilterBar } from '@/components/ui/filter-bar';

const statusOptions: { value: string; label: string }[] = [
  { value: '', label: 'Todos os Status' },
  { value: 'SUCCESS', label: 'Sucesso' },
  { value: 'PARTIAL_SUCCESS', label: 'Sucesso Parcial' },
  { value: 'FAILURE', label: 'Falha' },
];

const PAGE_SIZE = 25;

export default function ActionsPage() {
  const [allData, setAllData] = useState<MaintenanceActionResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState('');

  const [sortKey, setSortKey] = useState('createdAt');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');

  const [currentPage, setCurrentPage] = useState(0);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const actions = await maintenanceActionApi.getAll();
      setAllData(actions);
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
    ? allData.filter(action => action.outcomeStatus === statusFilter)
    : allData;

  const sortedData = [...filteredData].sort((a, b) => {
    let aVal: string | number = a[sortKey as keyof MaintenanceActionResponseDto] as string | number;
    let bVal: string | number = b[sortKey as keyof MaintenanceActionResponseDto] as string | number;

    if (sortKey === 'createdAt' || sortKey === 'startDate') {
      aVal = new Date(aVal as string).getTime();
      bVal = new Date(bVal as string).getTime();
    }

    if (sortDirection === 'asc') {
      return aVal > bVal ? 1 : -1;
    }
    return aVal < bVal ? 1 : -1;
  });

  const paginatedData = sortedData.slice(
    currentPage * PAGE_SIZE,
    (currentPage + 1) * PAGE_SIZE
  );

  const totalPages = Math.ceil(sortedData.length / PAGE_SIZE);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handleRefresh = () => {
    fetchData();
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-foreground">Ações de Manutenção</h2>
          <p className="text-muted-foreground mt-1">
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
            <Link href="/admin/maintenances/form">
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
        onFilterChange={(key, value) => {
          setStatusFilter(value);
          setCurrentPage(0);
        }}
      />

      {error && (
        <div className="bg-destructive/10 border border-destructive/20 rounded-lg p-4">
          <p className="text-destructive text-sm">{error}</p>
        </div>
      )}

      <MaintenanceActionTable
        data={paginatedData}
        loading={loading}
        onSort={handleSort}
        sortKey={sortKey}
        sortDirection={sortDirection}
        pagination={{
          currentPage,
          totalPages,
          totalElements: sortedData.length,
          pageSize: PAGE_SIZE,
          onPageChange: handlePageChange,
        }}
      />
    </div>
  );
}
