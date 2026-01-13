'use client';

import { Search } from 'lucide-react';
import { Input } from './input';
import { Select } from './select';
import { cn } from '@/lib/utils';

export interface FilterOption {
  key: string;
  label: string;
  options: { value: string; label: string }[];
  value?: string;
}

export interface FilterBarProps {
  searchPlaceholder?: string;
  searchValue?: string;
  onSearchChange?: (value: string) => void;
  filters?: FilterOption[];
  onFilterChange?: (key: string, value: string) => void;
  className?: string;
}

export function FilterBar({
  searchPlaceholder,
  searchValue = '',
  onSearchChange,
  filters = [],
  onFilterChange,
  className,
}: FilterBarProps) {
  return (
    <div
      className={cn(
        'bg-white rounded-lg border border-gray-200 p-4',
        className
      )}
    >
      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
        {onSearchChange && (
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <Input
              type="text"
              placeholder={searchPlaceholder}
              value={searchValue}
              onChange={(e) => onSearchChange(e.target.value)}
              className="pl-10"
            />
          </div>
        )}

        {filters.length > 0 && (
          <div className="flex flex-wrap items-center gap-3">
            {filters.map((filter) => (
              <div key={filter.key} className="min-w-[140px]">
                <Select
                  value={filter.value ?? ''}
                  onChange={(e) => onFilterChange?.(filter.key, e.target.value)}
                  className="w-full"
                >
                  {filter.options.map((opt) => (
                    <option key={opt.value} value={opt.value}>
                      {opt.label}
                    </option>
                  ))}
                </Select>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
