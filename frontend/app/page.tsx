import Link from "next/link";

export default function Home() {
  return (
    <main className="min-h-screen p-8 max-w-7xl mx-auto">
      <h1 className="text-4xl font-bold mb-6">Conecta Reparo</h1>
      <p className="text-lg mb-10">
        Sistema de Apoio à Manutenção de Estruturas de Saúde
      </p>

      <section className="text-slate-800 grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <Link
          href="/maintenance/1"
          className="p-6 bg-white border rounded-lg hover:shadow-md transition"
        >
          <h2 className="text-xl font-semibold mb-2">
            Ver Manutenções
          </h2>
          <p>Visualizar necessidades de manutenção abertas</p>
        </Link>

        <Link
          href="/pledge"
          className="p-6 bg-white border rounded-lg hover:shadow-md transition"
        >
          <h2 className="text-xl font-semibold mb-2">
            Fazer Pledge
          </h2>
          <p>Oferecer materiais ou voluntariado</p>
        </Link>

        <Link
          href="/admin"
          className="p-6 bg-white border rounded-lg hover:shadow-md transition"
        >
          <h2 className="text-xl font-semibold mb-2">
            Área Administrativa
          </h2>
          <p>Gerenciar manutenções (requer login)</p>
        </Link>
      </section>
    </main>
  );
}
