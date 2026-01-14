'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { 
  Plus, 
  Download,
  RefreshCw,
  Building2,
  Wrench,
  List,
  Zap,
  Snowflake,
  Armchair,
  Trees,
  Lock,
  Package,
  CircleX,
  CircleCheck,
  CircleDotDashed,
  Circle,
  CircleMinus,
  CircleFadingPlus,
  CirclePile
} from 'lucide-react';
import { MaintenanceTable } from '@/components/table';
import { maintenanceApi } from '@/lib/api/maintenance';
import { MaintenanceResponseDto, MaintenanceFilter, StatusSelectOptions, CategorySelectOptions } from '@/lib/types/maintenance';
import { Button } from '@/components/ui/button';
import { FilterBar } from '@/components/ui/filter-bar';

const statusOptions: StatusSelectOptions[] = [
  { value: 'all', label: 'Todos', icon: <CirclePile className="w-4 h-4" /> },
  { value: 'active', label: 'Ativos', icon: <CircleFadingPlus className="w-4 h-4" />},
  { value: 'inactive', label: 'Inativos', icon: <CircleMinus className="w-4 h-4" /> },
  { value: 'OPEN', label: 'Aberto', icon: <Circle className="w-4 h-4" /> },
  { value: 'IN_PROGRESS', label: 'Em Andamento', icon: <CircleDotDashed className="w-4 h-4"/> },
  { value: 'COMPLETED', label: 'Concluído', icon: <CircleCheck className="w-4 h-4" /> },
  { value: 'CANCELED', label: 'Cancelado', icon: <CircleX className="w-4 h-4" /> },
];

const PAGE_SIZE = 25;

const categoryOptions : CategorySelectOptions[] = [
  { value: 'all', label: 'Todas', icon: <List className="w-4 h-4" /> },
  { value: 'BUILDING', label: 'Construção', icon: <Building2 className="w-4 h-4" /> },
  { value: 'ELECTRICAL', label: 'Elétrica', icon: <Zap className="w-4 h-4" /> },
  { value: 'PLUMBING', label: 'Hidráulica', icon: <Wrench className="w-4 h-4" /> },
  { value: 'HVAC', label: 'HVAC', icon: <Snowflake className="w-4 h-4" /> },
  { value: 'FURNITURE', label: 'Mobília', icon: <Armchair className="w-4 h-4" /> },
  { value: 'GARDENING', label: 'Jardinagem', icon: <Trees className="w-4 h-4" /> },
  { value: 'SECURITY', label: 'Segurança', icon: <Lock className="w-4 h-4" /> },
  { value: 'OTHERS', label: 'Outros', icon: <Package className="w-4 h-4" /> },
];

export default function MaintenancesPage() {
  const [data, setData] = useState<MaintenanceResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  const [filter, setFilter] = useState<MaintenanceFilter>({
    status: '',
    search: '',
    category: '',
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

  const handleSort = (key: string, direction: 'asc' | 'desc') => {
    setSortKey(key);
    setSortDirection(direction);
    setFilter(prev => ({ ...prev, sort: `${key},${direction}` }));
  };

  const handlePageChange = (page: number) => {
    setFilter(prev => ({ ...prev, page }));
  };

  const handleRefresh = () => {
    fetchData();
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-foreground">Manutenções</h2>
          <p className="text-muted-foreground mt-1">
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

      <FilterBar
        searchPlaceholder="Buscar por título, descrição ou categoria..."
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
            key: 'category',
            label: 'Categoria',
            options: categoryOptions,
            value: filter.category,
          },
        ]}
        onFilterChange={(key, value) => setFilter(prev =>
          ({ ...prev,
            [key]: value === 'all' ? '' : value,
            page: 0 
          }))}
      />

      {error && (
        <div className="bg-destructive/10 border border-destructive/20 rounded-lg p-4">
          <p className="text-destructive text-sm">{error}</p>
        </div>
      )}

      <MaintenanceTable
        data={data}
        loading={loading}
        onSort={handleSort}
        sortKey={sortKey}
        sortDirection={sortDirection}
        pagination={{
          currentPage: pagination.currentPage,
          totalPages: pagination.totalPages,
          totalElements: pagination.totalElements,
          pageSize: PAGE_SIZE,
          onPageChange: handlePageChange,
        }}
      />
    </div>
  );
}
