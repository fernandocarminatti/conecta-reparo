interface EditMaintenanceProps {
  params: {
    id: string;
  };
}

export default function EditMaintenancePage({
  params,
}: EditMaintenanceProps) {
  return (
    <section>
      <h1 className="text-2xl font-bold mb-4">
        Editar Manutenção
      </h1>
      <p>Editando manutenção com ID: {params.id}</p>
    </section>
  );
}
