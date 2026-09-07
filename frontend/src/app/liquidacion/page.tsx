import type { Metadata } from "next";
import Link from "next/link";

export const metadata: Metadata = { title: "Liquidación", robots: { index: false, follow: false } };

export default function ClearancePage() {
  return <div className="container section"><div className="coming-page"><p className="eyebrow">Oportunidades</p><h1 className="display">Liquidación</h1><p className="lede">No hay productos en liquidación publicados por el momento. Esta vista mostrará únicamente ofertas comerciales vigentes y verificadas.</p><Link className="button button-primary" href="/">Volver al inicio</Link></div></div>;
}
