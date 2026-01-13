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
  Heart,
  FileText,
  RefreshCw,
  AlertCircle,
  CheckCircle,
  Loader2,
  Eye
} from 'lucide-react';
import { 
  PledgeResponseDto, 
  PledgeFormData,
  PledgeStatus,
  PledgeCategory
} from '@/lib/types/maintenance';
import { pledgeApi } from '@/lib/api/pledge';
import { Button } from '@/components/ui/button';

const statusOptions: { value: PledgeStatus; label: string; color: string }[] = [
  { value: 'OFFERED', label: 'Oferecido', color: 'bg-blue-100 text-blue-700' },
  { value: 'PENDING', label: 'Pendente', color: 'bg-yellow-100 text-yellow-700' },
  { value: 'REJECTED', label: 'Rejeitado', color: 'bg-red-100 text-red-700' },
  { value: 'COMPLETED', label: 'Concluído', color: 'bg-green-100 text-green-700' },
  { value: 'CANCELED', label: 'Cancelado', color: 'bg-gray-100 text-gray-700' },
];

const typeOptions: { value: PledgeCategory; label: string; icon: string }[] = [
  { value: 'MATERIAL', label: 'Material', icon: '📦' },
  { value: 'LABOR', label: 'Mão de Obra', icon: '👷' },
];

type Tab = 'details' | 'edit';

function StatusBadge({ status }: { status: PledgeStatus }) {
  const option = statusOptions.find(o => o.value === status);
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-xs text-sm font-medium ${option?.color || 'bg-gray-100 text-gray-700'}`}>
      {option?.label || status}
    </span>
  );
}

function TypeBadge({ type }: { type: PledgeCategory }) {
  const option = typeOptions.find(o => o.value === type);
  return (
    <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-xs text-sm font-medium bg-purple-100 text-purple-700">
      <span>{option?.icon}</span>
      {option?.label || type}
    </span>
  );
}

interface EditFormProps {
  pledge: PledgeResponseDto;
  onSave: (data: Partial<PledgeFormData>) => Promise<void>;
  onCancel: () => void;
}

function EditForm({ pledge, onSave, onCancel }: EditFormProps) {
  const [formData, setFormData] = useState<PledgeFormData>({
    volunteerName: pledge.volunteerName,
    volunteerContact: pledge.volunteerContact,
    description: pledge.description,
    type: pledge.type,
    status: pledge.status,
  });
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateField = (field: keyof PledgeFormData, value: string) => {
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

  const hasChanges = formData.volunteerName !== pledge.volunteerName ||
    formData.volunteerContact !== pledge.volunteerContact ||
    formData.description !== pledge.description ||
    formData.type !== pledge.type ||
    formData.status !== pledge.status;

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
              onChange={(e) => updateField('status', e.target.value as PledgeStatus)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-gray-900"
            >
              {statusOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-900 mb-1">Tipo</label>
            <select
              value={formData.type}
              onChange={(e) => updateField('type', e.target.value as PledgeCategory)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white text-gray-900"
            >
              {typeOptions.map((opt) => (
                <option key={opt.value} value={opt.value}>{opt.label}</option>
              ))}
            </select>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-900 mb-1">
            <User className="w-4 h-4 inline mr-1" />
            Nome do Voluntário
          </label>
          <input
            type="text"
            value={formData.volunteerName}
            onChange={(e) => updateField('volunteerName', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-400"
            placeholder="Nome do voluntário"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-900 mb-1">Contato</label>
          <input
            type="text"
            value={formData.volunteerContact}
            onChange={(e) => updateField('volunteerContact', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-400"
            placeholder="Email ou telefone"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-900 mb-1">
            <FileText className="w-4 h-4 inline mr-1" />
            Descrição
          </label>
          <textarea
            value={formData.description}
            onChange={(e) => updateField('description', e.target.value)}
            rows={4}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 placeholder-gray-400 resize-none"
            placeholder="Descrição do pledge"
          />
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

export default function PledgeDetailPage() {
  const params = useParams();
  const pledgeId = params.id as string;

  const [pledge, setPledge] = useState<PledgeResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<Tab>('details');

  const fetchPledge = useCallback(async () => {
    if (!pledgeId) return;
    
    setLoading(true);
    setError(null);
    
    try {
      const data = await pledgeApi.getById(pledgeId);
      setPledge(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar pledge');
    } finally {
      setLoading(false);
    }
  }, [pledgeId]);

  useEffect(() => {
    fetchPledge();
  }, [fetchPledge]);

  const handleSaveEdit = async (data: Partial<PledgeFormData>) => {
    const updateDto: Record<string, any> = {};
    
    if (data.status !== undefined && data.status !== pledge?.status) {
      updateDto.status = data.status;
    }
    if (data.description !== undefined && data.description !== pledge?.description) {
      updateDto.description = data.description;
    }
    if (data.volunteerName !== undefined && data.volunteerName !== pledge?.volunteerName) {
      updateDto.volunteerName = data.volunteerName;
    }
    if (data.volunteerContact !== undefined && data.volunteerContact !== pledge?.volunteerContact) {
      updateDto.volunteerContact = data.volunteerContact;
    }
    if (data.type !== undefined && data.type !== pledge?.type) {
      updateDto.type = data.type;
    }

    if (Object.keys(updateDto).length === 0) {
      setActiveTab('details');
      return;
    }

    const saved = await pledgeApi.update(pledgeId, updateDto);
    setPledge(prev => prev ? { ...prev, ...saved } : null);
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

  if (error && !pledge) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-6">
        <div className="flex items-center gap-3 text-red-700">
          <AlertCircle className="w-6 h-6" />
          <div>
            <h3 className="font-medium">Erro ao carregar pledge</h3>
            <p className="text-sm mt-1">{error}</p>
          </div>
        </div>
        <Button variant="destructive" onClick={fetchPledge} className="mt-4">
          <RefreshCw className="w-4 h-4" />
          Tentar novamente
        </Button>
      </div>
    );
  }

  if (!pledge) {
    return (
      <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
        <p className="text-yellow-700">Pledge não encontrado</p>
        <Link href="/admin/pledges" className="mt-4 inline-flex items-center gap-2 text-blue-600 hover:text-blue-700">
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
            <Link href="/admin/pledges">
              <ArrowLeft className="w-4 h-4" />
            </Link>
          </Button>
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Pledge</h2>
            <p className="text-gray-500 text-sm mt-1">
              {pledge.volunteerName}
            </p>
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          <StatusBadge status={pledge.status} />
          <TypeBadge type={pledge.type} />
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
                    <dd className="mt-1"><StatusBadge status={pledge.status} /></dd>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-gray-500 mb-1">Tipo</h3>
                    <dd className="mt-1"><TypeBadge type={pledge.type} /></dd>
                  </div>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-gray-500 mb-1">
                    <User className="w-4 h-4 inline mr-1" />
                    Nome do Voluntário
                  </h3>
                  <p className="text-gray-900 font-medium">{pledge.volunteerName}</p>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-gray-500 mb-1">Contato</h3>
                  <p className="text-gray-900">{pledge.volunteerContact}</p>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-gray-500 mb-1">
                    <FileText className="w-4 h-4 inline mr-1" />
                    Descrição
                  </h3>
                  <p className="text-gray-900">{pledge.description}</p>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <h3 className="text-sm font-medium text-gray-500 mb-1">
                      <Clock className="w-4 h-4 inline mr-1" />
                      Criado em
                    </h3>
                    <p className="text-gray-500 text-sm">
                      {format(new Date(pledge.createdAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                    </p>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-gray-500 mb-1">
                      <Clock className="w-4 h-4 inline mr-1" />
                      Última atualização
                    </h3>
                    <p className="text-gray-500 text-sm">
                      {format(new Date(pledge.updatedAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'edit' && (
            <EditForm
              pledge={pledge}
              onSave={handleSaveEdit}
              onCancel={() => setActiveTab('details')}
            />
          )}
        </div>
      </div>
    </div>
  );
}
