'use client';

import { useState, useEffect, useCallback } from 'react';
import { useParams } from 'next/navigation';
import Link from 'next/link';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import {
  ArrowLeft,
  Edit,
  RefreshCw,
  AlertCircle,
  Loader2,
  Eye,
  User,
  FileText,
  Clock,
} from 'lucide-react';
import { 
  PledgeResponseDto, 
} from '@/lib/types/pledges';
import { pledgeApi } from '@/lib/api/pledge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { PLEDGE_STATUS_CONFIG } from '@/lib/config/status-config';

function StatusBadge({ status }: { status: string }) {
  const config = PLEDGE_STATUS_CONFIG[status as keyof typeof PLEDGE_STATUS_CONFIG];
  return (
    <Badge variant={config?.variant || 'secondary'}>
      {config?.label || status}
    </Badge>
  );
}

function TypeBadge({ type }: { type: string }) {
  return (
    <Badge variant="outline">
      {type === 'MATERIAL' ? 'Material' : 'Mão de Obra'}
    </Badge>
  );
}

export default function PledgeDetailPage() {
  const params = useParams();
  const pledgeId = params.id as string;

  const [pledge, setPledge] = useState<PledgeResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPledge = useCallback(async () => {
    if (!pledgeId) return;
    
    setLoading(true);
    setError(null);
    
    try {
      const data = await pledgeApi.getById(pledgeId);
      setPledge(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar oferta');
    } finally {
      setLoading(false);
    }
  }, [pledgeId]);

  useEffect(() => {
    fetchPledge();
  }, [fetchPledge]);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <Loader2 className="w-8 h-8 animate-spin text-primary" />
      </div>
    );
  }

  if (error && !pledge) {
    return (
      <div className="bg-destructive/10 border border-destructive/20 rounded-lg p-6">
        <div className="flex items-center gap-3 text-destructive">
          <AlertCircle className="w-6 h-6" />
          <div>
            <h3 className="font-medium">Erro ao carregar oferta</h3>
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
        <p className="text-yellow-700">Oferta não encontrada</p>
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
            <h2 className="text-2xl font-bold text-foreground">Oferta</h2>
            <p className="text-muted-foreground text-sm mt-1">
              {pledge.volunteerName}
            </p>
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          <StatusBadge status={pledge.status} />
          <TypeBadge type={pledge.type} />
          <Button variant="outline" size="sm" asChild>
            <Link href={`/admin/pledges/form?id=${pledge.id}&mode=edit`}>
              <Edit className="w-4 h-4" />
              Editar
            </Link>
          </Button>
        </div>
      </div>

      <Card>
        <CardContent className="pt-6 p-0">
          <div className="p-6">
            <div className="max-w-3xl">
              <div className="grid gap-6">
                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground mb-1">Status</h3>
                    <dd className="mt-1"><StatusBadge status={pledge.status} /></dd>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground mb-1">Tipo</h3>
                    <dd className="mt-1"><TypeBadge type={pledge.type} /></dd>
                  </div>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-muted-foreground mb-1">
                    <User className="w-4 h-4 inline mr-1" />
                    Nome do Voluntário
                  </h3>
                  <p className="text-foreground font-medium">{pledge.volunteerName}</p>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-muted-foreground mb-1">Contato</h3>
                  <p className="text-foreground">{pledge.volunteerContact}</p>
                </div>

                <div>
                  <h3 className="text-sm font-medium text-muted-foreground mb-1">
                    <FileText className="w-4 h-4 inline mr-1" />
                    Descrição
                  </h3>
                  <p className="text-foreground">{pledge.description}</p>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground mb-1">
                      <Clock className="w-4 h-4 inline mr-1" />
                      Criado em
                    </h3>
                    <p className="text-muted-foreground text-sm">
                      {format(new Date(pledge.createdAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                    </p>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground mb-1">
                      <Clock className="w-4 h-4 inline mr-1" />
                      Última atualização
                    </h3>
                    <p className="text-muted-foreground text-sm">
                      {format(new Date(pledge.updatedAt), "dd/MM/yyyy 'às' HH:mm", { locale: ptBR })}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
