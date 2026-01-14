import { redirect } from 'next/navigation';

interface EditPageProps {
  params: Promise<{ id: string }>;
}

export default async function EditRedirectPage({ params }: EditPageProps) {
  const { id } = await params;
  redirect(`/admin/maintenances/form?id=${id}&mode=edit`);
}
