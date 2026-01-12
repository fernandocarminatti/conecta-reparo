export default function AdminDashboard() {
  return (
    <main className="min-h-screen p-8">
      <h1 className="text-4xl font-bold mb-8">Dashboard Administrativo</h1>
      <p className="text-lg mb-4">Gerencie manutenções e visualize estatísticas</p>
      
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <div className="p-6 border rounded-lg">
          <h2 className="text-xl font-semibold mb-2">Manutenções Ativas</h2>
          <p className="text-3xl font-bold">0</p>
        </div>
        
        <div className="p-6 border rounded-lg">
          <h2 className="text-xl font-semibold mb-2">Pledges Recebidos</h2>
          <p className="text-3xl font-bold">0</p>
        </div>
        
        <div className="p-6 border rounded-lg">
          <h2 className="text-xl font-semibold mb-2">Voluntários</h2>
          <p className="text-3xl font-bold">0</p>
        </div>
      </div>
    </main>
  );
}