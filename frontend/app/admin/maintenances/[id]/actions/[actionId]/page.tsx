'use client';

import { useState, useEffect, useCallback } from 'react';
import { useParams } from 'next/navigation';
import Link from 'next/link';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import {
  ArrowLeft,
  Edit,
  Save,
  X,
  Clock,
  User,
  ClipboardList,
  RefreshCw,
  AlertCircle,
  CheckCircle,
  Loader2,
  Eye,
  Package
} from 'lucide-react';
import { 
  MaintenanceActionResponseDto, 
  ActionStatus 
} from '@/lib/types/maintenance';
import { maintenanceActionApi } from '@/lib/api/maintenance-action';
import { Button } from '@/components/ui/button';

const statusOptions: { value: ActionStatus; label: string; color: string }[] = [
  { value: 'SUCCESS', label: 'Sucesso', color: 'bg-green-100 text-green-700' },
  { value: 'PARTIAL_SUCCESS', label: 'Sucesso Parcial', color: 'bg-yellow-100 text-yellow-700' },
  { value: 'FAILURE', label: 'Falha', color: 'bg-red-100 text-red-700' },
];

type Tab = 'details' | 'edit';

function StatusBadge({ status }: { status: ActionStatus }) {
  const option = statusOptions.find(o => o.value === status);
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${option?.color || 'bg-gray-100 text-gray-700'}`}>
      {option?.label || status}
    </span>
  );
}

interface EditFormProps {
  action: MaintenanceActionResponseDto;
  onSave: (data: Partial<MaintenanceActionResponseDto>) => Promise<void>;
  onCancel: () => void;
}

function EditForm({ action, onSave, onCancel }: EditFormProps) {
  const [formData, setFormData] = useState({
    executedBy: action.executedBy,
    actionDescription: action.actionDescription,
    outcomeStatus: action.outcomeStatus,
    startDate: action.startDate ? action.startDate.split('T')[0] : '',
    completionDate: action.completionDate ? action.completionDate.split('T')[0] : '',
  });
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateField = (field: string, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSave = async () => {
    setIsSaving(true);
    setError(null);
    try {
      await onSave({
        executedBy: formData.executedBy,
        actionDescription: formData.actionDescription,
        outcomeStatus: formData.outcomeStatus as ActionStatus,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao salvar');
    } finally {
      setIsSaving(false);
    }
  };

  const hasChanges = formData.executedBy !== action.executedBy ||
    formData.actionDescription !== action.actionDescription ||
    formData.outcomeStatus !== action.outcomeStatus;

  return (
    <div className="max-w-3xl">
      {error && (
        <div className="mb-4 bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-600 text-sm">{error}</p>
        </div>
      )}

      <div className="grid gap-6">
        <div>
          <label className="block text-sm font-medium text-gray-900 mb-1">Status</label>
          <select
            value={formData.outcomeStatus}
            onChange={(e) => updateField('outcomeStatus', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-gray-900"
          >
            {statusOptions.map((opt) => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-900 mb-1">
            <User className="w-4 h-4 inline mr-1" />
            Executado Por
          </label>
          <input
            type="text"
            value={formData.executedBy}
            onChange={(e) => updateField('executedBy', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-400"
            placeholder="Nome de quem executou"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-900 mb-1">
            <ClipboardList className="w-4 h-4 inline mr-1" />
            Descrição da Ação
          </label>
          <textarea
            value={formData.actionDescription}
            onChange={(e) => updateField('actionDescription', e.target.value)}
            rows={4}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-400 resize-none"
            placeholder="Descrição detalhada da ação"
          />
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <div>
            <label className="block text-sm font-medium text-gray-900 mb-1">
              <Clock className="w-4 h-4 inline mr-1" />
              Data Início
            </label>
            <input
              type="date"
              value={formData.startDate}
              disabled
              className="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-100 text-gray-500 cursor-not-allowed"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-900 mb-1">
              <CheckCircle className="w-4 h-4 inline mr-1" />
              Data Conclusão
            </label>
            <input
              type="date"
              value={formData.completionDate}
              disabled
              className="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-100 text-gray-500 cursor-not-allowed"
            />
          </div>
        </div>

        <div className="flex items-center justify-end gap-3 pt-4 border-t border-gray-200">
          <Button variant="outline" onClick={onCancel} disabled={isSaving}>
            <X className="w-4 h-4" />
            Cancelar
          </Button>
          <Button onClick={handleSave} disabled={!hasChanges || isSaving}>
            {isSaving ? (
              <Loader2 className="w-4 h-4 animate-spin" />
            ) : (
              <Save className="w-4 h-4" />
            )}
            Salvar Alterações
          </Button>
        </div>
      </div>
    </div>
  );
}

export default function ActionDetailPage() {
  const params = useParams();
  const maintenanceId = params.id as string;
  const actionId = params.actionId as string;

  const [action, setAction] = useState<MaintenanceActionResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<Tab>('details');

  const fetchAction = useCallback(async () => {
    if (!maintenanceId || !actionId) return;
    
    setLoading(true);
    setError(null);
    
    try {
      const data = await maintenanceActionApi.getById(maintenanceId, actionId);
      setAction(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar ação');
    } finally {
      setLoading(false);
    }
  }, [maintenanceId, actionId]);

  useEffect(() => {
    fetchAction();
  }, [fetchAction]);

  const handleSaveEdit = async (data: Partial<MaintenanceActionResponseDto>) => {
    if (!action) return;

    const updateDto: any = {};
    
    if (data.outcomeStatus !== undefined && data.outcomeStatus !== action.outcomeStatus) {
      updateDto.outcomeStatus = data.outcomeStatus;
    }
    if (data.executedBy !== undefined && data.executedBy !== action.executedBy) {
      updateDto.executedBy = data.executedBy;
    }
    if (data.actionDescription !== undefined && data.actionDescription !== action.actionDescription) {
      updateDto.actionDescription = data.actionDescription;
    }

    if (Object.keys(updateDto).length === 0) {
      setActiveTab('details');
      return;
    }

    const saved = await maintenanceActionApi.update(maintenanceId, actionId, updateDto);
    setAction(prev => prev ? { ...prev, ...saved } : null);
    setActiveTab('details');
  };

  const tabs: { id: Tab; label: string; icon: React.ComponentType<{ className?: string }> }[] = [
    { id: 'details', label: 'Detalhes', icon: Eye },
    { id: 'edit', label: 'Editar', icon: Edit },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
      </div>
    );
  }

  if (error && !action) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-6">
        <div className="flex items-center gap-3 text-red-700">
          <AlertCircle className="w-6 h-6" />
          <div>
            <h3 className="font-medium">Erro ao carregar ação</h3>
            <p className="text-sm mt-1">{error}</p>
          </div>
        </div>
        <Button variant="destructive" onClick={fetchAction} className="mt-4">
          <RefreshCw className="w-4 h-4" />
          Tentar novamente
        </Button>
      </div>
    );
  }

  if (!action) {
    return (
      <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
        <p className="text-yellow-700">Ação não encontrada</p>
        <Link href="/admin/actions" className="mt-4 inline-flex items-center gap-2 text-blue-600 hover:text-blue-700">
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
          <Button variant="ghost" asChild>
            <Link href="/admin/actions">
              <ArrowLeft className="w-4 h-4" />
            </Link>
          </Button>
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Ação de Manutenção</h2>
            <p className="text-gray-500 text-sm mt-1 truncate max-w-md">
              {action.actionDescription}
            </p>
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          <StatusBadge status={action.outcomeStatus} />
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
              </button>
            ))}
          </nav>
        </div>

        <div className="p-6">
          {activeTab === 'details' && (
            <div className="max-w-3xl">
              <div className="grid gap-6">
                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <h3 className="text-sm font-medium text-gray-500 mb-1">Status</h3>
                    <dd className="mt-1"><StatusBadge status={action.outcomeStatus} /></dd>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-gray-500 mb-1">Manutenção</h3>
                    <Link 
                      href={`/admin/maintenances/${action.maintenanceId}`}
                      className="text-blue-600 hover:text-blue-700 text-sm"
                    >
                      {action.maintenanceTitle}
                    </Link>
                  </div>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-gray-500 mb-1">
                    <User className="w-4 h-4 inline mr-1" />
                    Executado Por
                  </h3>
                  <p className="text-gray-900 font-medium">{action.executedBy}</p>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-gray-500 mb-1">
                    <ClipboardList className="w-4 h-4 inline mr-1" />
                    Descrição da Ação
                  </h3>
                  <p className="text-gray-900">{action.actionDescription}</p>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <h3 className="text-sm font-medium text-gray-500 mb-1">
                      <Clock className="w-4 h-4 inline mr-1" />
                      Data Início
                    </h3>
                    <p className="text-gray-900">
                      {action.startDate 
                        ? format(new Date(action.startDate), 'dd/MM/yyyy HH:mm', { locale: ptBR })
                        : '-'}
                    </p>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-gray-500 mb-1">
                      <CheckCircle className="w-4 h-4 inline mr-1" />
                      Data Conclusão
                    </h3>
                    <p className="text-gray-900">
                      {action.completionDate 
                        ? format(new Date(action.completionDate), 'dd/MM/yyyy HH:mm', { locale: ptBR })
                        : '-'}
                    </p>
                  </div>
                </div>

                {action.materialsUsed && action.materialsUsed.length > 0 && (
                  <div>
                    <h3 className="text-sm font-medium text-gray-500 mb-3">
                      <Package className="w-4 h-4 inline mr-1" />
                      Materiais Utilizados
                    </h3>
                    <div className="bg-gray-50 rounded-lg p-4">
                      <table className="w-full">
                        <thead>
                          <tr className="text-left text-xs text-gray-500 uppercase">
                            <th className="pb-2">Material</th>
                            <th className="pb-2 text-right">Quantidade</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                          {action.materialsUsed.map((material) => (
                            <tr key={material.id}>
                              <td className="py-2 text-sm text-gray-900">{material.itemName}</td>
                              <td className="py-2 text-sm text-gray-900 text-right">
                                {material.quantity} {material.unitOfMeasure}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}

                <div className="pt-4 border-t border-gray-200">
                  <p className="text-xs text-gray-400">
                    Criado em: {format(new Date(action.createdAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                  </p>
                  <p className="text-xs text-gray-400">
                    Última atualização: {format(new Date(action.updatedAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                  </p>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'edit' && (
            <EditForm
              action={action}
              onSave={handleSaveEdit}
              onCancel={() => setActiveTab('details')}
            />
          )}
        </div>
      </div>
    </div>
  );
}
