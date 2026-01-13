'use client';

import { Card } from '@/components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { SortIcon } from './SortIcon';
import { LoadingSpinner } from './LoadingSpinner';
import { TableActions } from './TableActions';
import { Pagination } from '@/components/ui/pagination';

export interface Column<T> {
  key: string;
  header: string;
  sortable?: boolean;
  render?: (row: T) => React.ReactNode;
  width?: string;
  className?: string;
}

export interface Action {
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  href?: string;
  onClick?: () => void;
  variant?: 'default' | 'danger';
}

export interface DataTablePagination {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  onPageChange: (page: number) => void;
}

export interface DataTableProps<T extends { id: string }> {
  data: T[];
  columns: Column<T>[];
  actions?: Action[];
  loading?: boolean;
  emptyMessage?: string;
  onSort?: (key: string, direction: 'asc' | 'desc') => void;
  sortKey?: string;
  sortDirection?: 'asc' | 'desc';
  getHrefReplacements?: (row: T) => Record<string, string>;
  pagination?: DataTablePagination;
}

export function DataTable<T extends { id: string }>({
  data,
  columns,
  actions = [],
  loading = false,
  emptyMessage = 'Nenhum registro encontrado',
  onSort,
  sortKey,
  sortDirection,
  getHrefReplacements = () => ({}),
  pagination,
}: DataTableProps<T>) {
  const handleSort = (key: string) => {
    if (!onSort) return;

    if (sortKey === key) {
      onSort(key, sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      onSort(key, 'asc');
    }
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (data.length === 0) {
    return (
      <Card className="p-12">
        <div className="text-center">
          <p className="text-muted-foreground">{emptyMessage}</p>
        </div>
      </Card>
    );
  }

  return (
    <Card className="overflow-hidden">
      <div className="overflow-x-auto">
        <Table>
          <TableHeader>
            <TableRow>
              {columns.map((column) => (
                <TableHead
                  key={column.key}
                  className={`${
                    column.sortable ? 'cursor-pointer hover:bg-muted' : ''
                  } ${column.className || ''} ${column.width || ''}`}
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
                </TableHead>
              ))}
              {actions.length > 0 && (
                <TableHead className="w-20 text-right">Ações</TableHead>
              )}
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.map((row) => (
              <TableRow key={row.id}>
                {columns.map((column) => (
                  <TableCell
                    key={column.key}
                    className={`text-sm text-foreground ${column.className || ''} ${column.width || ''}`}
                  >
                    {column.render
                      ? column.render(row)
                      : (row as Record<string, unknown>)[column.key]?.toString()}
                  </TableCell>
                ))}
                {actions.length > 0 && (
                  <TableCell className="text-right">
                    <TableActions
                      row={row}
                      actions={actions}
                      getReplacements={getHrefReplacements}
                    />
                  </TableCell>
                )}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
      {pagination && (
        <div className="border-t px-4">
          <Pagination
            currentPage={pagination.currentPage}
            totalPages={pagination.totalPages}
            totalElements={pagination.totalElements}
            pageSize={pagination.pageSize}
            onPageChange={pagination.onPageChange}
          />
        </div>
      )}
    </Card>
  );
}
