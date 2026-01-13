'use client';

import { ChevronUp, ChevronDown, ChevronsUpDown } from 'lucide-react';

interface SortIconProps {
  sortKey: string;
  currentSortKey?: string;
  direction?: 'asc' | 'desc';
}

export function SortIcon({ sortKey, currentSortKey, direction }: SortIconProps) {
  if (sortKey !== currentSortKey) {
    return <ChevronsUpDown className="w-4 h-4 text-gray-400" />;
  }
  return direction === 'asc' ? (
    <ChevronUp className="w-4 h-4 text-blue-600" />
  ) : (
    <ChevronDown className="w-4 h-4 text-blue-600" />
  );
}
