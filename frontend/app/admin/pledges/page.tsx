'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { 
  Plus, 
  Download,
  RefreshCw
} from 'lucide-react';
import { PledgeTable } from '@/components/table';
import { pledgeApi } from '@/lib/api/pledge';
import { PledgeResponseDto, PledgeFilter, PledgeStatus, PledgeCategory } from '@/lib/types/maintenance';
import { Button } from '@/components/ui/button';
import { FilterBar } from '@/components/ui/filter-bar';

const statusOptions: { value: string; label: string }[] = [
  { value: '', label: 'Todos os Status' },
  { value: 'OFFERED', label: 'Oferecido' },
  { value: 'PENDING', label: 'Pendente' },
  { value: 'REJECTED', label: 'Rejeitado' },
  { value: 'COMPLETED', label: 'Concluído' },
  { value: 'CANCELED', label: 'Cancelado' },
];

const typeOptions: { value: string; label: string }[] = [
  { value: '', label: 'Todos os Tipos' },
  { value: 'MATERIAL', label: 'Material' },
  { value: 'LABOR', label: 'Mão de Obra' },
];

const PAGE_SIZE = 25;

export default function PledgesPage() {
  const [data, setData] = useState<PledgeResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  const [filter, setFilter] = useState<PledgeFilter>({
    status: '',
    type: '',
    search: '',
    page: 0,
    size: PAGE_SIZE,
    sort: 'createdAt,desc',
  });
  
  const [pagination, setPagination] = useState({
    totalPages: 0,
    totalElements: 0,
    currentPage: 0,
  });

  const [sortKey, setSortKey] = useState('createdAt');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await pledgeApi.getAll(filter);
      setData(response.content);
      setPagination({
        totalPages: response.totalPages,
        totalElements: response.totalElements,
        currentPage: response.number,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar pledges');
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleSort = (key: string, direction: 'asc' | 'desc') => {
    setSortKey(key);
    setSortDirection(direction);
    setFilter(prev => ({ ...prev, sort: `${key},${direction}` }));
  };

  const handleRefresh = () => {
    fetchData();
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Pledges</h2>
          <p className="text-gray-500 mt-1">
            Gerencie as ofertas de voluntários ({pagination.totalElements} registros)
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
            <Link href="/admin/pledges/new">
              <Plus className="w-4 h-4" />
              Nova Oferta
            </Link>
          </Button>
        </div>
      </div>

      <FilterBar
        searchPlaceholder="Buscar por voluntário, contato ou descrição..."
        searchValue={filter.search}
        onSearchChange={(value) => setFilter(prev => ({ ...prev, search: value, page: 0 }))}
        filters={[
          {
            key: 'status',
            label: 'Status',
            options: statusOptions,
            value: filter.status,
          },
          {
            key: 'type',
            label: 'Tipo',
            options: typeOptions,
            value: filter.type || '',
          },
        ]}
        onFilterChange={(key, value) => setFilter(prev => ({ ...prev, [key]: value, page: 0 }))}
      />

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-600 text-sm">{error}</p>
        </div>
      )}

      <PledgeTable
        data={data}
        loading={loading}
        onSort={handleSort}
        sortKey={sortKey}
        sortDirection={sortDirection}
      />
    </div>
  );
}
