'use client';

import { useState, useEffect, useCallback } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import {
  ArrowLeft,
  Edit,
  Save,
  X,
  Clock,
  Calendar,
  ClipboardList,
  Heart,
  RefreshCw,
  AlertCircle,
  CheckCircle,
  Loader2,
  Eye,
  FileText
} from 'lucide-react';
import { 
  MaintenanceResponseDto, 
  MaintenanceDetailResponseDto,
  MaintenanceFormData,
  MaintenanceActionResponseDto,
  PledgeResponseDto,
  MaintenanceStatus,
  MaintenanceCategory,
  ActionStatus,
  PledgeStatus
} from '@/lib/types/maintenance';
import { maintenanceApi } from '@/lib/api/maintenance';

const statusOptions: { value: MaintenanceStatus; label: string; color: string }[] = [
  { value: 'OPEN', label: 'Aberto', color: 'bg-blue-100 text-blue-700' },
  { value: 'IN_PROGRESS', label: 'Em Andamento', color: 'bg-yellow-100 text-yellow-700' },
  { value: 'COMPLETED', label: 'Concluído', color: 'bg-green-100 text-green-700' },
  { value: 'CANCELED', label: 'Cancelado', color: 'bg-red-100 text-red-700' },
];

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

type Tab = 'details' | 'edit' | 'actions' | 'pledges';

function StatusBadge({ status }: { status: MaintenanceStatus }) {
  const option = statusOptions.find(o => o.value === status);
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${option?.color || 'bg-gray-100 text-gray-700'}`}>
      {option?.label || status}
    </span>
  );
}

function CategoryBadge({ category }: { category: MaintenanceCategory }) {
  const option = categoryOptions.find(o => o.value === category);
  return (
    <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-700">
      <span>{option?.icon}</span>
      {option?.label || category}
    </span>
  );
}

function ActionStatusBadge({ status }: { status: ActionStatus }) {
  const styles: Record<ActionStatus, { color: string; icon: React.ComponentType<{ className?: string }> }> = {
    SUCCESS: { color: 'bg-green-100 text-green-700', icon: CheckCircle },
    PARTIAL_SUCCESS: { color: 'bg-yellow-100 text-yellow-700', icon: AlertCircle },
    FAILURE: { color: 'bg-red-100 text-red-700', icon: X },
  };
  const { color, icon: Icon } = styles[status];
  const labels: Record<ActionStatus, string> = {
    SUCCESS: 'Sucesso',
    PARTIAL_SUCCESS: 'Sucesso Parcial',
    FAILURE: 'Falha',
  };
  
  return (
    <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium ${color}`}>
      <Icon className="w-3 h-3" />
      {labels[status]}
    </span>
  );
}

function PledgeStatusBadge({ status }: { status: PledgeStatus }) {
  const styles: Record<PledgeStatus, { color: string; icon: React.ComponentType<{ className?: string }> }> = {
    OFFERED: { color: 'bg-blue-100 text-blue-700', icon: Heart },
    PENDING: { color: 'bg-yellow-100 text-yellow-700', icon: Clock },
    REJECTED: { color: 'bg-red-100 text-red-700', icon: X },
    COMPLETED: { color: 'bg-green-100 text-green-700', icon: CheckCircle },
    CANCELED: { color: 'bg-gray-100 text-gray-700', icon: X },
  };
  const { color, icon: Icon } = styles[status];
  const labels: Record<PledgeStatus, string> = {
    OFFERED: 'Oferecido',
    PENDING: 'Pendente',
    REJECTED: 'Rejeitado',
    COMPLETED: 'Concluído',
    CANCELED: 'Cancelado',
  };
  
  return (
    <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium ${color}`}>
      <Icon className="w-3 h-3" />
      {labels[status]}
    </span>
  );
}

interface MaintenanceActionsListProps {
  actions: MaintenanceActionResponseDto[];
  maintenanceId: string;
}

function MaintenanceActionsList({ actions, maintenanceId }: MaintenanceActionsListProps) {
  if (actions.length === 0) {
    return (
      <div className="text-center py-12 bg-gray-50 rounded-lg">
        <ClipboardList className="w-12 h-12 text-gray-300 mx-auto mb-3" />
        <p className="text-gray-500">Nenhuma ação registrada</p>
        <Link
          href={`/admin/maintenances/${maintenanceId}/actions/new`}
          className="inline-flex items-center gap-2 mt-3 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm"
        >
          <ClipboardList className="w-4 h-4" />
          Nova Ação
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {actions.map((action) => (
        <div key={action.id} className="bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <div className="flex items-center gap-2 mb-2">
                <ActionStatusBadge status={action.outcomeStatus} />
                <span className="text-sm text-gray-500">
                  {format(new Date(action.startDate), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
                </span>
              </div>
              <p className="text-gray-900">{action.actionDescription}</p>
              <div className="flex items-center gap-2 mt-2 text-sm text-gray-500">
                <span>Por: {action.executedBy}</span>
                {action.completionDate && (
                  <>
                    <span>•</span>
                    <span>
                      Concluído: {format(new Date(action.completionDate), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
                    </span>
                  </>
                )}
              </div>
              {action.materialsUsed && action.materialsUsed.length > 0 && (
                <div className="mt-3 pt-3 border-t border-gray-100">
                  <p className="text-xs font-medium text-gray-500 mb-2">Materiais utilizados:</p>
                  <div className="flex flex-wrap gap-2">
                    {action.materialsUsed.map((material) => (
                      <span key={material.id} className="inline-flex items-center gap-1 px-2 py-1 bg-gray-100 rounded text-xs text-gray-700">
                        {material.itemName}: {material.quantity} {material.unitOfMeasure}
                      </span>
                    ))}
                  </div>
                </div>
              )}
            </div>
            <Link
              href={`/admin/maintenances/${maintenanceId}/actions/${action.id}`}
              className="p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100"
            >
              <Edit className="w-4 h-4" />
            </Link>
          </div>
        </div>
      ))}
    </div>
  );
}

interface PledgesListProps {
  pledges: PledgeResponseDto[];
  maintenanceId: string;
}

function PledgesList({ pledges, maintenanceId }: PledgesListProps) {
  if (pledges.length === 0) {
    return (
      <div className="text-center py-12 bg-gray-50 rounded-lg">
        <Heart className="w-12 h-12 text-gray-300 mx-auto mb-3" />
        <p className="text-gray-500">Nenhum pledge recebido</p>
        <Link
          href={`/admin/pledges/new?maintenanceId=${maintenanceId}`}
          className="inline-flex items-center gap-2 mt-3 px-4 py-2 bg-pink-600 text-white rounded-lg hover:bg-pink-700 transition-colors text-sm"
        >
          <Heart className="w-4 h-4" />
          Adicionar Pledge
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {pledges.map((pledge) => (
        <div key={pledge.id} className="bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <div className="flex items-center gap-2 mb-2">
                <PledgeStatusBadge status={pledge.status} />
                <span className="text-xs text-gray-400">
                  {format(new Date(pledge.createdAt), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
                </span>
              </div>
              <p className="font-medium text-gray-900">{pledge.volunteerName}</p>
              <p className="text-sm text-gray-500">{pledge.volunteerContact}</p>
              <p className="text-gray-700 mt-2">{pledge.description}</p>
              <div className="flex items-center gap-2 mt-2">
                <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                  pledge.type === 'MATERIAL' ? 'bg-purple-100 text-purple-700' : 'bg-cyan-100 text-cyan-700'
                }`}>
                  {pledge.type === 'MATERIAL' ? 'Material' : 'Mão de Obra'}
                </span>
              </div>
            </div>
            <div className="flex items-center gap-1">
              <Link
                href={`/admin/pledges/${pledge.id}`}
                className="p-2 text-gray-400 hover:text-gray-600 rounded-lg hover:bg-gray-100"
              >
                <Edit className="w-4 h-4" />
              </Link>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

interface EditFormProps {
  maintenance: MaintenanceResponseDto;
  onSave: (data: Partial<MaintenanceFormData>) => Promise<void>;
  onCancel: () => void;
}

function EditForm({ maintenance, onSave, onCancel }: EditFormProps) {
  const [formData, setFormData] = useState<MaintenanceFormData>({
    title: maintenance.title,
    description: maintenance.description,
    category: maintenance.category,
    scheduledDate: maintenance.scheduledDate ? maintenance.scheduledDate.split('T')[0] : '',
    status: maintenance.status,
  });
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateField = (field: keyof MaintenanceFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSave = async () => {
    setIsSaving(true);
    setError(null);
    try {
      await onSave(formData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao salvar');
    } finally {
      setIsSaving(false);
    }
  };

  const hasChanges = formData.title !== maintenance.title ||
    formData.description !== maintenance.description ||
    formData.category !== maintenance.category ||
    formData.status !== maintenance.status ||
    (formData.scheduledDate !== (maintenance.scheduledDate?.split('T')[0] || ''));

  return (
    <div className="max-w-3xl">
      {error && (
        <div className="mb-4 bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-600 text-sm">{error}</p>
        </div>
      )}

      <div className="grid gap-6">
        <div className="grid gap-4 sm:grid-cols-2">
        <div>
          <label className="block text-sm font-medium text-gray-900 mb-1">Status</label>
            <select
              value={formData.status}
              onChange={(e) => updateField('status', e.target.value as MaintenanceStatus)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-gray-900"
            >
              {statusOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-900 mb-1">Categoria</label>
            <select
              value={formData.category}
              onChange={(e) => updateField('category', e.target.value as MaintenanceCategory)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-gray-900"
            >
              {categoryOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-900 mb-1">Título</label>
          <input
            type="text"
            value={formData.title}
            onChange={(e) => updateField('title', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-400"
            placeholder="Título da manutenção"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-900 mb-1">Descrição</label>
          <textarea
            value={formData.description}
            onChange={(e) => updateField('description', e.target.value)}
            rows={4}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-400 resize-none"
            placeholder="Descrição detalhada do problema"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-900 mb-1">
            <Calendar className="w-4 h-4 inline mr-1" />
            Data Agendada
          </label>
          <input
            type="date"
            value={formData.scheduledDate}
            onChange={(e) => updateField('scheduledDate', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900"
          />
        </div>

        <div className="flex items-center justify-end gap-3 pt-4 border-t border-gray-200">
          <button
            onClick={onCancel}
            disabled={isSaving}
            className="inline-flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg text-gray-900 hover:bg-gray-50 transition-colors"
          >
            <X className="w-4 h-4" />
            Cancelar
          </button>
          <button
            onClick={handleSave}
            disabled={!hasChanges || isSaving}
            className="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isSaving ? (
              <Loader2 className="w-4 h-4 animate-spin" />
            ) : (
              <Save className="w-4 h-4" />
            )}
            Salvar Alterações
          </button>
        </div>
      </div>
    </div>
  );
}

export default function MaintenanceDetailPage() {
  const params = useParams();
  const router = useRouter();
  const maintenanceId = params.id as string;

  const [maintenance, setMaintenance] = useState<MaintenanceDetailResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<Tab>('details');

  const fetchMaintenance = useCallback(async () => {
    if (!maintenanceId) return;
    
    setLoading(true);
    setError(null);
    
    try {
      const data = await maintenanceApi.getDetail(maintenanceId);
      setMaintenance(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar manutenção');
    } finally {
      setLoading(false);
    }
  }, [maintenanceId]);

  useEffect(() => {
    fetchMaintenance();
  }, [fetchMaintenance]);

  const handleSaveEdit = async (data: Partial<MaintenanceFormData>) => {
    const updateDto: Record<string, any> = {};
    
    if (data.title !== undefined && data.title !== maintenance?.title) {
      updateDto.title = data.title;
    }
    if (data.description !== undefined && data.description !== maintenance?.description) {
      updateDto.description = data.description;
    }
    if (data.category !== undefined && data.category !== maintenance?.category) {
      updateDto.category = data.category;
    }
    if (data.status !== undefined && data.status !== maintenance?.status) {
      updateDto.status = data.status;
    }

    if (Object.keys(updateDto).length === 0) {
      setActiveTab('details');
      return;
    }

    const saved = await maintenanceApi.update(maintenanceId, updateDto);
    setMaintenance(prev => prev ? { ...prev, ...saved } : null);
    setActiveTab('details');
  };

  const tabs: { id: Tab; label: string; icon: React.ComponentType<{ className?: string }>; count?: number }[] = [
    { id: 'details', label: 'Detalhes', icon: Eye },
    { id: 'edit', label: 'Editar', icon: Edit },
    { id: 'actions', label: 'Ações', icon: ClipboardList, count: maintenance?.actions.length },
    { id: 'pledges', label: 'Pledges', icon: Heart, count: maintenance?.pledges.length },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
      </div>
    );
  }

  if (error && !maintenance) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-6">
        <div className="flex items-center gap-3 text-red-700">
          <AlertCircle className="w-6 h-6" />
          <div>
            <h3 className="font-medium">Erro ao carregar manutenção</h3>
            <p className="text-sm mt-1">{error}</p>
          </div>
        </div>
        <button
          onClick={fetchMaintenance}
          className="mt-4 inline-flex items-center gap-2 px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-colors"
        >
          <RefreshCw className="w-4 h-4" />
          Tentar novamente
        </button>
      </div>
    );
  }

  if (!maintenance) {
    return (
      <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
        <p className="text-yellow-700">Manutenção não encontrada</p>
        <Link href="/admin/maintenances" className="mt-4 inline-flex items-center gap-2 text-blue-600 hover:text-blue-700">
          <ArrowLeft className="w-4 h-4" />
          Voltar à lista
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Link
            href="/admin/maintenances"
            className="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <ArrowLeft className="w-5 h-5" />
          </Link>
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Manutenção</h2>
            <p className="text-gray-500 text-sm mt-1">
              {maintenance.title}
            </p>
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          <StatusBadge status={maintenance.status} />
          <CategoryBadge category={maintenance.category} />
        </div>
      </div>

      <div className="bg-white rounded-lg border border-gray-200">
        <div className="border-b border-gray-200">
          <nav className="flex -mb-px">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                  activeTab === tab.id
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <tab.icon className="w-4 h-4" />
                {tab.label}
                {tab.count !== undefined && tab.count > 0 && (
                  <span className={`ml-1 px-2 py-0.5 rounded-full text-xs ${
                    activeTab === tab.id ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-600'
                  }`}>
                    {tab.count}
                  </span>
                )}
              </button>
            ))}
          </nav>
        </div>

        <div className="p-6">
          {activeTab === 'details' && (
            <div className="max-w-3xl">
              <div className="grid gap-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">Informações Gerais</h3>
                  <dl className="grid gap-4 sm:grid-cols-2">
                    <div>
                      <dt className="text-sm font-medium text-gray-500">Status</dt>
                      <dd className="mt-1"><StatusBadge status={maintenance.status} /></dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">Categoria</dt>
                      <dd className="mt-1"><CategoryBadge category={maintenance.category} /></dd>
                    </div>
                  </dl>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-gray-500 mb-1">Título</h3>
                  <p className="text-gray-900 font-medium">{maintenance.title}</p>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-gray-500 mb-1">Descrição</h3>
                  <p className="text-gray-900">{maintenance.description}</p>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <h3 className="text-sm font-medium text-gray-500 mb-1">
                      <Calendar className="w-4 h-4 inline mr-1" />
                      Data Agendada
                    </h3>
                    <p className="text-gray-900">
                      {maintenance.scheduledDate 
                        ? format(new Date(maintenance.scheduledDate), 'dd/MM/yyyy', { locale: ptBR })
                        : '-'}
                    </p>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-gray-500 mb-1">
                      <Clock className="w-4 h-4 inline mr-1" />
                      Criado em
                    </h3>
                    <p className="text-gray-500 text-sm">
                      {format(new Date(maintenance.createdAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                    </p>
                  </div>
                </div>

                <div className="pt-4 border-t border-gray-200">
                  <p className="text-xs text-gray-400">
                    Última atualização: {format(new Date(maintenance.updatedAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                  </p>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'edit' && (
            <EditForm
              maintenance={maintenance}
              onSave={handleSaveEdit}
              onCancel={() => setActiveTab('details')}
            />
          )}

          {activeTab === 'actions' && (
            <MaintenanceActionsList actions={maintenance.actions} maintenanceId={maintenance.id} />
          )}

          {activeTab === 'pledges' && (
            <PledgesList pledges={maintenance.pledges} maintenanceId={maintenance.id} />
          )}
        </div>
      </div>
    </div>
  );
}