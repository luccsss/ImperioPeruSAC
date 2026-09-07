export default function Loading() {
  return <div className="container section" aria-live="polite"><div className="loading-block" /><div className="loading-grid">{Array.from({ length: 4 }, (_, index) => <div className="loading-card" key={index} />)}</div></div>;
}

