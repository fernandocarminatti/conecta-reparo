import Link from 'next/link';

export default function Home() {
  return (
    <main className="min-h-screen p-8">
      <h1 className="text-4xl font-bold mb-8">Conecta Reparo</h1>
      <p className="text-lg mb-4">Sistema de Apoio à Manutenção de Estruturas de Saúde</p>
      
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <Link 
          href="/maintenance/1"
          className="p-6 border rounded-lg hover:shadow-lg transition-shadow"
        >
          <h2 className="text-xl font-semibold">Ver Manutenções</h2>
          <p>Visualizar necessidades de manutenção abertas</p>
        </Link>
        
        <Link 
          href="/pledge"
          className="p-6 border rounded-lg hover:shadow-lg transition-shadow"
        >
          <h2 className="text-xl font-semibold">Fazer Pledge</h2>
          <p>Oferecer materiais ou voluntariado</p>
        </Link>
        
        <Link 
          href="/admin"
          className="p-6 border rounded-lg hover:shadow-lg transition-shadow"
        >
          <h2 className="text-xl font-semibold">Área Administrativa</h2>
          <p>Gerenciar manutenções (requer login)</p>
        </Link>
      </div>
    </main>
  );
}
