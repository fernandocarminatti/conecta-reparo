import { Card, CardContent } from '@/components/ui/card';

export default function History() {
  return (
    <div>
      <h1 className="text-3xl font-bold mb-8 text-foreground">Histórico de Manutenções</h1>
      <p className="text-lg mb-4 text-muted-foreground">Visualize o histórico de todas as manutenções</p>
      
      <Card>
        <CardContent className="pt-6 p-0">
          <p>Lista de manutenções históricas será exibida aqui.</p>
        </CardContent>
      </Card>
    </div>
  );
}
