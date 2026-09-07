import type { Metadata } from "next";
import Link from "next/link";
export const metadata: Metadata = { title: "Publicidad — Próximamente", robots: { index: false, follow: false } };
export default function AdvertisingPage() { return <div className="container section"><div className="coming-page"><p className="eyebrow">Productos personalizados en preparación</p><h1 className="display">Publicidad, próximamente.</h1><p className="lede">La futura experiencia admitirá productos a pedido y solicitudes de cotización en PEN. No mostramos servicios ni precios ficticios.</p><Link className="button button-primary" href="/">Volver al catálogo de libros</Link></div></div>; }

