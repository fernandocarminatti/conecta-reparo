'use client';

import { useState } from 'react';
import Link from 'next/link';
import { MoreHorizontal } from 'lucide-react';

interface Action {
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  href?: string;
  onClick?: () => void;
  variant?: 'default' | 'danger';
}

interface TableActionsProps<T> {
  row: T;
  actions: Action[];
  getReplacements: (row: T) => Record<string, string>;
}

export function TableActions<T>({ row, actions, getReplacements }: TableActionsProps<T>) {
  const [actionMenuOpen, setActionMenuOpen] = useState<string | null>(null);

  const resolveHref = (href: string) => {
    const replacements = getReplacements(row);
    let resolved = href;
    Object.entries(replacements).forEach(([key, value]) => {
      resolved = resolved.replace(key, value);
    });
    return resolved;
  };

  return (
    <div className="relative">
      <button
        onClick={() => setActionMenuOpen(actionMenuOpen === (row as { id: string }).id ? null : (row as { id: string }).id)}
        className="p-1 rounded hover:bg-muted text-muted-foreground hover:text-foreground"
      >
        <MoreHorizontal className="w-5 h-5" />
      </button>
      {actionMenuOpen === (row as { id: string }).id && (
        <div className="absolute right-0 mt-1 w-48 bg-popover rounded-lg shadow-lg border border-border py-1 z-10">
          {actions.map((action, index) => (
            <div key={index}>
              {action.href ? (
                <Link
                  href={resolveHref(action.href)}
                  className={`flex items-center gap-2 px-4 py-2 text-sm ${
                    action.variant === 'danger'
                      ? 'text-destructive hover:bg-destructive/10'
                      : 'hover:bg-muted'
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
                      ? 'text-destructive hover:bg-destructive/10'
                      : 'hover:bg-muted'
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
  );
}
