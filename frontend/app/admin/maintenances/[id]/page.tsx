'use client';

import { useState, useEffect, useCallback } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import {
  ArrowLeft,
  Edit,
  Clock,
  Calendar,
  ClipboardList,
  Heart,
  RefreshCw,
  AlertCircle,
  Loader2,
  Eye
} from 'lucide-react';
import { 
  MaintenanceResponseDto, 
  MaintenanceDetailResponseDto,
  MaintenanceActionResponseDto,
  MaintenanceStatus,
  MaintenanceCategory,
  ActionStatus
} from '@/lib/types/maintenance';
import { 
  PledgeResponseDto,
  PledgeStatus
} from '@/lib/types/pledges';
import { maintenanceApi } from '@/lib/api/maintenance';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { 
  MAINTENANCE_STATUS_CONFIG, 
  PLEDGE_STATUS_CONFIG, 
  ACTION_STATUS_CONFIG,
  CATEGORY_CONFIG 
} from '@/lib/config/status-config';

type Tab = 'details' | 'edit' | 'actions' | 'pledges';

function MaintenanceStatusBadge({ status }: { status: MaintenanceStatus }) {
  const config = MAINTENANCE_STATUS_CONFIG[status];
  return <Badge variant={config?.variant}>{config?.label}</Badge>;
}

function CategoryBadge({ category }: { category: MaintenanceCategory }) {
  const config = CATEGORY_CONFIG[category];
  return <Badge variant="secondary">{config?.label}</Badge>;
}

function ActionStatusBadge({ status }: { status: ActionStatus }) {
  const config = ACTION_STATUS_CONFIG[status];
  return <Badge variant={config?.variant}>{config?.label}</Badge>;
}

function PledgeStatusBadge({ status }: { status: PledgeStatus }) {
  const config = PLEDGE_STATUS_CONFIG[status];
  return <Badge variant={config?.variant}>{config?.label}</Badge>;
}

interface MaintenanceActionsListProps {
  actions: MaintenanceActionResponseDto[];
  maintenanceId: string;
}

function MaintenanceActionsList({ actions, maintenanceId }: MaintenanceActionsListProps) {
  if (actions.length === 0) {
    return (
      <div className="text-center py-12 bg-muted/50 rounded-lg">
        <ClipboardList className="w-12 h-12 text-gray-300 mx-auto mb-3" />
        <p className="text-muted-foreground">Nenhuma ação registrada</p>
        <Button asChild className="mt-3">
          <Link href={`/admin/maintenances/${maintenanceId}/actions/new`}>
            <ClipboardList className="w-4 h-4" />
            Nova Ação
          </Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {actions.map((action) => (
        <Card key={action.id} className="hover:shadow-md transition-shadow p-4">
          <CardContent className="pt-4 p-0">
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-2">
                  <ActionStatusBadge status={action.outcomeStatus} />
                  <span className="text-sm text-muted-foreground">
                    {format(new Date(action.startDate), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
                  </span>
                </div>
                <p className="text-foreground">{action.actionDescription}</p>
                <div className="flex items-center gap-2 mt-2 text-sm text-muted-foreground">
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
                  <div className="mt-3 pt-3 border-t border-border">
                    <p className="text-xs font-medium text-muted-foreground mb-2">Materiais utilizados:</p>
                    <div className="flex flex-wrap gap-2">
                      {action.materialsUsed.map((material) => (
                        <span key={material.id} className="inline-flex items-center gap-1 px-2 py-1 bg-secondary rounded text-xs text-foreground">
                          {material.itemName}: {material.quantity} {material.unitOfMeasure}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>
              <Link
                href={`/admin/maintenances/${maintenanceId}/actions/${action.id}`}
                className="p-2 text-muted-foreground hover:text-foreground hover:bg-secondary"
              >
                <Edit className="w-4 h-4" />
              </Link>
            </div>
          </CardContent>
        </Card>
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
      <div className="text-center py-12 bg-muted/50 rounded-lg">
        <Heart className="w-12 h-12 text-gray-300 mx-auto mb-3" />
        <p className="text-muted-foreground">Nenhuma oferta recebida</p>
        <Button asChild className="mt-3">
          <Link href={`/admin/pledges/new?maintenanceId=${maintenanceId}`}>
            <Heart className="w-4 h-4" />
            Adicionar Oferta
          </Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {pledges.map((pledge) => (
        <Card key={pledge.id} className="hover:shadow-md transition-shadow p-4">
          <CardContent className="pt-4 p-0">
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-2">
                  <PledgeStatusBadge status={pledge.status} />
                  <span className="text-xs text-muted-foreground">
                    {format(new Date(pledge.createdAt), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
                  </span>
                </div>
                <p className="font-medium text-foreground">{pledge.volunteerName}</p>
                <p className="text-sm text-muted-foreground">{pledge.volunteerContact}</p>
                <p className="text-foreground mt-2">{pledge.description}</p>
                <div className="flex items-center gap-2 mt-2">
                  <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                    pledge.type === 'MATERIAL' ? 'bg-secondary text-foreground' : 'bg-secondary text-foreground'
                  }`}>
                    {pledge.type === 'MATERIAL' ? 'Material' : 'Mão de Obra'}
                  </span>
                </div>
              </div>
              <div className="flex items-center gap-1">
                <Link
                  href={`/admin/pledges/${pledge.id}`}
                  className="p-2 text-muted-foreground hover:text-foreground hover:bg-secondary"
                >
                  <Edit className="w-4 h-4" />
                </Link>
              </div>
            </div>
          </CardContent>
        </Card>
      ))}
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

  const tabs: { id: Tab; label: string; icon: React.ComponentType<{ className?: string }>; count?: number }[] = [
    { id: 'details', label: 'Detalhes', icon: Eye },
    { id: 'actions', label: 'Ações', icon: ClipboardList, count: maintenance?.actions.length },
    { id: 'pledges', label: 'Ofertas', icon: Heart, count: maintenance?.pledges.length },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <Loader2 className="w-8 h-8 animate-spin text-primary" />
      </div>
    );
  }

  if (error && !maintenance) {
    return (
      <div className="bg-destructive/10 border border-destructive/20 rounded-lg p-6">
        <div className="flex items-center gap-3 text-destructive">
          <AlertCircle className="w-6 h-6" />
          <div>
            <h3 className="font-medium">Erro ao carregar manutenção</h3>
            <p className="text-sm mt-1">{error}</p>
          </div>
        </div>
        <Button variant="destructive" onClick={fetchMaintenance} className="mt-4">
          <RefreshCw className="w-4 h-4" />
          Tentar novamente
        </Button>
      </div>
    );
  }

  if (!maintenance) {
    return (
      <div className="bg-warning/10 border border-warning/20 rounded-lg p-6">
        <p className="text-warning">Manutenção não encontrada</p>
        <Link href="/admin/maintenances" className="mt-4 inline-flex items-center gap-2 text-primary hover:text-primary/80">
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
            <Link href="/admin/maintenances">
              <ArrowLeft className="w-4 h-4" />
            </Link>
          </Button>
          <div>
            <h2 className="text-2xl font-bold text-foreground">Manutenção</h2>
            <p className="text-muted-foreground text-sm mt-1">
              {maintenance.title}
            </p>
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          <MaintenanceStatusBadge status={maintenance.status} />
          <CategoryBadge category={maintenance.category} />
          <Button variant="outline" size="sm" asChild>
            <Link href={`/admin/maintenances/form?id=${maintenance.id}&mode=edit`}>
              <Edit className="w-4 h-4 mr-1" />
              Editar
            </Link>
          </Button>
        </div>
      </div>

      <Card>
        <CardContent className="pt-6 p-0">
          <div className="border-b border-border pb-px">
            <nav className="flex -mb-px">
              {tabs.map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center gap-2 px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                    activeTab === tab.id
                      ? 'border-primary text-primary'
                      : 'border-transparent text-muted-foreground hover:text-foreground hover:border-border'
                  }`}
                >
                  <tab.icon className="w-4 h-4" />
                  {tab.label}
                  {tab.count !== undefined && tab.count > 0 && (
                    <span className={`ml-1 px-2 py-0.5 rounded-full text-xs ${
                      activeTab === tab.id ? 'bg-primary/10 text-primary' : 'bg-secondary text-secondary-foreground'
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
                    <h3 className="text-lg font-semibold text-foreground mb-2">Informações Gerais</h3>
                    <dl className="grid gap-4 sm:grid-cols-2">
                      <div>
                        <dt className="text-sm font-medium text-muted-foreground">Status</dt>
                        <dd className="mt-1"><MaintenanceStatusBadge status={maintenance.status} /></dd>
                      </div>
                      <div>
                        <dt className="text-sm font-medium text-muted-foreground">Categoria</dt>
                        <dd className="mt-1"><CategoryBadge category={maintenance.category} /></dd>
                      </div>
                    </dl>
                  </div>

                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground mb-1">Título</h3>
                    <p className="text-foreground font-medium">{maintenance.title}</p>
                  </div>

                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground mb-1">Descrição</h3>
                    <p className="text-foreground">{maintenance.description}</p>
                  </div>

                  <div className="grid gap-4 sm:grid-cols-2">
                    <div>
                      <h3 className="text-sm font-medium text-muted-foreground mb-1">
                        <Calendar className="w-4 h-4 inline mr-1" />
                        Data Agendada
                      </h3>
                      <p className="text-foreground">
                        {maintenance.scheduledDate 
                          ? format(new Date(maintenance.scheduledDate), 'dd/MM/yyyy', { locale: ptBR })
                          : '-'}
                      </p>
                    </div>
                    <div>
                      <h3 className="text-sm font-medium text-muted-foreground mb-1">
                        <Clock className="w-4 h-4 inline mr-1" />
                        Criado em
                      </h3>
                      <p className="text-muted-foreground text-sm">
                        {format(new Date(maintenance.createdAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                      </p>
                    </div>
                  </div>

                  <div className="pt-4 border-t border-border">
                    <p className="text-xs text-muted-foreground">
                      Última atualização: {format(new Date(maintenance.updatedAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                    </p>
                  </div>
                </div>
              </div>
            )}

            {activeTab === 'actions' && (
              <MaintenanceActionsList actions={maintenance.actions} maintenanceId={maintenance.id} />
            )}

            {activeTab === 'pledges' && (
              <PledgesList pledges={maintenance.pledges} maintenanceId={maintenance.id} />
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
