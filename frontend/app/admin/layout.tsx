import { Sidebar } from '@/components/layout/Sidebar';
import { Header } from '@/components/layout/Header';

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <div className="ml-64 transition-all duration-300">
        <Header 
          title="Conecta Reparo" 
          subtitle="Painel Administrativo"
        />
        <main className="p-6">
          {children}
        </main>
      </div>
    </div>
  );
}
