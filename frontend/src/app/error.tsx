"use client";

export default function ErrorPage({ reset }: { error: Error & { digest?: string }; reset: () => void }) {
  return <div className="container section"><div className="empty"><div><p className="eyebrow">Ocurrió un problema</p><h1>No pudimos cargar esta sección</h1><p className="muted">Puede intentarlo nuevamente sin perder su navegación.</p><button className="button button-primary" onClick={reset}>Reintentar</button></div></div></div>;
}

