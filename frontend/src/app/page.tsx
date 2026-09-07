import Link from "next/link";
import { BookCard } from "@/components/BookCard";
import { EmptyState } from "@/components/EmptyState";
import { getNavigation, getPublishedBooks } from "@/lib/api";

export default async function HomePage() {
  const [navigation, books] = await Promise.all([getNavigation(), getPublishedBooks(8)]);
  return (
    <>
      <section className="hero">
        <div className="container hero-grid">
          <div className="hero-copy"><p className="eyebrow">Distribuidor de libros AMOLCA</p><h1 className="display">Conocimiento que acompaña tu práctica.</h1><p className="lede">Una experiencia editorial preparada para encontrar libros especializados de Medicina y Odontología con información comercial clara y precios en dólares.</p><div className="cluster"><Link className="button button-primary" href="/explorar">Explorar catálogo</Link><Link className="button button-secondary" href="/nosotros">Conocer a Imperio Perú</Link></div></div>
          <div className="hero-art" aria-label="Vista editorial de libros especializados"><div className="hero-orbit orbit-one"/><div className="hero-orbit orbit-two"/><div className="hero-book book-one"><span>AMOLCA</span><strong>Medicina</strong></div><div className="hero-book book-two"><span>AMOLCA</span><strong>Odontología</strong></div><div className="hero-note"><span>Catálogo profesional</span><strong>USD</strong></div></div>
        </div>
      </section>

      <section className="container section-tight trust-strip" aria-label="Beneficios"><div><strong>Catálogo especializado</strong><span>Medicina y Odontología</span></div><div><strong>Precio inequívoco</strong><span>Libros expresados en USD</span></div><div><strong>Atención profesional</strong><span>Distribución en Perú</span></div></section>

      <section className="section categories-section"><div className="container"><div className="section-heading"><div><p className="eyebrow">Explora por área</p><h2 className="heading">Dos raíces, múltiples especialidades</h2></div><p>La navegación proviene del catálogo administrativo y puede crecer sin cambios de código.</p></div><div className="root-grid">
        {navigation.map((root) => <article className={`root-card root-${root.rootType.toLowerCase()}`} key={root.id}><p className="eyebrow">Libros especializados</p><h3>{root.label}</h3><p>{root.children.length} especialidades comerciales disponibles en la taxonomía administrativa.</p><div className="root-links">{root.children.slice(0, 6).map((child) => <Link key={child.id} href={child.href}>{child.label}</Link>)}</div><Link className="button button-secondary" href={root.href}>Ver todas las especialidades →</Link></article>)}
      </div></div></section>

      <section className="container section"><div className="section-heading"><div><p className="eyebrow">Selección editorial</p><h2 className="heading">Libros disponibles</h2></div><Link href="/explorar">Ver catálogo →</Link></div>
        {books.length ? <div className="book-grid">{books.map((book) => <BookCard book={book} key={book.id} />)}</div> : <EmptyState title="Catálogo productivo aún no cargado"><p>La arquitectura está lista para recibir el archivo oficial y autorizado de AMOLCA. No mostramos productos ficticios.</p></EmptyState>}
      </section>

      <section className="section amolca-band"><div className="container amolca-grid"><div><p className="eyebrow">Relación comercial transparente</p><h2 className="heading">AMOLCA publica. Imperio Perú distribuye y vende.</h2></div><p>La plataforma separa la información bibliográfica de la oferta comercial. Precio, promoción, stock, contenido propio y publicación permanecen bajo control de Consorcio Imperio Perú SAC.</p></div></section>

      <section className="container section coming-grid"><article><span className="coming-icon">⌁</span><p className="eyebrow">Próximamente</p><h2>Tecnología</h2><p>La arquitectura está preparada; el catálogo se activará únicamente con información comercial real.</p><Link href="/tecnologia">Conocer más</Link></article><article><span className="coming-icon">✦</span><p className="eyebrow">Próximamente</p><h2>Publicidad</h2><p>Productos personalizados y cotizaciones se incorporarán cuando el negocio entregue su catálogo operativo.</p><Link href="/publicidad">Conocer más</Link></article></section>
    </>
  );
}

