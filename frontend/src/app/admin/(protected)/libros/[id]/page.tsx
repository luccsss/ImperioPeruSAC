import { BookForm } from "@/components/admin/BookForm";

export default async function EditBookPage({ params }: { params: Promise<{ id: string }> }) { const { id } = await params; return <div className="admin-page"><div className="admin-page-heading"><div><p className="eyebrow">Edición controlada</p><h1>Editar libro</h1><p>Bibliografía, oferta, clasificación, inventario inicial y SEO permanecen trazables.</p></div></div><BookForm bookId={id} /></div>; }
