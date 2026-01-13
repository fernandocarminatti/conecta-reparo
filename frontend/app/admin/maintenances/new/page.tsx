'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import {
  ArrowLeft,
  Calendar,
  ClipboardList,
  Loader2,
  AlertCircle
} from 'lucide-react';
import { MaintenanceDto, MaintenanceCategory } from '@/lib/types/maintenance';
import { maintenanceApi } from '@/lib/api/maintenance';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';

const categoryOptions: { value: MaintenanceCategory; label: string; icon: string }[] = [
  { value: 'BUILDING', label: 'Construção', icon: '🏢' },
  { value: 'ELECTRICAL', label: 'Elétrica', icon: '⚡' },
  { value: 'PLUMBING', label: 'Hidráulica', icon: '🔧' },
  { value: 'HVAC', label: 'HVAC', icon: '❄️' },
  { value: 'FURNITURE', label: 'Mobília', icon: '🪑' },
  { value: 'GARDENING', label: 'Jardinagem', icon: '🌿' },
  { value: 'SECURITY', label: 'Segurança', icon: '🔒' },
  { value: 'OTHERS', label: 'Outros', icon: '📦' },
];

export default function NewMaintenancePage() {
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<MaintenanceDto>({
    title: '',
    description: '',
    category: 'OTHERS',
    scheduledDate: new Date().toISOString().split('T')[0],
  });

  const updateField = (field: keyof MaintenanceDto, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const isValid = formData.title.trim() !== '' &&
    formData.description.trim() !== '' &&
    formData.scheduledDate !== '';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!isValid) return;

    setIsSubmitting(true);
    setError(null);

    try {
      const dataToSubmit: MaintenanceDto = {
        ...formData,
        scheduledDate: new Date(formData.scheduledDate).toISOString(),
      };

      const created = await maintenanceApi.create(dataToSubmit);
      router.push(`/admin/maintenances/${created.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao criar manutenção');
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center gap-4">
        <Button variant="ghost" asChild>
          <Link href="/admin/maintenances">
            <ArrowLeft className="w-4 h-4" />
          </Link>
        </Button>
        <div>
          <h2 className="text-2xl font-bold text-foreground">Nova Manutenção</h2>
          <p className="text-muted-foreground mt-1">Crie uma nova solicitação de manutenção</p>
        </div>
      </div>

      {error && (
        <div className="bg-destructive/10 border border-destructive/20 rounded-lg p-4">
          <div className="flex items-center gap-3">
            <AlertCircle className="w-5 h-5 text-destructive" />
            <p className="text-destructive text-sm">{error}</p>
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <Card>
          <CardContent className="pt-6 p-0">
            <div className="grid gap-6">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Categoria</label>
            <select
              value={formData.category}
              onChange={(e) => updateField('category', e.target.value as MaintenanceCategory)}
              className="w-full px-3 py-2 border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring bg-background"
            >
              {categoryOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.icon} {opt.label}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">
              <ClipboardList className="w-4 h-4 inline mr-1" />
              Título
            </label>
            <input
              type="text"
              value={formData.title}
              onChange={(e) => updateField('title', e.target.value)}
              className="w-full px-3 py-2 border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring text-foreground placeholder:text-muted-foreground bg-background"
              placeholder="Título resumido do problema"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Descrição</label>
            <textarea
              value={formData.description}
              onChange={(e) => updateField('description', e.target.value)}
              rows={4}
              className="w-full px-3 py-2 border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring text-foreground placeholder:text-muted-foreground resize-none bg-background"
              placeholder="Descrição detalhada do problema que precisa ser resolvido"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">
              <Calendar className="w-4 h-4 inline mr-1" />
              Data Agendada
            </label>
            <input
              type="date"
              value={formData.scheduledDate}
              onChange={(e) => updateField('scheduledDate', e.target.value)}
              className="w-full px-3 py-2 border border-input rounded-lg focus:outline-none focus:ring-2 focus:ring-ring text-foreground bg-background"
            />
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-border">
            <Button variant="outline" asChild>
              <Link href="/admin/maintenances">Cancelar</Link>
            </Button>
            <Button type="submit" disabled={!isValid || isSubmitting}>
              {isSubmitting ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  Criando...
                </>
              ) : (
                'Criar Manutenção'
              )}
            </Button>
          </div>
        </div>
        </CardContent>
        </Card>
      </form>
    </div>
  );
}
