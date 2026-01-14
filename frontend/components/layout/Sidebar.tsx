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
import { Button } from '@/components/ui/button';

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
      { name: 'Todas as Manutenções', href: '/admin/maintenances' },
      { name: 'Nova Manutenção', href: '/admin/maintenances/new' },
      { name: 'Histórico', href: '/admin/history' },
    ],
  },
  {
    name: 'Ofertas',
    href: '/admin/pledges',
    icon: Heart,
    children: [
      { name: 'Todos as Ofertas', href: '/admin/pledges' },
      { name: 'Ofertas Pendentes', href: '/admin/pledges/pending' },
      { name: 'Ofertas Aprovadas', href: '/admin/pledges/approved' },
    ],
  },
  {
    name: 'Ações',
    href: '/admin/actions',
    icon: ClipboardList,
    children: [
      { name: 'Todas as Ações', href: '/admin/actions' },
      { name: 'Ações Planejadas', href: '/admin/actions/planned' },
      { name: 'Ações Em Andamento', href: '/admin/actions/in-progress' },
      { name: 'Ações Concluídas', href: '/admin/actions/completed' },
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
              ? 'bg-primary/10 text-primary'
              : 'text-muted-foreground hover:bg-muted hover:text-foreground'
          }`}
          title={item.name}
        >
          <item.icon className="w-5 h-5" />
        </Link>
        {item.children && (
          <div className="absolute left-full top-0 ml-2 w-48 bg-popover rounded-lg shadow-lg border hidden group-hover:block z-50">
            {item.children.map((child) => (
              <Link
                key={child.name}
                href={child.href}
                className="block px-4 py-2 text-sm hover:bg-muted first:rounded-t-lg last:rounded-b-lg"
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
      {item.children ? (
        <button
          onClick={() => setIsOpen(!isOpen)}
          className={`w-full flex items-center justify-between p-3 rounded-lg transition-colors ${
            isActive
              ? 'bg-primary/10 text-primary'
              : 'text-muted-foreground hover:bg-muted hover:text-foreground'
          }`}
        >
          <div className="flex items-center gap-3">
            <item.icon className="w-5 h-5" />
            <span className="font-medium">{item.name}</span>
          </div>
          <ChevronRight
            className={`w-4 h-4 transition-transform ${isOpen ? 'rotate-90' : ''}`}
          />
        </button>
      ) : (
        <Link
          href={item.href}
          className={`flex items-center justify-between p-3 rounded-lg transition-colors ${
            isActive
              ? 'bg-primary/10 text-primary'
              : 'text-muted-foreground hover:bg-muted hover:text-foreground'
          }`}
        >
          <div className="flex items-center gap-3">
            <item.icon className="w-5 h-5" />
            <span className="font-medium">{item.name}</span>
          </div>
        </Link>
      )}
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
                    ? 'bg-primary/10 text-primary font-medium'
                    : 'text-muted-foreground hover:bg-muted hover:text-foreground'
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
      className={`fixed left-0 top-0 h-full bg-card border-r border-border transition-all duration-300 z-40 ${
        isCollapsed ? 'w-16' : 'w-64'
      }`}
    >
      <div className="flex flex-col h-full">
        <div className={`flex items-center justify-between p-4 border-b border-border ${isCollapsed ? 'justify-center' : ''}`}>
          {!isCollapsed && (
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
                <Wrench className="w-5 h-5 text-primary-foreground" />
              </div>
              <span className="font-bold text-foreground">Conecta Reparo</span>
            </div>
          )}
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setIsCollapsed(!isCollapsed)}
            title={isCollapsed ? 'Expandir menu' : 'Contrair menu'}
          >
            {isCollapsed ? <Menu className="w-4 h-4" /> : <X className="w-4 h-4" />}
          </Button>
        </div>

        <nav className="flex-1 p-3 overflow-y-auto">
          {navigation.map((item) => (
            <NavGroup key={item.name} item={item} isCollapsed={isCollapsed} />
          ))}
        </nav>

        {!isCollapsed && (
          <div className="p-4 border-t border-border">
            <div className="flex items-center gap-3 p-3 rounded-lg bg-muted">
              <div className="w-8 h-8 bg-secondary rounded-full flex items-center justify-center">
                <Users className="w-4 h-4 text-secondary-foreground" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-foreground truncate">Admin User</p>
                <p className="text-xs text-muted-foreground truncate">admin@conecta.com</p>
              </div>
            </div>
          </div>
        )}
      </div>
    </aside>
  );
}

export default Sidebar;
