'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import * as z from 'zod';
import {
  ArrowLeft,
  Calendar,
  ClipboardList,
  Loader2,
  AlertCircle,
  Building2,
  Zap,
  Wrench,
  Snowflake,
  Armchair,
  Trees,
  Lock,
  Package,
  Lightbulb,
  Hammer,
  ListChecks,
  CheckCircle2,
  Clock,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { MaintenanceCategory } from '@/lib/types/maintenance';
import { maintenanceApi } from '@/lib/api/maintenance';

const categoryOptions: { value: MaintenanceCategory; label: string; icon: React.ReactNode }[] = [
  { value: 'BUILDING', label: 'Construção', icon: <Building2 className="w-4 h-4" /> },
  { value: 'ELECTRICAL', label: 'Elétrica', icon: <Zap className="w-4 h-4" /> },
  { value: 'PLUMBING', label: 'Hidráulica', icon: <Wrench className="w-4 h-4" /> },
  { value: 'HVAC', label: 'HVAC', icon: <Snowflake className="w-4 h-4" /> },
  { value: 'FURNITURE', label: 'Mobília', icon: <Armchair className="w-4 h-4" /> },
  { value: 'GARDENING', label: 'Jardinagem', icon: <Trees className="w-4 h-4" /> },
  { value: 'SECURITY', label: 'Segurança', icon: <Lock className="w-4 h-4" /> },
  { value: 'OTHERS', label: 'Outros', icon: <Package className="w-4 h-4" /> },
];

const categoryDescriptions: Record<MaintenanceCategory, string> = {
  BUILDING: 'Reparos estruturais, renovações',
  ELECTRICAL: 'Fiação, tomadas, iluminação',
  PLUMBING: 'Vazamentos, tubulações, filtros',
  HVAC: 'Aquecimento, ventilação, ar condicionado',
  FURNITURE: 'Reparos de eletrodomésticos',
  GARDENING: 'Cuidados com gramado, jardinagem',
  SECURITY: 'Fechaduras, alarmes, câmeras',
  OTHERS: 'Miscelânea',
};

const commonMaterials: Record<string, string[]> = {
  Elétrica: ['Fiação', 'Fusíveis', 'Tomadas', 'Disjuntores'],
  Hidráulica: ['Tubos', 'Juntas', 'Vedações', 'Válvulas'],
  Construção: ['Cimento', 'Tijolos', 'Tinta', 'Drywall'],
  HVAC: ['Filtros', 'Refrigerantes', 'Correias'],
  Mobília: ['Peças', 'Hardware', 'Estofados'],
  Jardinagem: ['Plantas', 'Terra', 'Ferramentas', 'Adubo'],
  Segurança: ['Fechaduras', 'Sensores', 'Câmeras'],
  Outros: ['Materiais gerais'],
};

const formSchema = z.object({
  title: z.string().min(3, 'Título deve ter pelo menos 3 caracteres'),
  description: z.string().min(10, 'Descrição deve ter pelo menos 10 caracteres'),
  category: z.enum(['BUILDING', 'ELECTRICAL', 'PLUMBING', 'HVAC', 'FURNITURE', 'GARDENING', 'SECURITY', 'OTHERS']),
  scheduledDate: z.string().min(1, 'Data é obrigatória'),
});

type FormValues = z.infer<typeof formSchema>;

export default function NewMaintenancePage() {
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      title: '',
      description: '',
      category: 'OTHERS',
      scheduledDate: new Date().toISOString().split('T')[0],
    },
    mode: 'onChange',
  });

  const onSubmit = async (data: FormValues) => {
    setIsSubmitting(true);
    setError(null);

    try {
      const dataToSubmit = {
        ...data,
        scheduledDate: new Date(data.scheduledDate).toISOString(),
      };

      const created = await maintenanceApi.create(dataToSubmit);
      router.push(`/admin/maintenances/${created.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao criar manutenção');
      setIsSubmitting(false);
    }
  };

  return (
    <><div className="flex items-center gap-4 mb-6" >
      <Button variant="ghost" size="sm" asChild>
        <Link href="/admin/maintenances">
          <ArrowLeft className="w-4 h-4" />
        </Link>
      </Button>
      <div>
        <h2 className="text-2xl font-bold text-foreground">Nova Manutenção</h2>
        <p className="text-muted-foreground mt-1">Crie uma nova solicitação de manutenção</p>
      </div>
    </div>
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-12 mx-auto max-w-[1600] mt-12">
        <div className="lg:col-span-1 space-y-6">
          {error && (
            <div className="bg-destructive/10 border border-destructive/20 rounded-lg p-4">
              <div className="flex items-center gap-3">
                <AlertCircle className="w-5 h-5 text-destructive" />
                <p className="text-destructive text-sm">{error}</p>
              </div>
            </div>
          )}

            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                <FormField
                  control={form.control}
                  name="title"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>
                        <ClipboardList className="w-4 h-4 inline mr-1" />
                        Título
                      </FormLabel>
                      <FormControl>
                        <Input placeholder="Título resumido do problema" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )} />

                <FormField
                  control={form.control}
                  name="description"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Descrição</FormLabel>
                      <FormControl>
                        <Textarea
                          placeholder="Descrição detalhada do problema que precisa ser resolvido"
                          className="resize-none min-h-[280]"
                          {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )} />
                <div className="flex gap-4">
                  <FormField
                    control={form.control}
                    name="category"
                    render={({ field }) => (
                      <FormItem className="flex-1">
                        <FormLabel>Categoria</FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <FormControl>
                            <SelectTrigger className="w-[240]">
                              <SelectValue placeholder="Selecione uma categoria" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent position="popper" align="start" className="w-[var(--radix-select-trigger-width)]">
                            {categoryOptions.map((opt) => (
                              <SelectItem key={opt.value} value={opt.value}>
                                <div className="flex items-center gap-2">
                                  {opt.icon}
                                  <span>{opt.label}</span>
                                </div>
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )} />
                  <FormField
                    control={form.control}
                    name="scheduledDate"
                    render={({ field }) => (
                      <FormItem className="w-[160px] shrink-0">
                        <FormLabel>
                          <Calendar className="w-4 h-4 inline mr-1" />
                          Data
                        </FormLabel>
                        <FormControl>
                          <Input type="date" className="w-full" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )} />
                </div>
              </form>
              <div className="flex items-center justify-end gap-3 pt-4">
                <Button variant="outline" size="sm" asChild>
                  <Link href="/admin/maintenances">Cancelar</Link>
                </Button>
                <Button type="button" size="sm" disabled={isSubmitting} onClick={form.handleSubmit(onSubmit)}>
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
            </Form>
        </div>

        <div className="space-y-6">
          <div className="bg-muted/50 rounded-lg p-5 space-y-5 sticky top-6">
            <h3 className="font-semibold text-foreground flex items-center gap-2">
              <Lightbulb className="w-5 h-5 text-amber-500" />
              Guia de Criação
            </h3>
            <div className="space-y-3">
              <h4 className="text-sm font-medium text-foreground flex items-center gap-2">
                <Clock className="w-4 h-4 text-orange-600" />
                Fluxo de trabalho:
              </h4>
              <div className="text-sm text-muted-foreground space-y-2">
                <div className="flex items-center gap-2">
                  <span className="bg-primary text-primary-foreground px-1.5 py-0.5 rounded text-xs">1</span>
                  <span>Criação (você está aqui)</span>
                </div>
                <div className="flex items-center gap-2">
                  <span className="bg-muted text-muted-foreground px-1.5 py-0.5 rounded text-xs">2</span>
                  <span>Aberto → Em Andamento</span>
                </div>
                <div className="flex items-center gap-2">
                  <span className="bg-muted text-muted-foreground px-1.5 py-0.5 rounded text-xs">3</span>
                  <span>Ações com materiais</span>
                </div>
                <div className="flex items-center gap-2">
                  <span className="bg-muted text-muted-foreground px-1.5 py-0.5 rounded text-xs">4</span>
                  <span>Conclusão</span>
                </div>
              </div>
            </div>
            <div className="space-y-3">
              <h4 className="text-sm font-medium text-foreground flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-green-600" />
                Antes de criar:
              </h4>
              <ul className="text-sm text-muted-foreground space-y-1 list-disc list-inside ml-2">
                <li>Verifique se já há materiais disponíveis</li>
                <li>Considere a urgência do problema</li>
                <li>Defina a data ideal para execução</li>
              </ul>
            </div>
          </div>
        </div>

        <div className="space-y-6">
          <div className="bg-muted/50 rounded-lg p-5 space-y-3 sticky top-6">
            <h4 className="text-sm font-medium text-foreground flex items-center gap-2">
              <Hammer className="w-4 h-4 text-blue-600" />
              Materiais comuns por categoria:
            </h4>
            <div className="text-sm text-muted-foreground space-y-2">
              {Object.entries(commonMaterials).map(([category, materials]) => (
                <div key={category}>
                  <span className="font-medium text-foreground">{category}:</span>{' '}
                  {materials.join(', ')}
                </div>
              ))}
            </div>
          </div>

          <div className="bg-muted/50 rounded-lg p-5 space-y-3">
            <h4 className="text-sm font-medium text-foreground flex items-center gap-2">
              <ListChecks className="w-4 h-4 text-purple-600" />
              Descrição das categorias:
            </h4>
            <div className="text-sm text-muted-foreground space-y-2">
              {categoryOptions.map((opt) => (
                <div key={opt.value} className="flex items-center gap-2">
                  {opt.icon}
                  <span className="font-medium">{opt.label}:</span>
                  <span>{categoryDescriptions[opt.value as MaintenanceCategory]}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div></>
  );
}
