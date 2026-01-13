'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { 
  Search, 
  Filter, 
  Plus, 
  Download,
  RefreshCw
} from 'lucide-react';
import { PledgeTable } from '@/components/table';
import { pledgeApi } from '@/lib/api/pledge';
import { PledgeResponseDto, PledgeFilter, PledgeStatus, PledgeCategory } from '@/lib/types/maintenance';
import { Button } from '@/components/ui/button';

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

  const handleFilterChange = (key: keyof PledgeFilter, value: string | number) => {
    setFilter(prev => ({ ...prev, [key]: value, page: 0 }));
  };

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

      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por voluntário, contato ou descrição..."
              value={filter.search}
              onChange={(e) => setFilter(prev => ({ ...prev, search: e.target.value }))}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-400"
            />
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <div className="flex items-center gap-2">
              <Filter className="w-4 h-4 text-gray-400" />
              <select
                value={filter.status}
                onChange={(e) => handleFilterChange('status', e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-xs text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-gray-900"
              >
                {statusOptions.map(opt => (
                  <option key={opt.value} value={opt.value}>{opt.label}</option>
                ))}
              </select>
            </div>

            <select
              value={filter.type || ''}
              onChange={(e) => handleFilterChange('type', e.target.value)}
              className="px-3 py-2 border border-gray-300 rounded-xs text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-gray-900"
            >
              {typeOptions.map(opt => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

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
