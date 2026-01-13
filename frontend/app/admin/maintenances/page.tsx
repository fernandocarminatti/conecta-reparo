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
import { MaintenanceTable } from '@/components/table';
import { maintenanceApi } from '@/lib/api/maintenance';
import { MaintenanceResponseDto, MaintenanceFilter, PageResponse } from '@/lib/types/maintenance';
import { Button } from '@/components/ui/button';

const statusOptions = [
  { value: '', label: 'Todos os Status' },
  { value: 'all', label: 'Todos' },
  { value: 'active', label: 'Ativos' },
  { value: 'inactive', label: 'Inativos' },
  { value: 'OPEN', label: 'Aberto' },
  { value: 'IN_PROGRESS', label: 'Em Andamento' },
  { value: 'COMPLETED', label: 'Concluído' },
  { value: 'CANCELED', label: 'Cancelado' },
];

const PAGE_SIZE = 25;

const categoryOptions = [
  { value: '', label: 'Todas as Categorias' },
  { value: 'BUILDING', label: '🏢 Construção' },
  { value: 'ELECTRICAL', label: '⚡ Elétrica' },
  { value: 'PLUMBING', label: '🔧 Hidráulica' },
  { value: 'HVAC', label: '❄️ HVAC' },
  { value: 'FURNITURE', label: '🪑 Mobília' },
  { value: 'GARDENING', label: '🌿 Jardinagem' },
  { value: 'SECURITY', label: '🔒 Segurança' },
  { value: 'OTHERS', label: '📦 Outros' },
];

export default function MaintenancesPage() {
  const [data, setData] = useState<MaintenanceResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  const [filter, setFilter] = useState<MaintenanceFilter>({
    status: '',
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
      const response = await maintenanceApi.getAll(filter);
      setData(response.content);
      setPagination({
        totalPages: response.totalPages,
        totalElements: response.totalElements,
        currentPage: response.number,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar manutenções');
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleFilterChange = (key: keyof MaintenanceFilter, value: string | number) => {
    setFilter(prev => ({ ...prev, [key]: value, page: 0 }));
  };

  const handleSort = (key: string, direction: 'asc' | 'desc') => {
    setSortKey(key);
    setSortDirection(direction);
    setFilter(prev => ({ ...prev, sort: `${key},${direction}` }));
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    fetchData();
  };

  const handleRefresh = () => {
    fetchData();
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Manutenções</h2>
          <p className="text-gray-500 mt-1">
            Gerencie as solicitações de manutenção ({pagination.totalElements} registros)
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

      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
          <form onSubmit={handleSearch} className="flex-1 max-w-md">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                type="text"
                placeholder="Buscar por título, descrição ou categoria..."
                value={filter.search}
                onChange={(e) => setFilter(prev => ({ ...prev, search: e.target.value }))}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900 placeholder-gray-400"
              />
            </div>
          </form>

          <div className="flex flex-wrap items-center gap-3">
            <div className="flex items-center gap-2">
              <Filter className="w-4 h-4 text-gray-400" />
              <select
                value={filter.status}
                onChange={(e) => handleFilterChange('status', e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-gray-900"
              >
                {statusOptions.map(opt => (
                  <option key={opt.value} value={opt.value}>{opt.label}</option>
                ))}
              </select>
            </div>

            <select
              value={filter.category || ''}
              onChange={(e) => handleFilterChange('category', e.target.value)}
              className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-gray-900"
            >
              {categoryOptions.map(opt => (
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

      <MaintenanceTable
        data={data}
        loading={loading}
        onSort={handleSort}
        sortKey={sortKey}
        sortDirection={sortDirection}
      />
    </div>
  );
}
