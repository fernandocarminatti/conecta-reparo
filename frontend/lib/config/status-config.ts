export const MAINTENANCE_STATUS_CONFIG: Record<string, { variant: "default" | "secondary" | "destructive" | "success" | "warning" | "outline"; label: string }> = {
  OPEN: { variant: "default", label: "Aberto" },
  IN_PROGRESS: { variant: "warning", label: "Em Andamento" },
  COMPLETED: { variant: "success", label: "Concluído" },
  CANCELED: { variant: "destructive", label: "Cancelado" },
}

export const PLEDGE_STATUS_CONFIG: Record<string, { variant: "default" | "secondary" | "destructive" | "success" | "warning" | "outline"; label: string }> = {
  OFFERED: { variant: "default", label: "Oferecido" },
  PENDING: { variant: "warning", label: "Pendente" },
  REJECTED: { variant: "destructive", label: "Rejeitado" },
  COMPLETED: { variant: "success", label: "Concluído" },
  CANCELED: { variant: "secondary", label: "Cancelado" },
}

export const ACTION_STATUS_CONFIG: Record<string, { variant: "default" | "secondary" | "destructive" | "success" | "warning" | "outline"; label: string }> = {
  SUCCESS: { variant: "success", label: "Sucesso" },
  PARTIAL_SUCCESS: { variant: "warning", label: "Sucesso Parcial" },
  FAILURE: { variant: "destructive", label: "Falha" },
}

export const CATEGORY_CONFIG: Record<string, { variant: "default" | "secondary" | "destructive" | "success" | "warning" | "outline"; label: string; icon: string }> = {
  BUILDING: { variant: "secondary", label: "Construção", icon: "🏢" },
  ELECTRICAL: { variant: "warning", label: "Elétrica", icon: "⚡" },
  PLUMBING: { variant: "secondary", label: "Hidráulica", icon: "🔧" },
  HVAC: { variant: "secondary", label: "HVAC", icon: "❄️" },
  FURNITURE: { variant: "secondary", label: "Mobília", icon: "🪑" },
  GARDENING: { variant: "success", label: "Jardinagem", icon: "🌿" },
  SECURITY: { variant: "destructive", label: "Segurança", icon: "🔒" },
  OTHERS: { variant: "secondary", label: "Outros", icon: "📦" },
  MATERIAL: { variant: "secondary", label: "Material", icon: "📦" },
  LABOR: { variant: "warning", label: "Mão de Obra", icon: "🔧" },
}
