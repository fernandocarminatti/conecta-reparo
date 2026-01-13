'use client';

import { ChevronUp, ChevronDown, ChevronsUpDown } from 'lucide-react';

interface SortIconProps {
  sortKey: string;
  currentSortKey?: string;
  direction?: 'asc' | 'desc';
}

export function SortIcon({ sortKey, currentSortKey, direction }: SortIconProps) {
  if (sortKey !== currentSortKey) {
    return <ChevronsUpDown className="w-4 h-4 text-muted-foreground" />;
  }
  return direction === 'asc' ? (
    <ChevronUp className="w-4 h-4 text-primary" />
  ) : (
    <ChevronDown className="w-4 h-4 text-primary" />
  );
}
