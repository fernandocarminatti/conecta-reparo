'use client';

import { Search } from 'lucide-react';
import { Input } from './input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './select';
import { cn } from '@/lib/utils';
import { StatusSelectOptions, CategorySelectOptions } from '@/lib/types/maintenance';

export interface FilterOption {
  key: string;
  label: string;
  options: StatusSelectOptions[] | CategorySelectOptions[];
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
        'bg-card rounded-lg border border-border p-4',
        className
      )}
    >
      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
        {onSearchChange && (
          <div className="relative flex-1 max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
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
          <div className="flex flex-wrap items-center gap-3 relative z-10">
            {filters.map((filter) => (
              <div key={filter.key} className="min-w-[140px]">
                <Select defaultValue={filter.value || undefined}
                  onValueChange={(value) => onFilterChange?.(filter.key, value)}
                >
                  <SelectTrigger className="w-full">
                    <SelectValue placeholder={filter.label} />
                  </SelectTrigger>
                  <SelectContent position="popper" align="start" className="w-[var(--radix-select-trigger-width)]">
                    {filter.options.filter((opt) => opt.value !== '').map((opt) => (
                      <SelectItem className="min-w-[140px]" key={opt.value} value={opt.value}>
                        {opt.icon}
                        <span>{opt.label}</span>
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
