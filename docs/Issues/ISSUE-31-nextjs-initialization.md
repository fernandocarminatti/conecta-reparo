# [ISSUE #31] Initialize Next.js Frontend

## Context
Set up the Next.js frontend application with TypeScript, Tailwind CSS, and the required folder structure for the Conecta Reparo application.

## Current State
- Repository is being restructured as monorepo
- `frontend/` directory exists but is empty
- Backend API is being developed

## Target State
- Next.js 14+ application initialized
- TypeScript configured with strict mode
- Tailwind CSS integrated
- Basic page structure for public and admin sections
- API client library created
- Development server runs successfully

## Tasks

### Pre-requisites
- [ ] Verify Node.js 18+ is available
- [ ] Verify npm or yarn is available
- [ ] Ensure `frontend/` directory exists (from issue #29)

### Initialize Next.js Application
- [ ] Run `create-next-app` with TypeScript and Tailwind
- [ ] Configure App Router (not src directory)
- [ ] Install additional dependencies (lucide-react, date-fns)

### Create Folder Structure
- [ ] Create public pages structure (home, maintenance details, pledge)
- [ ] Create admin pages structure (dashboard, create maintenance, history)
- [ ] Create components directory structure
- [ ] Create lib directory for utilities

### Initial Page Implementation
- [ ] Create home page listing open maintenances
- [ ] Create maintenance detail page
- [ ] Create pledge submission page
- [ ] Create admin dashboard
- [ ] Create admin layout with navigation

### Verify Setup
- [ ] Run development server
- [ ] Test basic routing
- [ ] Verify TypeScript compilation
- [ ] Verify Tailwind styles apply

## Implementation

### Step 1: Initialize Next.js App
```bash
cd frontend
npx create-next-app@latest . \
  --typescript \
  --tailwind \
  --app \
  --no-src-dir \
  --import-alias "@/*"
```

### Step 2: Install Additional Dependencies
```bash
npm install lucide-react date-fns
npm install -D @types/node
```

### Step 3: Create Directory Structure
```bash
mkdir -p app/maintenance/\[id\]
mkdir -p app/pledge
mkdir -p app/admin/layout.tsx
mkdir -p app/admin/maintenance/new
mkdir -p app/admin/maintenance/\[id\]/edit
mkdir -p app/admin/history
mkdir -p components/ui
mkdir -p lib
```

### Step 4: Basic Page Implementations

#### app/page.tsx (Home - Public)
```tsx
import Link from 'next/link';

export default function Home() {
  return (
    <main className="min-h-screen p-8">
      <h1 className="text-4xl font-bold mb-8">Conecta Reparo</h1>
      <p className="text-lg mb-4">Sistema de Apoio à Manutenção de Estruturas de Saúde</p>
      
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <Link 
          href="/maintenance/1"
          className="p-6 border rounded-lg hover:shadow-lg transition-shadow"
        >
          <h2 className="text-xl font-semibold">Ver Manutenções</h2>
          <p>Visualizar necessidades de manutenção abertas</p>
        </Link>
        
        <Link 
          href="/pledge"
          className="p-6 border rounded-lg hover:shadow-lg transition-shadow"
        >
          <h2 className="text-xl font-semibold">Fazer Pledge</h2>
          <p>Oferecer materiais ou voluntariado</p>
        </Link>
        
        <Link 
          href="/admin"
          className="p-6 border rounded-lg hover:shadow-lg transition-shadow"
        >
          <h2 className="text-xl font-semibold">Área Administrativa</h2>
          <p>Gerenciar manutenções (requer login)</p>
        </Link>
      </div>
    </main>
  );
}
```

#### app/layout.tsx
```tsx
import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Conecta Reparo Mondaí',
  description: 'Plataforma de Apoio à Manutenção de Estruturas de Saúde Comunitárias',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="pt-BR">
      <body className="min-h-screen bg-gray-50">{children}</body>
    </html>
  );
}
```

### Step 5: Run and Verify
```bash
npm run dev
```

Visit http://localhost:3000 to verify setup.

## Folder Structure Summary
```
frontend/
├── app/
│   ├── layout.tsx
│   ├── page.tsx
│   ├── maintenance/
│   │   └── [id]/
│   │       └── page.tsx
│   ├── pledge/
│   │   └── page.tsx
│   └── admin/
│       ├── layout.tsx
│       ├── page.tsx
│       ├── maintenance/
│       │   ├── new/
│       │   │   └── page.tsx
│       │   └── [id]/
│       │       └── edit/
│       │           └── page.tsx
│       └── history/
│           └── page.tsx
├── components/
│   ├── ui/
│   ├── MaintenanceCard.tsx
│   └── PledgeForm.tsx
├── lib/
│   ├── api.ts
│   └── types.ts
├── public/
├── next.config.js
├── tailwind.config.ts
├── tsconfig.json
└── package.json
```

## Acceptance Criteria

- [ ] Next.js application initialized with TypeScript
- [ ] Tailwind CSS configured and working
- [ ] Basic routing works (/, /maintenance/1, /pledge, /admin)
- [ ] Development server runs without errors
- [ ] TypeScript configured with strict mode
- [ ] Page structure matches requirements
- [ ] No console errors in browser
- [ ] Lighthouse accessibility score > 90

## Definition of Done

- [ ] Next.js app initialized and running
- [ ] All required pages created (even if basic)
- [ ] Tailwind CSS styling applied
- [ ] TypeScript compiles without errors
- [ ] Navigation between pages works
- [ ] Changes committed

## Notes

- Use `lucide-react` for icons
- Use `date-fns` for date formatting
- Keep pages simple for now - functionality comes later
- Focus on structure and routing first
- Accessibility (a11y) is important from the start
- Mobile-responsive design using Tailwind classes

## Related Issues

- #29: Repository Monorepo Restructure
- #35: Create API Client Library
- #36: Frontend Dockerfile Creation
- #33: Nginx Reverse Proxy Configuration (for routing)
