import type { Metadata } from "next";
import Image from "next/image";
import Link from "next/link";
import { notFound, permanentRedirect } from "next/navigation";
import { AddToCartButton } from "@/components/AddToCartButton";
import { MoneyText } from "@/components/MoneyText";
import { getBook, getBookRedirect, mediaUrl } from "@/lib/api";

type Props = { params: Promise<{ slug: string }> };

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { slug } = await params;
  const book = await getBook(slug);
  if (!book) return { title: "Libro no disponible", robots: { index: false, follow: false } };
  const indexable = book.status === "PUBLISHED" && book.seo?.robotsPolicy === "INDEX";
  return {
    title: book.seo?.seoTitle ?? book.title,
    description: book.seo?.metaDescription ?? book.commercialDescription ?? book.bibliographicDescription,
    alternates: { canonical: book.canonicalPath },
    robots: { index: indexable, follow: indexable },
    openGraph: { title: book.seo?.ogTitle ?? book.title, description: book.seo?.ogDescription, images: book.seo?.ogImageUrl ? [book.seo.ogImageUrl] : [] }
  };
}

export default async function BookPage({ params }: Props) {
  const { slug } = await params;
  const book = await getBook(slug);
  if (!book) {
    const destination = await getBookRedirect(slug);
    if (destination) permanentRedirect(destination);
    notFound();
  }
  const cover = book.images.find((image) => image.type === "COVER") ?? book.images[0];
  const gallery = book.images.filter((image) => image.id !== cover?.id);
  const displayPrice = book.promotionalPrice ?? book.regularPrice;
  const inStock = (book.inventory?.available ?? 0) > 0;
  const schema = {
    "@context": "https://schema.org",
    "@type": "Product",
    name: book.title,
    description: book.commercialDescription ?? book.bibliographicDescription,
    image: book.images.map((image) => mediaUrl(image.url)),
    sku: book.sku,
    isbn: book.isbn13 ?? book.isbn10,
    brand: book.publisher ? { "@type": "Organization", name: book.publisher.name } : undefined,
    offers: {
      "@type": "Offer",
      price: displayPrice.amount.toFixed(2),
      priceCurrency: displayPrice.currency,
      availability: inStock ? "https://schema.org/InStock" : "https://schema.org/OutOfStock",
      url: `${process.env.NEXT_PUBLIC_SITE_URL ?? "http://localhost:3000"}${book.canonicalPath}`,
      seller: { "@type": "Organization", name: "Consorcio Imperio Perú SAC" }
    }
  };
  return (
    <div className="container section-tight">
      <nav className="breadcrumbs" aria-label="Breadcrumb"><Link href="/">Inicio</Link><span>›</span>{book.categories.find((category) => category.primary) && <><Link href={book.categories.find((category) => category.primary)!.href}>{book.categories.find((category) => category.primary)!.displayName}</Link><span>›</span></>}<span aria-current="page">{book.title}</span></nav>
      <div className="product-layout">
        <section className="product-media" aria-label="Imágenes del libro"><div className="product-cover">{cover ? <Image src={mediaUrl(cover.url)} alt={cover.altText} fill priority sizes="(max-width: 768px) 82vw, 42vw" /> : <div className="cover-placeholder product-placeholder"><span>AMOLCA</span><strong>{book.title}</strong></div>}</div>{gallery.length > 0 && <div className="product-thumbs">{gallery.map((image) => <Image key={image.id} src={mediaUrl(image.url)} alt={image.altText} width={90} height={110} />)}</div>}</section>
        <section className="product-info"><p className="eyebrow">{book.publisher?.name ?? "Libro especializado"}</p><h1>{book.title}</h1>{book.subtitle && <p className="product-subtitle">{book.subtitle}</p>}<p className="product-authors">Por {book.authors.map((author) => author.name).join(", ")}</p><div className="product-price"><MoneyText value={displayPrice} />{book.promotionalPrice && <del><MoneyText value={book.regularPrice} /></del>}</div><p className={`stock-line ${inStock ? "in" : "out"}`}><span aria-hidden>●</span>{inStock ? `${book.inventory?.available} disponibles` : "Sin stock disponible"}</p><AddToCartButton slug={book.slug} title={book.title} price={displayPrice} disabled={!inStock} /><p className="price-note">Precio comercial en USD. El cargo se procesará en la moneda indicada.</p><dl className="quick-facts"><div><dt>ISBN</dt><dd>{book.isbn13 ?? book.isbn10 ?? "Por confirmar"}</dd></div><div><dt>Edición</dt><dd>{book.edition ?? "Por confirmar"}</dd></div><div><dt>Formato</dt><dd>{book.bindingFormat ?? "Por confirmar"}</dd></div><div><dt>Idioma</dt><dd>{book.language ?? "Por confirmar"}</dd></div></dl></section>
      </div>
      <section className="product-content"><details open><summary>Descripción</summary><div><p>{book.commercialDescription ?? book.bibliographicDescription ?? "Descripción pendiente de contenido autorizado y revisión editorial."}</p></div></details><details><summary>Información técnica</summary><div><dl className="technical-list"><dt>Editorial</dt><dd>{book.publisher?.name ?? "Por confirmar"}</dd><dt>Año</dt><dd>{book.publicationYear ?? "Por confirmar"}</dd><dt>Páginas</dt><dd>{book.pages ?? "Por confirmar"}</dd><dt>SKU</dt><dd>{book.sku}</dd></dl></div></details><details><summary>Categorías</summary><div className="cluster">{book.categories.map((category) => <Link className="chip" key={category.id} href={category.href}>{category.displayName}</Link>)}</div></details></section>
      <script type="application/ld+json" dangerouslySetInnerHTML={{ __html: JSON.stringify(schema).replaceAll("<", "\\u003c") }} />
    </div>
  );
}
