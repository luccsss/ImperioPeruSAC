import Link from "next/link";
import { BooksTable } from "@/components/admin/BooksTable";
export default function AdminBooksPage() { return <div className="admin-page"><div className="admin-page-heading"><div><h1>Libros</h1><p>Bibliografía y oferta comercial permanecen separadas dentro de cada registro.</p></div><Link className="button button-primary" href="/admin/libros/nuevo">＋ Nuevo libro</Link></div><BooksTable /></div>; }

