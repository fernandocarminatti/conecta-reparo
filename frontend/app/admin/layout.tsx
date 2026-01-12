export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-semibold">Conecta Reparo - Admin</h1>
            </div>
            <div className="flex items-center space-x-4">
              <a href="/" className="text-gray-600 hover:text-gray-900">
                Voltar ao Site
              </a>
            </div>
          </div>
        </div>
      </nav>
      
      <div className="flex">
        <aside className="w-64 bg-white shadow-sm min-h-screen">
          <nav className="mt-8 px-4">
            <ul className="space-y-2">
              <li>
                <a href="/admin" className="block px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100">
                  Dashboard
                </a>
              </li>
              <li>
                <a href="/admin/maintenance/new" className="block px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100">
                  Nova Manutenção
                </a>
              </li>
              <li>
                <a href="/admin/history" className="block px-3 py-2 rounded-md text-gray-700 hover:bg-gray-100">
                  Histórico
                </a>
              </li>
            </ul>
          </nav>
        </aside>
        
        <main className="flex-1 p-8">
          {children}
        </main>
      </div>
    </div>
  );
}