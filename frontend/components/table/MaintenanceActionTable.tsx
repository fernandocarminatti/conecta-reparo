'use client';

import { Eye, Edit } from 'lucide-react';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { MaintenanceActionResponseDto } from '@/lib/types/maintenance';
import { Badge } from '@/components/ui/badge';
import { ACTION_STATUS_CONFIG } from '@/lib/config/status-config';
import { DataTable, Action, DataTablePagination } from './DataTable';

interface MaintenanceActionTableProps {
  data: MaintenanceActionResponseDto[];
  loading?: boolean;
  onSort?: (key: string, direction: 'asc' | 'desc') => void;
  sortKey?: string;
  sortDirection?: 'asc' | 'desc';
  pagination?: DataTablePagination;
}

export function MaintenanceActionTable({
  data,
  loading,
  onSort,
  sortKey,
  sortDirection,
  pagination,
}: MaintenanceActionTableProps) {
  const columns = [
    {
      key: 'maintenanceTitle',
      header: 'Manutenção',
      sortable: true,
      className: 'whitespace-nowrap',
      render: (row: MaintenanceActionResponseDto) => (
        <div className="truncate max-w-[380px]">
          <p className="text-sm font-bold text-gray-900 truncate">{row.maintenanceTitle}</p>
          <p className="text-xs text-gray-500 truncate">{row.executedBy}</p>
        </div>
      ),
    },
    {
      key: 'actionDescription',
      header: 'Descrição',
      sortable: false,
      render: (row: MaintenanceActionResponseDto) => (
        <p className="text-sm text-gray-600 truncate max-w-[300px]">{row.actionDescription}</p>
      ),
    },
    {
      key: 'outcomeStatus',
      header: 'Status',
      sortable: true,
      width: 'w-32',
      className: 'whitespace-nowrap',
      render: (row: MaintenanceActionResponseDto) => {
        const config = ACTION_STATUS_CONFIG[row.outcomeStatus];
        return <Badge variant={config.variant}>{config.label}</Badge>;
      },
    },
    {
      key: 'startDate',
      header: 'Data Início',
      sortable: true,
      width: 'w-28',
      className: 'whitespace-nowrap',
      render: (row: MaintenanceActionResponseDto) => (
        <span className="text-sm text-gray-500">
          {format(new Date(row.startDate), 'dd/MM/yyyy', { locale: ptBR })}
        </span>
      ),
    },
    {
      key: 'createdAt',
      header: 'Criado em',
      sortable: true,
      width: 'w-36',
      className: 'whitespace-nowrap',
      render: (row: MaintenanceActionResponseDto) => (
        <span className="text-sm text-gray-500">
          {format(new Date(row.createdAt), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
        </span>
      ),
    },
  ];

  const actions: Action[] = [
    {
      label: 'Ver Detalhes',
      icon: Eye,
      href: '/admin/maintenances/[id]/actions/[actionId]',
    },
    {
      label: 'Editar',
      icon: Edit,
      href: '/admin/maintenances/[id]/actions/[actionId]/edit',
    },
  ];

  return (
    <DataTable
      data={data}
      columns={columns}
      actions={actions}
      loading={loading}
      emptyMessage="Nenhuma ação encontrada"
      onSort={onSort}
      sortKey={sortKey}
      sortDirection={sortDirection}
      getHrefReplacements={(row) => ({ '[id]': row.maintenanceId, '[actionId]': row.id })}
      pagination={pagination}
    />
  );
}

export default MaintenanceActionTable;
