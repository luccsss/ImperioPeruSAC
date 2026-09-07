import type { Metadata } from "next";
import { EmptyState } from "@/components/EmptyState";
export const metadata: Metadata = { title: "Búsqueda", robots: { index: false, follow: false } };
export default async function SearchPage({ searchParams }: { searchParams: Promise<{ q?: string }> }) { const { q } = await searchParams; return <div className="container section-tight"><p className="eyebrow">Búsqueda interna · noindex</p><h1 className="heading">Resultados para “{q ?? ""}”</h1><div className="section-tight"><EmptyState title="Índice de búsqueda aún sin catálogo"><p>La interfaz está lista; los resultados se habilitarán con la carga autorizada.</p></EmptyState></div></div>; }

