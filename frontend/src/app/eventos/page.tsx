import type { Metadata } from "next";
import Link from "next/link";

export const metadata: Metadata = { title: "Eventos", robots: { index: false, follow: false } };

export default function EventsPage() {
  return <div className="container section"><div className="coming-page"><p className="eyebrow">Agenda de Imperio Perú</p><h1 className="display">Eventos</h1><p className="lede">Aún no hay eventos publicados. Aquí aparecerán las presentaciones, actividades y novedades confirmadas por Consorcio Imperio Perú.</p><Link className="button button-primary" href="/">Volver al inicio</Link></div></div>;
}
