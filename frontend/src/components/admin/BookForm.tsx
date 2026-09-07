"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import type { Author, Book, BookStatus, Category, Publisher } from "@/lib/types";

const statuses: BookStatus[] = ["DRAFT", "REVIEWED", "PUBLISHED", "OUT_OF_STOCK", "DISCONTINUED", "ARCHIVED"];

type Props = { bookId?: string };

export function BookForm({ bookId }: Props) {
  const router = useRouter();
  const [book, setBook] = useState<Book | null>(null);
  const [authors, setAuthors] = useState<Author[]>([]);
  const [publishers, setPublishers] = useState<Publisher[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [state, setState] = useState<"loading" | "ready" | "saving" | "error">("loading");
  const [message, setMessage] = useState("");
  const [newAuthorName, setNewAuthorName] = useState("");

  useEffect(() => {
    const urls = ["/api/backend/reference/authors", "/api/backend/reference/publishers", "/api/backend/categories"];
    if (bookId) urls.push(`/api/backend/books/${bookId}`);
    Promise.all(urls.map(async (url) => {
      const response = await fetch(url);
      if (!response.ok) throw new Error("No fue posible cargar los datos del formulario");
      return response.json();
    })).then(([authorData, publisherData, categoryData, bookData]) => {
      setAuthors(authorData as Author[]);
      setPublishers(publisherData as Publisher[]);
      setCategories(categoryData as Category[]);
      setBook((bookData as Book | undefined) ?? null);
      setState("ready");
    }).catch((error: Error) => { setMessage(error.message); setState("error"); });
  }, [bookId]);

  const selectedCategoryIds = useMemo(() => new Set(book?.categories.map((item) => item.id) ?? []), [book]);
  if (state === "loading") return <div className="loading-block" aria-label="Cargando formulario" />;
  if (state === "error") return <div className="notice" role="alert">{message}</div>;

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setState("saving"); setMessage("");
    const data = new FormData(event.currentTarget);
    const categoryIds = data.getAll("categoryIds").map(String);
    const primaryId = String(data.get("primaryCategoryId") ?? "");
    const amount = Number(data.get("regularPrice"));
    const promoRaw = String(data.get("promotionalPrice") ?? "");
    const payload = {
      isbn13: String(data.get("isbn13") ?? "").replaceAll(/[^0-9]/g, ""), isbn10: String(data.get("isbn10") ?? "").replaceAll(/[^0-9Xx]/g, ""),
      title: data.get("title"), subtitle: data.get("subtitle"), edition: data.get("edition"),
      publicationYear: data.get("publicationYear") ? Number(data.get("publicationYear")) : null,
      pages: data.get("pages") ? Number(data.get("pages")) : null, language: data.get("language"), bindingFormat: data.get("bindingFormat"),
      bibliographicDescription: data.get("bibliographicDescription"), publisherId: data.get("publisherId") || null,
      authorIds: data.getAll("authorIds").map(String), sku: data.get("sku"), slug: data.get("slug"),
      regularPrice: { amount, currency: "USD" },
      promotionalPrice: promoRaw ? { amount: Number(promoRaw), currency: "USD" } : null,
      commercialDescription: data.get("commercialDescription"), status: data.get("status"),
      featured: data.get("featured") === "on", newArrival: data.get("newArrival") === "on",
      categories: categoryIds.map((categoryId, sortOrder) => ({ categoryId, primary: categoryId === primaryId, sortOrder })),
      seo: { seoTitle: data.get("seoTitle"), metaDescription: data.get("metaDescription"), canonicalUrl: data.get("canonicalUrl"), robotsPolicy: data.get("robotsPolicy"), ogTitle: data.get("ogTitle"), ogDescription: data.get("ogDescription"), ogImageUrl: data.get("ogImageUrl") },
      initialOnHand: book?.inventory?.onHand ?? Number(data.get("initialOnHand") ?? 0), minimumStock: Number(data.get("minimumStock") ?? 0)
    };
    if (!categoryIds.length || !primaryId || !categoryIds.includes(primaryId)) {
      setMessage("Seleccione al menos una categoría y marque una de ellas como principal."); setState("ready"); return;
    }
    const response = await fetch(bookId ? `/api/backend/books/${bookId}` : "/api/backend/books", { method: bookId ? "PUT" : "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) });
    if (!response.ok) {
      const problem = await response.json().catch(() => ({})) as { detail?: string; errors?: Record<string, string> };
      setMessage(problem.detail ?? Object.values(problem.errors ?? {}).join(" · ") ?? "No fue posible guardar el libro."); setState("ready"); return;
    }
    const saved = await response.json() as Book;
    router.push(`/admin/libros/${saved.id}`); router.refresh(); setBook(saved); setMessage("Libro guardado correctamente."); setState("ready");
  }

  async function createAuthor() {
    const name = newAuthorName.trim();
    if (!name) return;
    const slug = name.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
    const response = await fetch("/api/backend/reference/authors", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ name, slug, biography: "", active: true }) });
    if (!response.ok) { setMessage("No fue posible crear el autor. Revise que el nombre y slug no estén duplicados."); return; }
    const created = await response.json() as Author;
    setAuthors((current) => [...current, created].sort((left, right) => left.name.localeCompare(right.name)));
    setNewAuthorName(""); setMessage(`Autor ${created.name} creado; selecciónelo en la lista.`);
  }

  return <form className="admin-form" onSubmit={submit}>
    {message && <div className={message.includes("correctamente") ? "success" : "notice"} role="status">{message}</div>}
    <section className="form-section"><div className="form-section-heading"><p className="eyebrow">Registro editorial</p><h2>Datos bibliográficos</h2></div><div className="form-grid">
      <label className="field span-2"><span>Título *</span><input className="input" name="title" defaultValue={book?.title} required maxLength={300} /></label>
      <label className="field span-2"><span>Subtítulo</span><input className="input" name="subtitle" defaultValue={book?.subtitle} maxLength={300} /></label>
      <label className="field"><span>ISBN-13</span><input className="input" name="isbn13" defaultValue={book?.isbn13} inputMode="numeric" pattern="[0-9 -]{13,17}" /></label>
      <label className="field"><span>ISBN-10</span><input className="input" name="isbn10" defaultValue={book?.isbn10} /></label>
      <label className="field"><span>Edición</span><input className="input" name="edition" defaultValue={book?.edition} /></label>
      <label className="field"><span>Año</span><input className="input" name="publicationYear" type="number" min="1400" max="2200" defaultValue={book?.publicationYear} /></label>
      <label className="field"><span>Páginas</span><input className="input" name="pages" type="number" min="1" defaultValue={book?.pages} /></label>
      <label className="field"><span>Idioma</span><input className="input" name="language" defaultValue={book?.language ?? "Español"} /></label>
      <label className="field"><span>Formato</span><input className="input" name="bindingFormat" defaultValue={book?.bindingFormat} /></label>
      <label className="field"><span>Editorial</span><select className="select" name="publisherId" defaultValue={book?.publisher?.id ?? ""}><option value="">Sin editorial</option>{publishers.map((item) => <option value={item.id} key={item.id}>{item.name}</option>)}</select></label>
      <div className="field span-2"><span>Crear autor de referencia</span><div className="inline-create"><input className="input" value={newAuthorName} onChange={(event) => setNewAuthorName(event.target.value)} placeholder="Nombre completo" /><button className="button button-secondary" type="button" onClick={createAuthor}>＋ Crear autor</button></div></div>
      <label className="field span-2"><span>Autores * (Ctrl/Cmd para selección múltiple)</span><select className="select multi-select" name="authorIds" multiple required defaultValue={book?.authors.map((item) => item.id)}>{authors.map((item) => <option value={item.id} key={item.id}>{item.name}</option>)}</select></label>
      <label className="field span-2"><span>Descripción bibliográfica</span><textarea className="textarea" name="bibliographicDescription" defaultValue={book?.bibliographicDescription} /></label>
    </div></section>
    <section className="form-section"><div className="form-section-heading"><p className="eyebrow">BookOffer</p><h2>Oferta comercial e inventario</h2><p>La moneda se almacena explícitamente y permanece fija en USD para esta línea.</p></div><div className="form-grid">
      <label className="field"><span>SKU *</span><input className="input" name="sku" defaultValue={book?.sku} required /></label>
      <label className="field"><span>Slug estable *</span><input className="input" name="slug" defaultValue={book?.slug} required pattern="[a-z0-9]+(?:-[a-z0-9]+)*" /></label>
      <label className="field"><span>Precio regular (USD) *</span><div className="money-input"><span>USD</span><input className="input" name="regularPrice" type="number" min="0" step="0.01" defaultValue={book?.regularPrice.amount} required /></div></label>
      <label className="field"><span>Precio promocional (USD)</span><div className="money-input"><span>USD</span><input className="input" name="promotionalPrice" type="number" min="0" step="0.01" defaultValue={book?.promotionalPrice?.amount} /></div></label>
      {!book && <label className="field"><span>Stock inicial</span><input className="input" name="initialOnHand" type="number" min="0" defaultValue="0" /></label>}
      <label className="field"><span>Stock mínimo</span><input className="input" name="minimumStock" type="number" min="0" defaultValue={book?.inventory?.minimumStock ?? 0} /></label>
      <label className="field"><span>Estado</span><select className="select" name="status" defaultValue={book?.status ?? "DRAFT"}>{statuses.map((status) => <option key={status}>{status}</option>)}</select></label>
      <div className="check-row"><label><input type="checkbox" name="featured" defaultChecked={book?.featured} /> Destacado</label><label><input type="checkbox" name="newArrival" defaultChecked={book?.newArrival} /> Novedad</label></div>
      <label className="field span-2"><span>Descripción comercial</span><textarea className="textarea" name="commercialDescription" defaultValue={book?.commercialDescription} /></label>
    </div></section>
    {book?.inventory && <InventoryAdjustment book={book} onUpdated={(inventory) => setBook({ ...book, inventory })} />}
    <section className="form-section"><div className="form-section-heading"><p className="eyebrow">Clasificación many-to-many</p><h2>Categorías</h2><p>Un libro mantiene una sola URL canónica aunque aparezca en varias categorías.</p></div><div className="category-picker">{categories.filter((item) => item.parentId).map((item) => { const selected = selectedCategoryIds.has(item.id); return <label key={item.id}><input type="checkbox" name="categoryIds" value={item.id} defaultChecked={selected} /><span>{item.displayName}<small>{item.rootType}</small></span><input type="radio" name="primaryCategoryId" value={item.id} defaultChecked={book?.categories.some((value) => value.id === item.id && value.primary)} aria-label={`Usar ${item.displayName} como principal`} /></label>; })}</div></section>
    <section className="form-section"><div className="form-section-heading"><p className="eyebrow">Editable, no masivo</p><h2>Metadatos SEO</h2></div><div className="form-grid">
      <label className="field span-2"><span>Título SEO</span><input className="input" name="seoTitle" defaultValue={book?.seo?.seoTitle} maxLength={180} /></label>
      <label className="field span-2"><span>Meta description</span><textarea className="textarea" name="metaDescription" defaultValue={book?.seo?.metaDescription} maxLength={320} /></label>
      <label className="field"><span>Canonical URL</span><input className="input" name="canonicalUrl" defaultValue={book?.seo?.canonicalUrl ?? (book ? `/libro/${book.slug}/` : "")} /></label>
      <label className="field"><span>Robots</span><select className="select" name="robotsPolicy" defaultValue={book?.seo?.robotsPolicy ?? "NOINDEX"}><option>NOINDEX</option><option>INDEX</option></select></label>
      <label className="field"><span>OG title</span><input className="input" name="ogTitle" defaultValue={book?.seo?.ogTitle} /></label>
      <label className="field"><span>OG image URL</span><input className="input" name="ogImageUrl" defaultValue={book?.seo?.ogImageUrl} /></label>
      <label className="field span-2"><span>OG description</span><textarea className="textarea" name="ogDescription" defaultValue={book?.seo?.ogDescription} /></label>
    </div></section>
    <div className="sticky-actions"><button className="button button-primary" disabled={state === "saving"}>{state === "saving" ? "Guardando…" : "Guardar libro"}</button></div>
  </form>;
}

function InventoryAdjustment({ book, onUpdated }: { book: Book; onUpdated: (inventory: NonNullable<Book["inventory"]>) => void }) {
  const [message, setMessage] = useState("");
  const inventory = book.inventory!;
  const [delta, setDelta] = useState(0);
  const [minimumStock, setMinimumStock] = useState(inventory.minimumStock);
  const [reason, setReason] = useState("");
  async function adjust() {
    setMessage("");
    if (reason.trim().length < 3) { setMessage("Indique un motivo de al menos tres caracteres."); return; }
    const response = await fetch(`/api/backend/inventory/${inventory.id}/adjust`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ delta, minimumStock, reason }) });
    if (!response.ok) { const problem = await response.json().catch(() => ({})) as { detail?: string }; setMessage(problem.detail ?? "No fue posible ajustar el inventario."); return; }
    onUpdated(await response.json() as NonNullable<Book["inventory"]>); setDelta(0); setReason(""); setMessage("Movimiento registrado en el historial de inventario.");
  }
  return <section className="form-section"><div className="form-section-heading"><p className="eyebrow">Movimiento auditable</p><h2>Inventario</h2><p>En mano: {inventory.onHand} · reservado: {inventory.reserved} · disponible: {inventory.available} · estado: {inventory.status}</p></div>{message && <div className="success" role="status">{message}</div>}<div className="notice">Use cantidades positivas para ingresos y negativas para salidas. Cada ajuste conserva motivo, actor y saldo posterior.</div><div className="form-grid">
    <label className="field"><span>Ajuste de unidades *</span><input className="input" type="number" value={delta} onChange={(event) => setDelta(Number(event.target.value))} /></label>
    <label className="field"><span>Stock mínimo *</span><input className="input" type="number" min="0" value={minimumStock} onChange={(event) => setMinimumStock(Number(event.target.value))} /></label>
    <label className="field span-2"><span>Motivo *</span><input className="input" minLength={3} maxLength={300} value={reason} onChange={(event) => setReason(event.target.value)} /></label>
  </div><button className="button button-secondary" type="button" onClick={adjust}>Registrar ajuste</button>
  </section>;
}
