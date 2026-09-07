import Link from "next/link";

export default function NotFound() {
  return <div className="container section"><div className="empty"><div><p className="eyebrow">404</p><h1>Esta página no está disponible</h1><p className="muted">La URL puede haber cambiado o el libro todavía no está publicado.</p><Link className="button button-primary" href="/">Volver al inicio</Link></div></div></div>;
}

