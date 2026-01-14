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
  Package
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
    <div className="max-w-md mx-auto space-y-6">
      <div className="flex items-center gap-4">
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
            )}
          />

          <FormField
            control={form.control}
            name="description"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Descrição</FormLabel>
                <FormControl>
                  <Textarea
                    placeholder="Descrição detalhada do problema que precisa ser resolvido"
                    className="resize-none"
                    {...field}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="category"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Categoria</FormLabel>
                <Select onValueChange={field.onChange} defaultValue={field.value}>
                  <FormControl>
                    <SelectTrigger>
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
            )}
          />
          <FormField
            control={form.control}
            name="scheduledDate"
            render={({ field }) => (
              <FormItem>
                <FormLabel>
                  <Calendar className="w-4 h-4 inline mr-1" />
                  Data Agendada
                </FormLabel>
                <FormControl>
                  <Input type="date" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <div className="flex items-center justify-end gap-3 pt-4">
            <Button variant="outline" size="sm" asChild>
              <Link href="/admin/maintenances">Cancelar</Link>
            </Button>
            <Button type="submit" size="sm" disabled={isSubmitting}>
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
        </form>
      </Form>
    </div>
  );
}
