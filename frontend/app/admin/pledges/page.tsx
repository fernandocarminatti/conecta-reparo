'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { 
  Search, 
  Filter, 
  Plus, 
  Download,
  RefreshCw,
  ChevronLeft,
  ChevronRight,
  Heart
} from 'lucide-react';
import { PledgeTable } from '@/components/table';
import { pledgeApi } from '@/lib/api/pledge';
import { PledgeResponseDto, PledgeFilter, PledgeStatus, PledgeCategory } from '@/lib/types/maintenance';

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

  const handlePageChange = (newPage: number) => {
    setFilter(prev => ({ ...prev, page: newPage }));
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
          <button
            onClick={handleRefresh}
            className="inline-flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
            title="Atualizar lista"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
          <button className="inline-flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors">
            <Download className="w-4 h-4" />
            Exportar
          </button>
          <Link
            href="/admin/pledges/new"
            className="inline-flex items-center gap-2 px-4 py-2 bg-pink-600 text-white rounded-lg hover:bg-pink-700 transition-colors"
          >
            <Plus className="w-4 h-4" />
            Nova Oferta
          </Link>
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

      {pagination.totalPages > 1 && (
        <div className="flex items-center justify-between bg-white rounded-lg border border-gray-200 px-4 py-3">
          <div className="text-sm text-gray-500">
            Mostrando {pagination.currentPage * PAGE_SIZE + 1} a{' '}
            {Math.min((pagination.currentPage + 1) * PAGE_SIZE, pagination.totalElements)} de{' '}
            {pagination.totalElements} resultados
          </div>
          
          <div className="flex items-center gap-2">
            <button
              onClick={() => handlePageChange(pagination.currentPage - 1)}
              disabled={pagination.currentPage === 0}
              className="p-2 rounded-lg border border-gray-300 text-gray-600 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            
            <div className="flex items-center gap-1">
              {Array.from({ length: Math.min(5, pagination.totalPages) }, (_, i) => {
                let pageNum: number;
                if (pagination.totalPages <= 5) {
                  pageNum = i;
                } else if (pagination.currentPage < 3) {
                  pageNum = i;
                } else if (pagination.currentPage > pagination.totalPages - 3) {
                  pageNum = pagination.totalPages - 5 + i;
                } else {
                  pageNum = pagination.currentPage - 2 + i;
                }
                
                return (
                  <button
                    key={pageNum}
                    onClick={() => handlePageChange(pageNum)}
                    className={`w-8 h-8 rounded-lg text-sm font-medium transition-colors ${
                      pagination.currentPage === pageNum
                        ? 'bg-blue-600 text-white'
                        : 'text-gray-600 hover:bg-gray-100'
                    }`}
                  >
                    {pageNum + 1}
                  </button>
                );
              })}
            </div>
            
            <button
              onClick={() => handlePageChange(pagination.currentPage + 1)}
              disabled={pagination.currentPage >= pagination.totalPages - 1}
              className="p-2 rounded-lg border border-gray-300 text-gray-600 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}