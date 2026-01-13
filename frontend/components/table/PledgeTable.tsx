'use client';

import { Eye, Edit } from 'lucide-react';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { PledgeResponseDto } from '@/lib/types/maintenance';
import { Badge } from '@/components/ui/badge';
import { PLEDGE_STATUS_CONFIG, CATEGORY_CONFIG } from '@/lib/config/status-config';
import { DataTable, Action, DataTablePagination } from './DataTable';

interface PledgeTableProps {
  data: PledgeResponseDto[];
  loading?: boolean;
  onSort?: (key: string, direction: 'asc' | 'desc') => void;
  sortKey?: string;
  sortDirection?: 'asc' | 'desc';
  pagination?: DataTablePagination;
}

export function PledgeTable({
  data,
  loading,
  onSort,
  sortKey,
  sortDirection,
  pagination,
}: PledgeTableProps) {
  const columns = [
    {
      key: 'volunteerName',
      header: 'Voluntário',
      sortable: true,
      className: 'whitespace-nowrap',
      render: (row: PledgeResponseDto) => (
        <div className="truncate max-w-[200px]">
          <p className="text-sm font-bold text-gray-900">{row.volunteerName}</p>
          <p className="text-xs text-gray-500 truncate">{row.volunteerContact}</p>
        </div>
      ),
    },
    {
      key: 'description',
      header: 'Descrição',
      sortable: false,
      render: (row: PledgeResponseDto) => (
        <p className="text-sm text-gray-600 truncate max-w-[300px]">{row.description}</p>
      ),
    },
    {
      key: 'type',
      header: 'Tipo',
      sortable: true,
      width: 'w-28',
      className: 'whitespace-nowrap',
      render: (row: PledgeResponseDto) => {
        const config = CATEGORY_CONFIG[row.type];
        return <Badge variant={config.variant}>{config.label}</Badge>;
      },
    },
    {
      key: 'status',
      header: 'Status',
      sortable: true,
      width: 'w-32',
      className: 'whitespace-nowrap',
      render: (row: PledgeResponseDto) => {
        const config = PLEDGE_STATUS_CONFIG[row.status];
        return <Badge variant={config.variant}>{config.label}</Badge>;
      },
    },
    {
      key: 'createdAt',
      header: 'Criado em',
      sortable: true,
      width: 'w-36',
      className: 'whitespace-nowrap',
      render: (row: PledgeResponseDto) => (
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
      href: '/admin/pledges/[id]',
    },
    {
      label: 'Editar',
      icon: Edit,
      href: '/admin/pledges/[id]/edit',
    },
  ];

  return (
    <DataTable
      data={data}
      columns={columns}
      actions={actions}
      loading={loading}
      emptyMessage="Nenhum pledge encontrado"
      onSort={onSort}
      sortKey={sortKey}
      sortDirection={sortDirection}
      getHrefReplacements={(row) => ({ '[id]': row.id })}
      pagination={pagination}
    />
  );
}

export default PledgeTable;
