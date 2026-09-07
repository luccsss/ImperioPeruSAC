"use client";

import Link from "next/link";
import { useEffect, useId, useState } from "react";
import type { NavigationNode } from "@/lib/types";

type MenuSection = {
  id: string;
  label: string;
  href: string;
  eyebrow: string;
  children: Array<{ id: string; label: string; href: string }>;
};

export function MegaMenu({ nodes }: { nodes: NavigationNode[] }) {
  const medicine = nodes.find((node) => node.rootType === "MEDICINE");
  const dentistry = nodes.find((node) => node.rootType === "DENTISTRY");
  const sections: MenuSection[] = [
    {
      id: medicine?.id ?? "medicine",
      label: "Medicina",
      href: medicine?.href ?? "/explorar?raiz=MEDICINE",
      eyebrow: "Libros médicos",
      children: medicine?.children ?? [],
    },
    {
      id: dentistry?.id ?? "dentistry",
      label: "Odontología",
      href: dentistry?.href ?? "/explorar?raiz=DENTISTRY",
      eyebrow: "Libros de odontología",
      children: dentistry?.children ?? [],
    },
    {
      id: "technology",
      label: "Tecnología",
      href: "/tecnologia",
      eyebrow: "Productos tecnológicos",
      children: [{ id: "technology-laptops", label: "Laptops", href: "/tecnologia" }],
    },
    {
      id: "advertising",
      label: "Publicidad",
      href: "/publicidad",
      eyebrow: "Productos personalizados",
      children: [
        { id: "advertising-signs", label: "Carteles", href: "/publicidad" },
        { id: "advertising-stamps", label: "Sellos", href: "/publicidad" },
        { id: "advertising-lettering", label: "Letreros", href: "/publicidad" },
        { id: "advertising-custom", label: "Productos personalizados", href: "/publicidad" },
      ],
    },
  ];
  const [desktopOpen, setDesktopOpen] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [activeId, setActiveId] = useState(sections[0]!.id);
  const dialogId = useId();
  const menuId = `${dialogId}-desktop`;
  const active = sections.find((section) => section.id === activeId) ?? sections[0]!;

  useEffect(() => {
    if (!mobileOpen && !desktopOpen) return;
    const close = (event: KeyboardEvent) => {
      if (event.key !== "Escape") return;
      setMobileOpen(false);
      setDesktopOpen(false);
    };
    document.addEventListener("keydown", close);
    if (mobileOpen) document.body.style.overflow = "hidden";
    return () => { document.removeEventListener("keydown", close); document.body.style.overflow = ""; };
  }, [desktopOpen, mobileOpen]);

  return (
    <>
      <div className="desktop-category-menu" onMouseEnter={() => setDesktopOpen(true)} onMouseLeave={() => setDesktopOpen(false)}>
        <button className="button nav-catalog-button" type="button" aria-expanded={desktopOpen} aria-controls={menuId} onFocus={() => setDesktopOpen(true)} onClick={() => setDesktopOpen((value) => !value)}>
          <span aria-hidden>☰</span> Categorías <span className="nav-catalog-chevron" aria-hidden>⌄</span>
        </button>
        {desktopOpen && (
        <div className="mega-menu" id={menuId}>
          <div className="mega-roots" role="tablist" aria-label="Líneas del catálogo de libros">
            {sections.map((section) => (
              <button key={section.id} role="tab" aria-selected={section.id === active.id} aria-controls={`${menuId}-panel`} onFocus={() => setActiveId(section.id)} onMouseEnter={() => setActiveId(section.id)} onClick={() => setActiveId(section.id)}>
                <span>{section.label}</span><span aria-hidden>›</span>
              </button>
            ))}
          </div>
          <div className="mega-panel" id={`${menuId}-panel`} role="tabpanel">
            <div className="mega-heading"><div><p className="eyebrow">{active.eyebrow}</p><h2>{active.label}</h2></div><Link href={active.href} onClick={() => setDesktopOpen(false)}>Ver todo <span aria-hidden>→</span></Link></div>
            <div className="mega-links">
              {active.children.length > 0
                ? active.children.map((child) => <Link key={child.id} href={child.href} onClick={() => setDesktopOpen(false)}>{child.label}</Link>)
                : <p className="muted">Las subcategorías se publicarán desde el catálogo administrativo.</p>}
            </div>
          </div>
        </div>
        )}
      </div>

      <button className="icon-button mobile-menu-button" type="button" aria-label="Abrir menú" aria-controls={dialogId} aria-expanded={mobileOpen} onClick={() => setMobileOpen(true)}>☰</button>
      {mobileOpen && (
        <div className="drawer-backdrop" role="presentation" onMouseDown={(event) => { if (event.currentTarget === event.target) setMobileOpen(false); }}>
          <div className="drawer" id={dialogId} role="dialog" aria-modal="true" aria-label="Menú principal">
            <div className="drawer-header"><strong>Explorar catálogo</strong><button className="icon-button" type="button" aria-label="Cerrar menú" onClick={() => setMobileOpen(false)}>×</button></div>
            <nav aria-label="Categorías de libros">
              {sections.map((section) => (
                <details key={section.id} className="drawer-group">
                  <summary>{section.label}<span aria-hidden>+</span></summary>
                  <div className="drawer-links">
                    {section.children.map((child) => <Link key={child.id} href={child.href} onClick={() => setMobileOpen(false)}>{child.label}</Link>)}
                    <Link className="drawer-all" href={section.href} onClick={() => setMobileOpen(false)}>Ver todo en {section.label} →</Link>
                  </div>
                </details>
              ))}
              <div className="drawer-view-links">
                <Link href="/" onClick={() => setMobileOpen(false)}>Inicio</Link>
                <Link href="/nosotros" onClick={() => setMobileOpen(false)}>Nosotros</Link>
                <Link href="/eventos" onClick={() => setMobileOpen(false)}>Eventos</Link>
                <Link href="/liquidacion" onClick={() => setMobileOpen(false)}>Liquidación</Link>
              </div>
            </nav>
          </div>
        </div>
      )}
    </>
  );
}
