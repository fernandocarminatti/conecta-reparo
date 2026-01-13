'use client';

import { Eye, Edit, ClipboardList } from 'lucide-react';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { MaintenanceResponseDto } from '@/lib/types/maintenance';
import { Badge } from '@/components/ui/badge';
import { MAINTENANCE_STATUS_CONFIG, CATEGORY_CONFIG } from '@/lib/config/status-config';
import { DataTable, Action, DataTablePagination } from './DataTable';

interface MaintenanceTableProps {
  data: MaintenanceResponseDto[];
  loading?: boolean;
  onSort?: (key: string, direction: 'asc' | 'desc') => void;
  sortKey?: string;
  sortDirection?: 'asc' | 'desc';
  pagination?: DataTablePagination;
}

export function MaintenanceTable({
  data,
  loading,
  onSort,
  sortKey,
  sortDirection,
  pagination,
}: MaintenanceTableProps) {
  const columns = [
    {
      key: 'title',
      header: 'Título',
      sortable: true,
      width: 'w-96',
      className: 'whitespace-nowrap',
      render: (row: MaintenanceResponseDto) => (
        <div className="truncate max-w-[380px]">
          <p className="text-sm font-bold text-gray-900">{row.title}</p>
          <p className="text-xs text-gray-500 truncate">{row.description}</p>
        </div>
      ),
    },
    {
      key: 'category',
      header: 'Categoria',
      sortable: true,
      width: 'w-72',
      className: 'whitespace-nowrap',
      render: (row: MaintenanceResponseDto) => {
        const config = CATEGORY_CONFIG[row.category];
        return (
          <Badge variant={config.variant} className="gap-1 whitespace-nowrap">
            <span>{config.icon}</span>
            {config.label}
          </Badge>
        );
      },
    },
    {
      key: 'status',
      header: 'Status',
      sortable: true,
      width: 'w-60',
      className: 'whitespace-nowrap',
      render: (row: MaintenanceResponseDto) => {
        const config = MAINTENANCE_STATUS_CONFIG[row.status];
        return <Badge variant={config.variant}>{config.label}</Badge>;
      },
    },
    {
      key: 'scheduledDate',
      header: 'Data Agendada',
      sortable: true,
      width: 'w-80',
      className: 'whitespace-nowrap',
      render: (row: MaintenanceResponseDto) => (
        <span className="text-gray-600">
          {format(new Date(row.scheduledDate), 'dd/MM/yyyy', { locale: ptBR })}
        </span>
      ),
    },
    {
      key: 'createdAt',
      header: 'Criado em',
      sortable: true,
      width: 'w-80',
      className: 'whitespace-nowrap',
      render: (row: MaintenanceResponseDto) => (
        <span className="text-gray-600">
          {format(new Date(row.createdAt), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
        </span>
      ),
    },
  ];

  const actions: Action[] = [
    {
      label: 'Ver Detalhes',
      icon: Eye,
      href: '/admin/maintenances/[id]',
    },
    {
      label: 'Editar',
      icon: Edit,
      href: '/admin/maintenances/[id]/edit',
    },
    {
      label: 'Ver Ações',
      icon: ClipboardList,
      href: '/admin/maintenances/[id]',
    },
  ];

  return (
    <DataTable
      data={data}
      columns={columns}
      actions={actions}
      loading={loading}
      emptyMessage="Nenhuma manutenção encontrada"
      onSort={onSort}
      sortKey={sortKey}
      sortDirection={sortDirection}
      getHrefReplacements={(row) => ({ '[id]': row.id })}
      pagination={pagination}
    />
  );
}

export default MaintenanceTable;
