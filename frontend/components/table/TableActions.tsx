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
        className="p-1 rounded hover:bg-gray-100 text-gray-500 hover:text-gray-700"
      >
        <MoreHorizontal className="w-5 h-5" />
      </button>
      {actionMenuOpen === (row as { id: string }).id && (
        <div className="absolute right-0 mt-1 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-10">
          {actions.map((action, index) => (
            <div key={index}>
              {action.href ? (
                <Link
                  href={resolveHref(action.href)}
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
  );
}
