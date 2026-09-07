import Image from "next/image";
import Link from "next/link";
import { mediaUrl } from "@/lib/api";
import type { Book } from "@/lib/types";
import { MoneyText } from "@/components/MoneyText";

export function BookCard({ book }: { book: Book }) {
  const cover = book.images.find((image) => image.type === "COVER") ?? book.images[0];
  return (
    <article className="book-card">
      <Link className="book-cover" href={book.canonicalPath} aria-label={`Ver ${book.title}`}>
        {cover ? <Image src={mediaUrl(cover.url)} alt={cover.altText} fill sizes="(max-width: 480px) 44vw, (max-width: 900px) 30vw, 220px" /> : <div className="cover-placeholder" aria-hidden><span>AMOLCA</span><strong>{book.title}</strong></div>}
        {book.newArrival && <span className="book-badge">Novedad</span>}
      </Link>
      <div className="book-card-body">
        <p className="book-meta">{book.publisher?.name ?? "Editorial por confirmar"}</p>
        <h3><Link href={book.canonicalPath}>{book.title}</Link></h3>
        <p className="book-authors">{book.authors.map((author) => author.name).join(", ")}</p>
        <div className="book-commercial"><MoneyText className="book-price" value={book.promotionalPrice ?? book.regularPrice} /><span className={`availability ${book.inventory?.available ? "in" : "out"}`}>{book.inventory?.available ? "En stock" : "Consultar stock"}</span></div>
      </div>
    </article>
  );
}

