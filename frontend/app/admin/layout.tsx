import Link from "next/link";

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen flex">
      <aside className="w-64 bg-gray-900 text-white p-6">
        <nav className="space-y-4">
          <Link href="/admin" className="block hover:underline">
            Dashboard
          </Link>
          <Link
            href="/admin/maintenance/new"
            className="block hover:underline"
          >
            Nova Manutenção
          </Link>
          <Link
            href="/admin/history"
            className="block hover:underline"
          >
            Histórico
          </Link>
        </nav>
      </aside>

      <main className="flex-1 p-8 text-slate-800bg-gray-50">
        {children}
      </main>
    </div>
  );
}
