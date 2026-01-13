'use client';

import { useState } from 'react';
import {
  ChevronUp,
  ChevronDown,
  ChevronsUpDown,
  MoreHorizontal,
  Eye,
  Edit,
  Loader2
} from 'lucide-react';
import Link from 'next/link';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { MaintenanceActionResponseDto, ActionStatus } from '@/lib/types/maintenance';
import { Badge } from '@/components/ui/badge';
import { ACTION_STATUS_CONFIG } from '@/lib/config/status-config';
import { Card, CardContent } from '@/components/ui/card';

interface Column<T> {
  key: string;
  header: string;
  sortable?: boolean;
  render?: (row: T) => React.ReactNode;
  width?: string;
}

interface Action {
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  href?: string;
  onClick?: () => void;
  variant?: 'default' | 'danger';
}

interface MaintenanceActionTableProps {
  data: MaintenanceActionResponseDto[];
  loading?: boolean;
  emptyMessage?: string;
  onSort?: (key: string, direction: 'asc' | 'desc') => void;
  sortKey?: string;
  sortDirection?: 'asc' | 'desc';
}

function SortIcon({ sortKey, currentSortKey, direction }: { sortKey: string; currentSortKey?: string; direction?: 'asc' | 'desc' }) {
  if (sortKey !== currentSortKey) {
    return <ChevronsUpDown className="w-4 h-4 text-gray-400" />;
  }
  return direction === 'asc' ? (
    <ChevronUp className="w-4 h-4 text-blue-600" />
  ) : (
    <ChevronDown className="w-4 h-4 text-blue-600" />
  );
}

function LoadingSpinner() {
  return (
    <div className="flex items-center justify-center py-12">
      <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
    </div>
  );
}

export function MaintenanceActionTable({ 
  data, 
  loading = false,
  emptyMessage = 'Nenhum registro encontrado',
  onSort,
  sortKey,
  sortDirection 
}: MaintenanceActionTableProps) {
  const [actionMenuOpen, setActionMenuOpen] = useState<string | null>(null);

  const handleSort = (key: string) => {
    if (!onSort) return;
    
    if (sortKey === key) {
      onSort(key, sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      onSort(key, 'asc');
    }
  };

  const columns: Column<MaintenanceActionResponseDto>[] = [
    {
      key: 'maintenanceTitle',
      header: 'Manutenção',
      sortable: true,
      render: (row) => (
        <div>
          <p className="font-bold text-gray-900 truncate max-w-xs">{row.maintenanceTitle}</p>
          <p className="text-sm text-gray-500 truncate">{row.executedBy}</p>
        </div>
      ),
    },
    {
      key: 'actionDescription',
      header: 'Descrição',
      sortable: false,
      render: (row) => (
        <p className="text-sm text-gray-600 truncate max-w-xs">{row.actionDescription}</p>
      ),
    },
    {
      key: 'outcomeStatus',
      header: 'Status',
      sortable: true,
      render: (row) => {
        const config = ACTION_STATUS_CONFIG[row.outcomeStatus];
        return <Badge variant={config.variant}>{config.label}</Badge>;
      },
    },
    {
      key: 'startDate',
      header: 'Data Início',
      sortable: true,
      render: (row) => (
        <span className="text-sm text-gray-500">
          {format(new Date(row.startDate), 'dd/MM/yyyy', { locale: ptBR })}
        </span>
      ),
    },
    {
      key: 'createdAt',
      header: 'Criado em',
      sortable: true,
      render: (row) => (
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

  if (loading) {
    return <LoadingSpinner />;
  }

  if (data.length === 0) {
    return (
      <Card className="p-12">
        <div className="text-center">
          <p className="text-gray-500">{emptyMessage}</p>
        </div>
      </Card>
    );
  }

  return (
    <Card className="overflow-hidden">
      <CardContent className="p-0">
        <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="bg-gray-50 border-b border-gray-200">
              {columns.map((column) => (
                <th
                  key={column.key}
                  className={`px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider ${
                    column.sortable ? 'cursor-pointer hover:bg-gray-100' : ''
                  }`}
                  style={{ width: column.width }}
                  onClick={() => column.sortable && handleSort(column.key)}
                >
                  <div className="flex items-center gap-2">
                    {column.header}
                    {column.sortable && (
                      <SortIcon 
                        sortKey={column.key} 
                        currentSortKey={sortKey} 
                        direction={sortDirection} 
                      />
                    )}
                  </div>
                </th>
              ))}
              {actions.length > 0 && (
                <th className="px-6 py-3 text-right text-xs font-semibold text-gray-500 uppercase tracking-wider w-20">
                  Ações
                </th>
              )}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {data.map((row) => (
              <tr key={row.id} className="hover:bg-gray-50 transition-colors">
                {columns.map((column) => (
                  <td key={column.key} className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {column.render ? column.render(row) : (row as any)[column.key]}
                  </td>
                ))}
                {actions.length > 0 && (
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <div className="relative">
                      <button
                        onClick={() => setActionMenuOpen(actionMenuOpen === row.id ? null : row.id)}
                        className="p-1 rounded hover:bg-gray-100 text-gray-500 hover:text-gray-700"
                      >
                        <MoreHorizontal className="w-5 h-5" />
                      </button>
                      {actionMenuOpen === row.id && (
                        <div className="absolute right-0 mt-1 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-10">
                          {actions.map((action, index) => (
                            <div key={index}>
                              {action.href ? (
                                <Link
                                  href={action.href
                                    .replace('[actionId]', row.id)
                                    .replace('[id]', row.maintenanceId)}
                                  className={`flex items-center gap-2 px-4 py-2 text-sm ${
                                    action.variant === 'danger' 
                                      ? 'text-red-600 hover:bg-red-50' 
                                      : 'text-gray-700 hover:bg-gray-50'
                                  }`}
                                  onClick={() => setActionMenuOpen(null)}
                                >
                                  <action.icon className="w-4 h-4" />
                                  {action.label}
                                </Link>
                              ) : (
                                <button
                                  onClick={() => {
                                    action.onClick?.();
                                    setActionMenuOpen(null);
                                  }}
                                  className={`flex items-center gap-2 px-4 py-2 text-sm w-full text-left ${
                                    action.variant === 'danger' 
                                      ? 'text-red-600 hover:bg-red-50' 
                                      : 'text-gray-700 hover:bg-gray-50'
                                  }`}
                                >
                                  <action.icon className="w-4 h-4" />
                                  {action.label}
                                </button>
                              )}
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
        </div>
        </CardContent>
        </Card>
      );
    }

export default MaintenanceActionTable;