'use client';

import { useState } from 'react';
import { useTheme } from 'next-themes';
import { Bell, Search, User, LogOut, HelpCircle, Settings } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface HeaderProps {
  title: string;
  subtitle?: string;
}

export function Header({ title, subtitle }: HeaderProps) {
  const [showUserMenu, setShowUserMenu] = useState(false);
  const { theme, setTheme } = useTheme();

  return (
    <header className="bg-card border-b border-border sticky top-0 z-30">
      <div className="flex items-center justify-between px-6 py-4">
        <div>
          <h1 className="text-2xl font-bold text-foreground">{title}</h1>
          {subtitle && <p className="text-sm text-muted-foreground mt-1">{subtitle}</p>}
        </div>

        <div className="flex items-center gap-4">
          <div className="relative hidden md:block">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <input
              type="text"
              placeholder="Buscar..."
              className="w-64 pl-10 pr-4 py-2 border border-input rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 bg-background"
            />
          </div>

          <Button variant="ghost" size="icon" className="relative">
            <Bell className="w-4 h-4" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-destructive rounded-full"></span>
          </Button>

          <Button variant="ghost" size="icon">
            <HelpCircle className="w-4 h-4" />
          </Button>

          <div className="relative">
            <Button variant="ghost" onClick={() => setShowUserMenu(!showUserMenu)}>
              <div className="w-8 h-8 bg-primary/10 rounded-full flex items-center justify-center">
                <User className="w-4 h-4 text-primary" />
              </div>
            </Button>

            {showUserMenu && (
              <div className="absolute right-0 mt-2 w-48 bg-popover rounded-lg shadow-lg border border-border py-1 z-50">
                <div className="px-4 py-2 border-b border-border">
                  <p className="text-sm font-medium text-foreground">Admin User</p>
                  <p className="text-xs text-muted-foreground">admin@conecta.com</p>
                </div>
                <a
                  href="/admin/profile"
                  className="flex items-center gap-2 px-4 py-2 text-sm hover:bg-muted"
                >
                  <User className="w-4 h-4" />
                  Perfil
                </a>
                <Button
                  variant="ghost"
                  onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
                  className="text-sm w-full justify-start"
                >
                  {theme === 'dark' ? 'Change to light theme' : 'Change to dark theme'}
                </Button>
                <a
                  href="/admin/settings"
                  className="flex items-center gap-2 px-4 py-2 text-sm hover:bg-muted"
                >
                  <Settings className="w-4 h-4" />
                  Configurações
                </a>
                <hr className="my-1" />
                <button className="flex items-center gap-2 px-4 py-2 text-sm text-destructive hover:bg-destructive/10 w-full">
                  <LogOut className="w-4 h-4" />
                  Sair
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}

export default Header;
