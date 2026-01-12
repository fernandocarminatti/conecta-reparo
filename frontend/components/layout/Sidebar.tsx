'use client';

import { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  Wrench,
  Heart,
  ClipboardList,
  Menu,
  X,
  ChevronRight,
  LayoutDashboard,
  Settings,
  Users
} from 'lucide-react';

interface NavItem {
  name: string;
  href: string;
  icon: React.ComponentType<{ className?: string }>;
  children?: { name: string; href: string }[];
}

const navigation: NavItem[] = [
  {
    name: 'Dashboard',
    href: '/admin',
    icon: LayoutDashboard,
  },
  {
    name: 'Manutenções',
    href: '/admin/maintenances',
    icon: Wrench,
    children: [
      { name: 'Listar Todas', href: '/admin/maintenances' },
      { name: 'Criar Nova', href: '/admin/maintenances/new' },
      { name: 'Historico', href: '/admin/history' },
    ],
  },
  {
    name: 'Pledges',
    href: '/admin/pledges',
    icon: Heart,
    children: [
      { name: 'Todos os Pledges', href: '/admin/pledges' },
      { name: 'Pendentes', href: '/admin/pledges/pending' },
      { name: 'Aprovados', href: '/admin/pledges/approved' },
    ],
  },
  {
    name: 'Ações',
    href: '/admin/actions',
    icon: ClipboardList,
    children: [
      { name: 'Todas as Ações', href: '/admin/actions' },
      { name: 'Planejadas', href: '/admin/actions/planned' },
      { name: 'Em Andamento', href: '/admin/actions/in-progress' },
      { name: 'Concluídas', href: '/admin/actions/completed' },
    ],
  },
  {
    name: 'Configurações',
    href: '/admin/settings',
    icon: Settings,
  },
];

function NavGroup({ item, isCollapsed }: { item: NavItem; isCollapsed: boolean }) {
  const pathname = usePathname();
  const [isOpen, setIsOpen] = useState(false);
  const isActive = pathname.startsWith(item.href);

  if (isCollapsed) {
    return (
      <div className="relative group">
        <Link
          href={item.href}
          className={`flex items-center justify-center p-3 rounded-lg transition-colors ${
            isActive
              ? 'bg-blue-50 text-blue-600'
              : 'text-gray-600 hover:bg-gray-100'
          }`}
          title={item.name}
        >
          <item.icon className="w-5 h-5" />
        </Link>
        {item.children && (
          <div className="absolute left-full top-0 ml-2 w-48 bg-white rounded-lg shadow-lg border hidden group-hover:block z-50">
            {item.children.map((child) => (
              <Link
                key={child.name}
                href={child.href}
                className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 first:rounded-t-lg last:rounded-b-lg"
              >
                {child.name}
              </Link>
            ))}
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="mb-1">
      <button
        onClick={() => setIsOpen(!isOpen)}
        className={`w-full flex items-center justify-between p-3 rounded-lg transition-colors ${
          isActive
            ? 'bg-blue-50 text-blue-600'
            : 'text-gray-600 hover:bg-gray-100'
        }`}
      >
        <div className="flex items-center gap-3">
          <item.icon className="w-5 h-5" />
          <span className="font-medium">{item.name}</span>
        </div>
        {item.children && (
          <ChevronRight
            className={`w-4 h-4 transition-transform ${isOpen ? 'rotate-90' : ''}`}
          />
        )}
      </button>
      {item.children && isOpen && (
        <div className="ml-8 mt-1 space-y-1">
          {item.children.map((child) => {
            const isChildActive = pathname === child.href;
            return (
              <Link
                key={child.name}
                href={child.href}
                className={`block px-3 py-2 rounded-md text-sm transition-colors ${
                  isChildActive
                    ? 'bg-blue-50 text-blue-600 font-medium'
                    : 'text-gray-600 hover:bg-gray-100'
                }`}
              >
                {child.name}
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}

export function Sidebar() {
  const [isCollapsed, setIsCollapsed] = useState(false);

  return (
    <aside
      className={`fixed left-0 top-0 h-full bg-white border-r border-gray-200 transition-all duration-300 z-40 ${
        isCollapsed ? 'w-16' : 'w-64'
      }`}
    >
      <div className="flex flex-col h-full">
        <div className={`flex items-center justify-between p-4 border-b border-gray-200 ${isCollapsed ? 'justify-center' : ''}`}>
          {!isCollapsed && (
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
                <Wrench className="w-5 h-5 text-white" />
              </div>
              <span className="font-bold text-gray-900">Conecta Reparo</span>
            </div>
          )}
          <button
            onClick={() => setIsCollapsed(!isCollapsed)}
            className="p-2 rounded-lg hover:bg-gray-100 text-gray-600 transition-colors"
            title={isCollapsed ? 'Expandir menu' : 'Contrair menu'}
          >
            {isCollapsed ? <Menu className="w-5 h-5" /> : <X className="w-5 h-5" />}
          </button>
        </div>

        <nav className="flex-1 p-3 overflow-y-auto">
          {navigation.map((item) => (
            <NavGroup key={item.name} item={item} isCollapsed={isCollapsed} />
          ))}
        </nav>

        {!isCollapsed && (
          <div className="p-4 border-t border-gray-200">
            <div className="flex items-center gap-3 p-3 rounded-lg bg-gray-50">
              <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
                <Users className="w-4 h-4 text-gray-600" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">Admin User</p>
                <p className="text-xs text-gray-500 truncate">admin@conecta.com</p>
              </div>
            </div>
          </div>
        )}
      </div>
    </aside>
  );
}

export default Sidebar;