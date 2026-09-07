import type { Metadata } from "next";
import Link from "next/link";
export const metadata: Metadata = { title: "Tecnología — Próximamente", robots: { index: false, follow: false } };
export default function TechnologyPage() { return <div className="container section"><div className="coming-page"><p className="eyebrow">Línea comercial en preparación</p><h1 className="display">Tecnología, próximamente.</h1><p className="lede">Esta sección se activará cuando Consorcio Imperio Perú confirme su catálogo real de productos tecnológicos en PEN.</p><Link className="button button-primary" href="/">Volver al catálogo de libros</Link></div></div>; }

