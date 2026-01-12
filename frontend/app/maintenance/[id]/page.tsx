interface MaintenancePageProps {
  params: {
    id: string;
  };
}

export default function MaintenancePage({ params }: MaintenancePageProps) {
  return (
    <main className="p-8 max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold mb-4">
        Detalhes da Manutenção
      </h1>
      <p className="text-lg">
        ID da manutenção: <strong>{params.id}</strong>
      </p>
    </main>
  );
}
