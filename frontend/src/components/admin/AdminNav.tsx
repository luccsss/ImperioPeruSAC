"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";

const links = [
  ["/admin", "Resumen", "⌂"],
  ["/admin/libros", "Libros", "▤"],
  ["/admin/categorias", "Categorías", "⌘"],
  ["/admin/importaciones", "Importaciones", "⇩"]
] as const;

export function AdminNav() {
  const pathname = usePathname();
  const router = useRouter();
  async function logout() { await fetch("/api/auth/logout", { method: "POST" }); router.push("/admin/login"); router.refresh(); }
  return <aside className="admin-nav"><div className="admin-nav-title"><span className="brand-mark">IP</span><div><strong>Administración</strong><small>Fase 2A</small></div></div><nav>{links.map(([href, label, icon]) => <Link href={href} key={href} aria-current={pathname === href || (href !== "/admin" && pathname.startsWith(href)) ? "page" : undefined}><span aria-hidden>{icon}</span>{label}</Link>)}</nav><button type="button" onClick={logout}>Cerrar sesión</button></aside>;
}

