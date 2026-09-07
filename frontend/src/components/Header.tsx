import Link from "next/link";
import { getNavigation } from "@/lib/api";
import { MegaMenu } from "@/components/MegaMenu";

export async function Header() {
  const navigation = await getNavigation();
  return (
    <header className="site-header">
      <div className="topbar"><div className="container topbar-inner"><span>Distribuidor de libros AMOLCA</span><span>Venta en USD · Atención en Perú</span></div></div>
      <div className="container header-main">
        <div className="mobile-menu-slot"><MegaMenu nodes={navigation} /></div>
        <Link className="brand" href="/" aria-label="Imperio Perú, inicio">
          <span className="brand-mark" aria-hidden>IP</span>
          <span><strong>Imperio Perú</strong><small>Conocimiento profesional</small></span>
        </Link>
        <form className="search" action="/buscar" role="search">
          <label className="sr-only" htmlFor="site-search">Buscar libros, autores o ISBN</label>
          <input id="site-search" name="q" type="search" autoComplete="off" placeholder="Buscar por título, autor o ISBN" />
          <button type="submit" aria-label="Buscar">⌕</button>
        </form>
        <nav className="header-actions" aria-label="Utilidades">
          <Link href="/admin/login"><span aria-hidden>♙</span><span className="action-label">Admin</span></Link>
          <Link href="/carrito"><span aria-hidden>◫</span><span className="action-label">Carrito</span></Link>
        </nav>
      </div>
      <div className="desktop-nav">
        <div className="container">
          <div className="desktop-menu-slot"><MegaMenu nodes={navigation} /></div>
          <nav className="desktop-view-links" aria-label="Vistas principales">
            <Link href="/">Inicio</Link>
            <Link href="/nosotros">Nosotros</Link>
            <Link href="/eventos">Eventos</Link>
            <Link href="/liquidacion">Liquidación</Link>
          </nav>
        </div>
      </div>
    </header>
  );
}
