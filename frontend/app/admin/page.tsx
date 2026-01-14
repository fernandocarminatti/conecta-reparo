import {
  Wrench,
  Heart,
  ClipboardList,
  TrendingUp,
  Clock,
  CheckCircle,
  AlertCircle,
  ArrowRight,
  Plus
} from 'lucide-react';
import Link from 'next/link';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';

interface StatCardProps {
  title: string;
  value: string | number;
  change?: string;
  changeType?: 'positive' | 'negative' | 'neutral';
  icon: React.ComponentType<{ className?: string }>;
  iconColor: string;
}

function StatCard({ title, value, change, changeType = 'neutral', icon: Icon, iconColor }: StatCardProps) {
  return (
    <Card className="hover:shadow-lg transition-shadow">
      <CardContent className="pt-6">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-muted-foreground">{title}</p>
            <p className="text-3xl font-bold text-foreground mt-2">{value}</p>
            {change && (
              <p className={`text-sm mt-2 flex items-center gap-1 ${
                changeType === 'positive' ? 'text-green-600' : 
                changeType === 'negative' ? 'text-red-600' : 'text-muted-foreground'
              }`}>
                <TrendingUp className={`w-4 h-4 ${changeType === 'negative' ? 'rotate-180' : ''}`} />
                {change}
              </p>
            )}
          </div>
          <div className={`w-12 h-12 rounded-lg flex items-center justify-center ${iconColor}`}>
            <Icon className="w-6 h-6 text-white" />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

interface RecentActivity {
  id: string;
  type: 'maintenance' | 'pledge' | 'action';
  title: string;
  description: string;
  time: string;
  status: 'pending' | 'completed' | 'in_progress';
}

const recentActivities: RecentActivity[] = [
  {
    id: '1',
    type: 'maintenance',
    title: 'Nova solicitação de manutenção',
    description: 'UBS do Bairro Norte reportou problema hidráulico',
    time: 'há 2 horas',
    status: 'pending',
  },
  {
    id: '2',
    type: 'pledge',
    title: 'Pledge recebido',
    description: 'Material de construção doado por empresa local',
    time: 'há 4 horas',
    status: 'completed',
  },
  {
    id: '3',
    type: 'action',
    title: 'Ação concluída',
    description: 'Reparo elétricos na UBS Centro',
    time: 'há 1 dia',
    status: 'completed',
  },
  {
    id: '4',
    type: 'maintenance',
    title: 'Manutenção em andamento',
    description: 'Pintura externa da UBS Oeste',
    time: 'há 2 dias',
    status: 'in_progress',
  },
];

function ActivityIcon({ type }: { type: RecentActivity['type'] }) {
  const icons = {
    maintenance: Wrench,
    pledge: Heart,
    action: ClipboardList,
  };
  const colors = {
    maintenance: 'bg-maintenance-light text-maintenance',
    pledge: 'bg-pledge-light text-pledge',
    action: 'bg-action-light text-action',
  };
  const Icon = icons[type];
  return (
    <div className={`w-10 h-10 rounded-md flex items-center justify-center ${colors[type]}`}>
      <Icon className="w-5 h-5" />
    </div>
  );
}

const STATUS_CONFIG: Record<string, { variant: "default" | "secondary" | "destructive" | "success" | "warning" | "outline"; label: string }> = {
  pending: { variant: "warning", label: "Pendente" },
  completed: { variant: "success", label: "Concluído" },
  in_progress: { variant: "default", label: "Em Andamento" },
}

export default function AdminDashboard() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-foreground">Dashboard</h2>
          <p className="text-muted-foreground mt-1">Visão geral do sistema de manutenção</p>
        </div>
        <Button asChild>
          <Link href="/admin/maintenances/form">
            <Plus className="w-4 h-4" />
            Nova Manutenção
          </Link>
        </Button>
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Total de Manutenções"
          value={24}
          change="+12% este mês"
          changeType="positive"
          icon={Wrench}
          iconColor="bg-maintenance"
        />
        <StatCard
          title="Ofertas Ativas"
          value={18}
          change="+5 esta semana"
          changeType="positive"
          icon={Heart}
          iconColor="bg-pledge"
        />
        <StatCard
          title="Ações Concluídas"
          value={156}
          change="+23 este mês"
          changeType="positive"
          icon={ClipboardList}
          iconColor="bg-action"
        />
        <StatCard
          title="Taxa de Conclusão"
          value="87%"
          change="+3% vs último mês"
          changeType="positive"
          icon={TrendingUp}
          iconColor="bg-green-600"
        />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-foreground">Atividades Recentes</h3>
              <Link href="/admin/history" className="text-sm text-primary hover:text-primary/80 flex items-center gap-1">
                Ver todas
                <ArrowRight className="w-4 h-4" />
              </Link>
            </div>
              <div className="divide-y divide-border -mx-6 px-6">
                {recentActivities.map((activity) => (
                  <div key={activity.id} className="py-4 first:pt-0 last:pb-0 hover:bg-muted transition-colors -mx-4 px-4">
                    <div className="flex items-start gap-4">
                      <ActivityIcon type={activity.type} />
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-foreground">{activity.title}</p>
                        <p className="text-sm text-muted-foreground mt-0.5">{activity.description}</p>
                        <div className="flex items-center gap-2 mt-2">
                          <Badge variant={STATUS_CONFIG[activity.status].variant}>
                            {activity.status === 'completed' && <CheckCircle className="w-3 h-3 mr-1" />}
                            {activity.status === 'pending' && <Clock className="w-3 h-3 mr-1" />}
                            {activity.status === 'in_progress' && <AlertCircle className="w-3 h-3 mr-1" />}
                            {STATUS_CONFIG[activity.status].label}
                          </Badge>
                          <span className="text-xs text-muted-foreground">{activity.time}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
          </CardContent>
        </Card>

          <Card>
            <CardContent className="pt-6">
              <h3 className="text-lg font-semibold text-foreground mb-4">Ações Rápidas</h3>
              <div className="grid gap-4 sm:grid-cols-2 -mx-2 px-2">
                <Link
                  href="/admin/maintenances/form"
                  className="flex items-center gap-4 p-4 border border-border rounded-lg hover:bg-muted transition-colors group"
                >
                  <div className="w-10 h-10 bg-maintenance-light rounded-lg flex items-center justify-center group-hover:bg-maintenance-light-hover transition-colors">
                    <Plus className="w-5 h-5 text-maintenance" />
                  </div>
                  <div>
                    <p className="font-medium text-foreground">Nova Manutenção</p>
                    <p className="text-sm text-muted-foreground">Criar solicitação</p>
                  </div>
                </Link>
                <Link
                  href="/admin/pledges"
                  className="flex items-center gap-4 p-4 border border-border rounded-lg hover:bg-muted transition-colors group"
                >
                  <div className="w-10 h-10 bg-pledge-light rounded-lg flex items-center justify-center group-hover:bg-pledge-light-hover transition-colors">
                    <Heart className="w-5 h-5 text-pledge" />
                  </div>
                  <div>
                    <p className="font-medium text-foreground">Gerenciar Pledges</p>
                    <p className="text-sm text-muted-foreground">Ver doações</p>
                  </div>
                </Link>
                <Link
                  href="/admin/actions"
                  className="flex items-center gap-4 p-4 border border-border rounded-lg hover:bg-muted transition-colors group"
                >
                  <div className="w-10 h-10 bg-action-light rounded-lg flex items-center justify-center group-hover:bg-action-light-hover transition-colors">
                    <ClipboardList className="w-5 h-5 text-action" />
                  </div>
                  <div>
                    <p className="font-medium text-foreground">Ver Ações</p>
                    <p className="text-sm text-muted-foreground">Todas as ações</p>
                  </div>
                </Link>
                <Link
                  href="/admin/history"
                  className="flex items-center gap-4 p-4 border border-border rounded-lg hover:bg-muted transition-colors group"
                >
                  <div className="w-10 h-10 bg-secondary rounded-lg flex items-center justify-center group-hover:bg-secondary/80 transition-colors">
                    <Clock className="w-5 h-5 text-secondary-foreground" />
                  </div>
                  <div>
                    <p className="font-medium text-foreground">Histórico</p>
                    <p className="text-sm text-muted-foreground">Ver registros</p>
                  </div>
                </Link>
              </div>
            </CardContent>
          </Card>
      </div>
    </div>
  );
}
