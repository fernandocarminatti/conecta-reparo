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
    <div className="bg-white rounded-xl border border-gray-200 p-6 hover:shadow-lg transition-shadow">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-500">{title}</p>
          <p className="text-3xl font-bold text-gray-900 mt-2">{value}</p>
          {change && (
            <p className={`text-sm mt-2 flex items-center gap-1 ${
              changeType === 'positive' ? 'text-green-600' : 
              changeType === 'negative' ? 'text-red-600' : 'text-gray-500'
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
    </div>
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
    maintenance: 'bg-blue-100 text-blue-600',
    pledge: 'bg-pink-100 text-pink-600',
    action: 'bg-purple-100 text-purple-600',
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
          <h2 className="text-2xl font-bold text-gray-900">Dashboard</h2>
          <p className="text-gray-500 mt-1">Visão geral do sistema de manutenção</p>
        </div>
        <Button asChild>
          <Link href="/admin/maintenances/new">
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
          iconColor="bg-blue-600"
        />
        <StatCard
          title="Ofertas Ativas"
          value={18}
          change="+5 esta semana"
          changeType="positive"
          icon={Heart}
          iconColor="bg-pink-500"
        />
        <StatCard
          title="Ações Concluídas"
          value={156}
          change="+23 este mês"
          changeType="positive"
          icon={ClipboardList}
          iconColor="bg-purple-600"
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
        <div className="bg-white rounded-xl border border-gray-200">
          <div className="flex items-center justify-between p-6 border-b border-gray-200">
            <h3 className="text-lg font-semibold text-gray-900">Atividades Recentes</h3>
            <Link href="/admin/history" className="text-sm text-blue-600 hover:text-blue-700 flex items-center gap-1">
              Ver todas
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
          <div className="divide-y divide-gray-100">
            {recentActivities.map((activity) => (
              <div key={activity.id} className="p-4 hover:bg-gray-50 transition-colors">
                <div className="flex items-start gap-4">
                  <ActivityIcon type={activity.type} />
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900">{activity.title}</p>
                    <p className="text-sm text-gray-500 mt-0.5">{activity.description}</p>
                    <div className="flex items-center gap-2 mt-2">
                      <Badge variant={STATUS_CONFIG[activity.status].variant}>
                        {activity.status === 'completed' && <CheckCircle className="w-3 h-3 mr-1" />}
                        {activity.status === 'pending' && <Clock className="w-3 h-3 mr-1" />}
                        {activity.status === 'in_progress' && <AlertCircle className="w-3 h-3 mr-1" />}
                        {STATUS_CONFIG[activity.status].label}
                      </Badge>
                      <span className="text-xs text-gray-400">{activity.time}</span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-xl border border-gray-200">
          <div className="flex items-center justify-between p-6 border-b border-gray-200">
            <h3 className="text-lg font-semibold text-gray-900">Ações Rápidas</h3>
          </div>
          <div className="p-6 grid gap-4 sm:grid-cols-2">
            <Link
              href="/admin/maintenances/new"
              className="flex items-center gap-4 p-4 border border-gray-200 rounded-lg hover:border-blue-300 hover:bg-blue-50 transition-colors group"
            >
              <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center group-hover:bg-blue-200 transition-colors">
                <Plus className="w-5 h-5 text-blue-600" />
              </div>
              <div>
                <p className="font-medium text-gray-900">Nova Manutenção</p>
                <p className="text-sm text-gray-500">Criar solicitação</p>
              </div>
            </Link>
            <Link
              href="/admin/pledges"
              className="flex items-center gap-4 p-4 border border-gray-200 rounded-lg hover:border-pink-300 hover:bg-pink-50 transition-colors group"
            >
              <div className="w-10 h-10 bg-pink-100 rounded-lg flex items-center justify-center group-hover:bg-pink-200 transition-colors">
                <Heart className="w-5 h-5 text-pink-600" />
              </div>
              <div>
                <p className="font-medium text-gray-900">Gerenciar Pledges</p>
                <p className="text-sm text-gray-500">Ver doações</p>
              </div>
            </Link>
            <Link
              href="/admin/actions"
              className="flex items-center gap-4 p-4 border border-gray-200 rounded-lg hover:border-purple-300 hover:bg-purple-50 transition-colors group"
            >
              <div className="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center group-hover:bg-purple-200 transition-colors">
                <ClipboardList className="w-5 h-5 text-purple-600" />
              </div>
              <div>
                <p className="font-medium text-gray-900">Ver Ações</p>
                <p className="text-sm text-gray-500">Todas as ações</p>
              </div>
            </Link>
            <Link
              href="/admin/history"
              className="flex items-center gap-4 p-4 border border-gray-200 rounded-lg hover:border-gray-300 hover:bg-gray-50 transition-colors group"
            >
              <div className="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center group-hover:bg-gray-200 transition-colors">
                <Clock className="w-5 h-5 text-gray-600" />
              </div>
              <div>
                <p className="font-medium text-gray-900">Histórico</p>
                <p className="text-sm text-gray-500">Ver registros</p>
              </div>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}