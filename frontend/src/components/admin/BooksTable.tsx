"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { formatMoney } from "@/lib/money";
import type { Book } from "@/lib/types";

export function BooksTable() {
  const [books, setBooks] = useState<Book[]>([]);
  const [state, setState] = useState<"loading" | "ready" | "error">("loading");
  useEffect(() => { fetch("/api/backend/books").then(async (response) => { if (!response.ok) throw new Error(); return response.json() as Promise<Book[]>; }).then((value) => { setBooks(value); setState("ready"); }).catch(() => setState("error")); }, []);
  if (state === "loading") return <div className="loading-block" />;
  if (state === "error") return <div className="notice">No se pudo conectar con la API administrativa.</div>;
  if (!books.length) return <div className="empty"><div><h2>No hay libros creados</h2><p className="muted">Cree un registro manual o previsualice un archivo autorizado.</p><Link className="button button-primary" href="/admin/libros/nuevo">Crear primer libro</Link></div></div>;
  return <div className="table-shell"><table><thead><tr><th>Libro</th><th>SKU / ISBN</th><th>Precio</th><th>Stock</th><th>Estado</th><th><span className="sr-only">Acciones</span></th></tr></thead><tbody>{books.map((book) => <tr key={book.id}><td><strong>{book.title}</strong><small>{book.authors.map((author) => author.name).join(", ")}</small></td><td>{book.sku}<small>{book.isbn13}</small></td><td>{formatMoney(book.regularPrice)}</td><td>{book.inventory?.available ?? 0}</td><td><span className={`chip ${book.status === "PUBLISHED" ? "status-good" : "status-warn"}`}>{book.status}</span></td><td><Link className="button button-secondary" href={`/admin/libros/${book.id}`}>Editar</Link></td></tr>)}</tbody></table></div>;
}

