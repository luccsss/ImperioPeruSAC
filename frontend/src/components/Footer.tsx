import Link from "next/link";

export function Footer() {
  return (
    <footer className="site-footer">
      <div className="container footer-grid">
        <div><div className="brand brand-inverse"><span className="brand-mark">IP</span><span><strong>Imperio Perú</strong><small>Conocimiento profesional</small></span></div><p>Distribución y venta de libros AMOLCA para profesionales y estudiantes.</p></div>
        <details open><summary>Catálogo</summary><nav><Link href="/explorar?raiz=MEDICINE">Medicina</Link><Link href="/explorar?raiz=DENTISTRY">Odontología</Link><Link href="/tecnologia">Tecnología</Link><Link href="/publicidad">Publicidad</Link></nav></details>
        <details><summary>Ayuda</summary><nav><Link href="/nosotros">Nosotros</Link><Link href="/contacto">Contacto</Link><Link href="/envios">Envíos</Link><Link href="/libro-reclamaciones">Libro de reclamaciones</Link></nav></details>
        <details><summary>Información legal</summary><nav><Link href="/privacidad">Privacidad</Link><Link href="/terminos">Términos</Link><Link href="/cambios-devoluciones">Cambios y devoluciones</Link></nav></details>
      </div>
      <div className="container footer-bottom"><span>© {new Date().getFullYear()} Consorcio Imperio Perú SAC</span><span>Fase 2A · Catálogo productivo aún no publicado</span></div>
    </footer>
  );
}

