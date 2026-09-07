"use client";

import { FormEvent, useEffect, useState } from "react";
import type { Category } from "@/lib/types";

const empty: Omit<Category, "id" | "href"> = { displayName: "", code: "", rootType: "MEDICINE", parentId: "", slugCandidate: "", publicPath: "", description: "", sortOrder: 0, active: true, visible: true, showInMenu: true, seo: undefined };

export function CategoryManager() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [selected, setSelected] = useState<Category | null>(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  async function load() { const response = await fetch("/api/backend/categories"); if (!response.ok) throw new Error(); setCategories(await response.json() as Category[]); setLoading(false); }
  useEffect(() => { fetch("/api/backend/categories").then(async (response) => { if (!response.ok) throw new Error(); return response.json() as Promise<Category[]>; }).then((data) => { setCategories(data); setLoading(false); }).catch(() => { setMessage("No fue posible cargar la taxonomía."); setLoading(false); }); }, []);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault(); setMessage("");
    const data = new FormData(event.currentTarget);
    const payload = {
      parentId: data.get("parentId") || null, rootType: data.get("rootType"), code: data.get("code"), displayName: data.get("displayName"),
      slugCandidate: data.get("slugCandidate"), publicPath: data.get("publicPath") || null, description: data.get("description"),
      sortOrder: Number(data.get("sortOrder")), active: data.get("active") === "on", visible: data.get("visible") === "on", showInMenu: data.get("showInMenu") === "on",
      seo: { seoTitle: data.get("seoTitle"), metaDescription: data.get("metaDescription"), canonicalUrl: data.get("canonicalUrl"), robotsPolicy: data.get("robotsPolicy"), ogTitle: "", ogDescription: "", ogImageUrl: "" }
    };
    const response = await fetch(selected ? `/api/backend/categories/${selected.id}` : "/api/backend/categories", { method: selected ? "PUT" : "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) });
    if (!response.ok) { const problem = await response.json().catch(() => ({})) as { detail?: string }; setMessage(problem.detail ?? "No fue posible guardar la categoría."); return; }
    setMessage("Categoría guardada. La ruta pública continúa sin congelarse salvo que se defina expresamente."); setSelected(null); await load();
  }

  if (loading) return <div className="loading-block" />;
  const value = selected ?? empty;
  return <div className="taxonomy-layout"><div className="taxonomy-list card"><div className="taxonomy-toolbar"><strong>Taxonomía administrativa</strong><button className="button button-secondary" onClick={() => setSelected(null)}>＋ Nueva</button></div>{categories.map((category) => <button type="button" className={selected?.id === category.id ? "selected" : ""} key={category.id} onClick={() => setSelected(category)}><span>{category.parentId ? "↳" : "◆"} {category.displayName}<small>{category.code}</small></span><span className={`chip ${category.active ? "status-good" : ""}`}>{category.rootType}</span></button>)}</div>
    <form className="form-section" key={selected?.id ?? "new"} onSubmit={submit}><div className="form-section-heading"><p className="eyebrow">{selected ? "Editar nodo" : "Nuevo nodo"}</p><h2>{selected?.displayName ?? "Categoría"}</h2></div>{message && <div className="notice" role="status">{message}</div>}<div className="form-grid">
      <label className="field span-2"><span>Nombre *</span><input className="input" name="displayName" defaultValue={value.displayName} required /></label>
      <label className="field"><span>Código estable *</span><input className="input" name="code" defaultValue={value.code} pattern="[a-z0-9]+(?:-[a-z0-9]+)*" required /></label>
      <label className="field"><span>Raíz *</span><select className="select" name="rootType" defaultValue={value.rootType}><option value="MEDICINE">Medicina</option><option value="DENTISTRY">Odontología</option></select></label>
      <label className="field span-2"><span>Padre</span><select className="select" name="parentId" defaultValue={value.parentId ?? ""}><option value="">Categoría raíz</option>{categories.filter((item) => !item.parentId && item.id !== selected?.id).map((item) => <option value={item.id} key={item.id}>{item.displayName}</option>)}</select></label>
      <label className="field"><span>Slug candidato (no congela URL)</span><input className="input" name="slugCandidate" defaultValue={value.slugCandidate} /></label>
      <label className="field"><span>Ruta pública (opcional)</span><input className="input" name="publicPath" defaultValue={value.publicPath} placeholder="Pendiente" /></label>
      <label className="field"><span>Orden</span><input className="input" type="number" name="sortOrder" min="0" defaultValue={value.sortOrder} /></label>
      <div className="check-row"><label><input type="checkbox" name="active" defaultChecked={value.active} /> Activa</label><label><input type="checkbox" name="visible" defaultChecked={value.visible} /> Visible</label><label><input type="checkbox" name="showInMenu" defaultChecked={value.showInMenu} /> Menú</label></div>
      <label className="field span-2"><span>Descripción</span><textarea className="textarea" name="description" defaultValue={value.description} /></label>
      <label className="field span-2"><span>Título SEO</span><input className="input" name="seoTitle" defaultValue={value.seo?.seoTitle} /></label>
      <label className="field span-2"><span>Meta description</span><textarea className="textarea" name="metaDescription" defaultValue={value.seo?.metaDescription} /></label>
      <label className="field"><span>Canonical</span><input className="input" name="canonicalUrl" defaultValue={value.seo?.canonicalUrl} /></label>
      <label className="field"><span>Robots</span><select className="select" name="robotsPolicy" defaultValue={value.seo?.robotsPolicy ?? "NOINDEX"}><option>NOINDEX</option><option>INDEX</option></select></label>
    </div><button className="button button-primary">Guardar categoría</button></form></div>;
}
