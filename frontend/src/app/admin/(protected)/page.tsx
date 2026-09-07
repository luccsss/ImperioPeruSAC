import Link from "next/link";

export default function AdminDashboard() {
  return <div className="admin-page"><div className="admin-page-heading"><div><h1>Fundaciones operativas</h1><p>Gestione datos administrativos sin congelar rutas SEO ni importar el catálogo productivo.</p></div></div><div className="admin-metrics"><article><span>49</span><strong>Nodos de taxonomía</strong><small>32 Medicina + 15 Odontología + 2 raíces</small></article><article><span>USD</span><strong>Moneda de libros</strong><small>Monto y moneda siempre explícitos</small></article><article><span>0</span><strong>Importaciones productivas</strong><small>Aplicación bloqueada por configuración</small></article></div><div className="admin-actions-grid"><Link href="/admin/libros/nuevo"><span>＋</span><strong>Crear libro</strong><small>Bibliografía, oferta, inventario y clasificación</small></Link><Link href="/admin/categorias"><span>⌘</span><strong>Gestionar categorías</strong><small>Orden, visibilidad, jerarquía y SEO</small></Link><Link href="/admin/importaciones"><span>⇩</span><strong>Previsualizar archivo</strong><small>CSV/XLSX con diff y campos protegidos</small></Link></div></div>;
}

