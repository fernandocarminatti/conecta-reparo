'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import * as z from 'zod';
import {
  ArrowLeft,
  User,
  Phone,
  FileText,
  Package,
  Hammer,
  Hand,
  Loader2,
  AlertCircle,
  Save,
  X,
  Info,
  Heart,
  Clock,
  CheckCircle2,
  ClipboardList,
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
import { 
  PledgeResponseDto, 
  PledgeStatus, 
  PledgeCategory,
  PledgeUpdateDto,
  PledgeFormData,
  PledgeFilter
} from '@/lib/types/pledges';
import { MaintenanceResponseDto } from '@/lib/types/maintenance';
import { PLEDGE_STATUS_CONFIG } from '@/lib/config/status-config';

const typeOptions: { value: PledgeCategory; label: string; icon: React.ReactNode }[] = [
  { value: 'MATERIAL', label: 'Material', icon: <Package className="w-4 h-4" /> },
  { value: 'LABOR', label: 'Mão de Obra', icon: <Hammer className="w-4 h-4" /> },
];

const statusOptions = Object.entries(PLEDGE_STATUS_CONFIG).map(([value, config]) => ({
  value,
  label: config.label,
}));

const createFormSchema = z.object({
  volunteerName: z.string().min(3, 'Nome deve ter pelo menos 3 caracteres'),
  volunteerContact: z.string().min(5, 'Contato deve ter pelo menos 5 caracteres'),
  description: z.string().min(10, 'Descrição deve ter pelo menos 10 caracteres'),
  type: z.enum(['MATERIAL', 'LABOR']),
  maintenanceId: z.string().min(1, 'Selecione uma manutenção'),
});

const editFormSchema = createFormSchema.extend({
  status: z.enum(['OFFERED', 'PENDING', 'REJECTED', 'COMPLETED', 'CANCELED']),
});

type CreateFormValues = z.infer<typeof createFormSchema>;
type EditFormValues = z.infer<typeof editFormSchema>;

interface PledgeFormProps {
  initialData: PledgeResponseDto | null;
  mode: 'create' | 'edit';
  maintenances: MaintenanceResponseDto[];
  onSubmit: (data: CreateFormValues | EditFormValues) => Promise<void>;
  onCancel?: () => void;
  isSubmitting?: boolean;
  error?: string | null;
}

export function PledgeForm({
  initialData,
  mode,
  maintenances,
  onSubmit,
  onCancel,
  isSubmitting = false,
  error = null,
}: PledgeFormProps) {
  const router = useRouter();
  const [submitError, setSubmitError] = useState<string | null>(error);

  const activeMaintenances = maintenances.filter(m => 
    m.status === 'OPEN' || m.status === 'IN_PROGRESS'
  );

  const defaultValues = mode === 'create'
    ? {
        volunteerName: '',
        volunteerContact: '',
        description: '',
        type: 'MATERIAL' as PledgeCategory,
        maintenanceId: '',
      }
    : {
        volunteerName: initialData!.volunteerName,
        volunteerContact: initialData!.volunteerContact,
        description: initialData!.description,
        type: initialData!.type,
        maintenanceId: '',
        status: initialData!.status,
      };

  const formSchema = mode === 'edit' ? editFormSchema : createFormSchema;

  const form = useForm<CreateFormValues | EditFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: defaultValues as CreateFormValues | EditFormValues,
    mode: 'onChange',
  });

  const handleSubmit = async (data: CreateFormValues | EditFormValues) => {
    setSubmitError(null);
    try {
      await onSubmit(data);
    } catch (err) {
      setSubmitError(err instanceof Error ? err.message : 'Erro ao salvar');
    }
  };

  const pageTitle = mode === 'create' ? 'Nova Oferta' : `Editar Oferta - ${initialData?.volunteerName}`;
  const pageDescription = mode === 'create'
    ? 'Cadastre uma nova oferta de material ou mão de obra'
    : 'Edite os detalhes da oferta';
  const submitLabel = mode === 'create' ? 'Criar Oferta' : 'Salvar Alterações';

  return (
    <>
      <div className="flex items-center gap-4 mb-6">
        <Button variant="ghost" size="sm" asChild>
          <Link href="/admin/pledges">
            <ArrowLeft className="w-4 h-4" />
          </Link>
        </Button>
        <div>
          <h2 className="text-2xl font-bold text-foreground">{pageTitle}</h2>
          <p className="text-muted-foreground mt-1">{pageDescription}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-12 mx-auto max-w-[1600px] mt-12">
        <div className="lg:col-span-1 space-y-6">
          {(submitError || error) && (
            <div className="bg-destructive/10 border border-destructive/20 rounded-lg p-4">
              <div className="flex items-center gap-3">
                <AlertCircle className="w-5 h-5 text-destructive" />
                <p className="text-destructive text-sm">{submitError || error}</p>
              </div>
            </div>
          )}

          <Form {...form}>
            <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
              {mode === 'create' && (
                <FormField
                  control={form.control}
                  name="maintenanceId"
                  render={({ field }) => (
                    <FormItem className="w-full">
                      <FormLabel>
                        <ClipboardList className="w-4 h-4 inline mr-1" />
                        Manutenção
                      </FormLabel>
                      <Select onValueChange={field.onChange} defaultValue={field.value}>
                        <FormControl>
                          <SelectTrigger className="w-full">
                            <SelectValue placeholder="Selecione uma manutenção" />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent position="popper" align="start">
                          {activeMaintenances.map((m) => (
                            <SelectItem key={m.id} value={m.id} >
                              <div className="flex items-center gap-2">
                                <span className="truncate max-w-[550]">{m.title}</span>
                              </div>
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                      <p className="text-xs text-muted-foreground">
                        Apenas manutenções ativas (Aberto/Em Andamento)
                      </p>
                      <FormMessage />
                    </FormItem>
                  )} />
              )}

              <div className="flex gap-4 flex-wrap">
                <FormField
                  control={form.control}
                  name="volunteerName"
                  render={({ field }) => (
                    <FormItem className="flex-1 min-w-[200px]">
                      <FormLabel>
                        <User className="w-4 h-4 inline mr-1" />
                        Nome
                      </FormLabel>
                      <FormControl>
                        <Input placeholder="Nome do voluntário" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )} />

                <FormField
                  control={form.control}
                  name="volunteerContact"
                  render={({ field }) => (
                    <FormItem className="flex-1 min-w-[200px]">
                      <FormLabel>
                        <Phone className="w-4 h-4 inline mr-1" />
                        Contato
                      </FormLabel>
                      <FormControl>
                        <Input placeholder="Email ou telefone" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )} />
              </div>

              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>
                      <FileText className="w-4 h-4 inline mr-1" />
                      Descrição
                    </FormLabel>
                    <FormControl>
                      <Textarea
                        placeholder="Descrição detalhada da oferta"
                        className="resize-none min-h-[120]"
                        {...field} />
                    </FormControl>
                      <FormMessage />
                    </FormItem>
                  )} />

              <div className="flex gap-4 flex-wrap">
                <FormField
                  control={form.control}
                  name="type"
                  render={({ field }) => (
                    <FormItem className="w-[160] shrink-0">
                      <FormLabel>Tipo</FormLabel>
                      <Select onValueChange={field.onChange} defaultValue={field.value}>
                        <FormControl>
                          <SelectTrigger className="w-full truncate">
                            <SelectValue placeholder="Selecione" />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent position="popper" align="start">
                          {typeOptions.map((opt) => (
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

                {mode === 'edit' && (
                  <FormField
                    control={form.control}
                    name="status"
                    render={({ field }) => (
<FormItem className="w-[200px] shrink-0">
                        <FormLabel>Status</FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <FormControl>
<SelectTrigger className="w-full">
                              <SelectValue placeholder="Selecione" />
                            </SelectTrigger>
                          </FormControl>
<SelectContent>
                            {statusOptions.map((opt) => (
                              <SelectItem key={opt.value} value={opt.value}>
                                {opt.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )} />
                )}
              </div>
            </form>

            <div className="flex items-center justify-end gap-3 pt-4">
              <Button variant="outline" size="sm" onClick={onCancel || (() => router.push('/admin/pledges'))}>
                <X className="w-4 h-4 mr-1" />
                Cancelar
              </Button>
              <Button type="button" size="sm" disabled={isSubmitting} onClick={form.handleSubmit(handleSubmit)}>
                {isSubmitting ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin mr-1" />
                    {mode === 'create' ? 'Criando...' : 'Salvando...'}
                  </>
                ) : (
                  <>
                    <Save className="w-4 h-4 mr-1" />
                    {submitLabel}
                  </>
                )}
              </Button>
            </div>
          </Form>
        </div>

        <div className="space-y-6">
          <div className="bg-muted/50 rounded-lg p-5 space-y-5 sticky top-6">
            <h3 className="font-semibold text-foreground flex items-center gap-2">
              <Info className="w-5 h-5 text-blue-500" />
              Guia de {mode === 'create' ? 'Criação' : 'Edição'}
            </h3>
            <div className="space-y-3">
              <h4 className="text-sm font-medium text-foreground flex items-center gap-2">
                <Heart className="w-4 h-4 text-red-500" />
                Fluxo de trabalho:
              </h4>
              <div className="text-sm text-muted-foreground space-y-2">
                <div className="flex items-center gap-2">
                  <span className={`px-1.5 py-0.5 rounded text-xs ${mode === 'create' ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'}`}>1</span>
                  <span>{mode === 'create' ? 'Oferta criada (você está aqui)' : 'Oferta criada'}</span>
                </div>
                <div className="flex items-center gap-2">
                  <span className="bg-muted text-muted-foreground px-1.5 py-0.5 rounded text-xs">2</span>
                  <span>Oferta aceita ou rejeitada</span>
                </div>
                <div className="flex items-center gap-2">
                  <span className="bg-muted text-muted-foreground px-1.5 py-0.5 rounded text-xs">3</span>
                  <span>Conclusão da manutenção</span>
                </div>
              </div>
            </div>
            <div className="space-y-3">
              <h4 className="text-sm font-medium text-foreground flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-green-600" />
                {mode === 'create' ? 'Antes de criar:' : 'Antes de editar:'}
              </h4>
              <ul className="text-sm text-muted-foreground space-y-1 list-disc list-inside ml-2">
                <li>Verifique a manutenção selecionada</li>
                <li>Confirme os dados de contato</li>
                <li>Descreva claramente a oferta</li>
              </ul>
            </div>
          </div>
        </div>

        <div className="space-y-6">
          <div className="bg-muted/50 rounded-lg p-5 space-y-3 sticky top-6">
            <h4 className="text-sm font-medium text-foreground flex items-center gap-2">
              <Hand className="w-4 h-4 text-orange-600" />
              Tipos de oferta:
            </h4>
            <div className="text-sm text-muted-foreground space-y-2">
              <div className="flex items-center gap-2">
                <Package className="w-4 h-4" />
                <span className="font-medium">Material:</span>
                <span>Doação de ferramentas, peças, equipamentos</span>
              </div>
              <div className="flex items-center gap-2">
                <Hammer className="w-4 h-4" />
                <span className="font-medium">Mão de Obra:</span>
                <span>Voluntariado para execução de serviços</span>
              </div>
            </div>
          </div>

          <div className="bg-muted/50 rounded-lg p-5 space-y-3">
            <h4 className="text-sm font-medium text-foreground flex items-center gap-2">
              <Clock className="w-4 h-4 text-purple-600" />
              Detalhes importates:
            </h4>
            <div className="text-sm text-muted-foreground space-y-2">
              <p>Uma oferta só pode ser criada para manutenções em estado ativo:</p>
              <ul className="list-disc list-inside ml-2 space-y-1">
                <li>Aberto</li>
                <li>Em Andamento</li>
              </ul>
              <p className="mt-2 text-xs">Não é possível criar ofertas para manutenções concluídas ou canceladas.</p>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default PledgeForm;
