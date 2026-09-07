import type { Metadata } from "next";
import Link from "next/link";
import { BookCard } from "@/components/BookCard";
import { EmptyState } from "@/components/EmptyState";
import { getNavigation, getPublishedBooks } from "@/lib/api";

export const metadata: Metadata = { title: "Explorar catálogo", robots: { index: false, follow: true } };

export default async function ExplorePage({ searchParams }: { searchParams: Promise<{ categoria?: string; raiz?: string }> }) {
  const query = await searchParams;
  const [navigation, books] = await Promise.all([getNavigation(), getPublishedBooks(60)]);
  const allCategories = navigation.flatMap((root) => [root, ...root.children]);
  const selected = allCategories.find((node) => node.code === query.categoria) ?? navigation.find((node) => node.rootType === query.raiz);
  const filtered = selected ? books.filter((book) => book.categories.some((category) => category.code === selected.code || category.rootType === selected.rootType)) : books;
  return <div className="container section-tight"><div className="catalog-heading"><div><p className="eyebrow">Ruta de exploración configurable · noindex</p><h1 className="heading">{selected?.label ?? "Catálogo de libros"}</h1><p className="lede">Esta utilidad permite probar navegación y componentes sin congelar URLs SEO de categorías.</p></div><button className="button button-secondary filter-button" type="button">Filtros <span aria-hidden>☷</span></button></div><div className="catalog-layout"><aside className="filter-sidebar"><h2>Filtrar</h2>{["Especialidad", "Autor", "Editorial", "Edición", "Idioma", "Precio", "Disponibilidad"].map((label) => <details key={label}><summary>{label}</summary><p className="muted">Se activará con el catálogo real.</p></details>)}</aside><section>{filtered.length ? <div className="book-grid">{filtered.map((book) => <BookCard book={book} key={book.id} />)}</div> : <EmptyState title="Aún no hay libros publicados"><p>La taxonomía administrativa está disponible, pero el catálogo AMOLCA productivo sigue bloqueado.</p><Link className="button button-secondary" href="/">Volver al inicio</Link></EmptyState>}</section></div></div>;
}

