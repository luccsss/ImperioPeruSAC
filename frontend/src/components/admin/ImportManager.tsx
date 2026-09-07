"use client";

import { FormEvent, useEffect, useState } from "react";
import type { ImportBatch } from "@/lib/types";

export function ImportManager() {
  const [batches, setBatches] = useState<ImportBatch[]>([]);
  const [selected, setSelected] = useState<ImportBatch | null>(null);
  const [pending, setPending] = useState(false);
  const [message, setMessage] = useState("");
  async function load() { const response = await fetch("/api/backend/imports"); if (!response.ok) throw new Error(); setBatches(await response.json() as ImportBatch[]); }
  useEffect(() => { fetch("/api/backend/imports").then(async (response) => { if (!response.ok) throw new Error(); return response.json() as Promise<ImportBatch[]>; }).then(setBatches).catch(() => setMessage("No fue posible cargar los lotes.")); }, []);
  async function preview(event: FormEvent<HTMLFormElement>) {
    event.preventDefault(); setPending(true); setMessage("");
    const response = await fetch("/api/backend/imports/preview?sourceCode=AMOLCA_FILE", { method: "POST", body: new FormData(event.currentTarget) });
    if (!response.ok) { const problem = await response.json().catch(() => ({})) as { detail?: string }; setMessage(problem.detail ?? "El archivo no pudo previsualizarse."); setPending(false); return; }
    const batch = await response.json() as ImportBatch; setSelected(batch); setPending(false); await load();
  }
  async function details(id: string) { const response = await fetch(`/api/backend/imports/${id}`); if (response.ok) setSelected(await response.json() as ImportBatch); }
  async function decide(approve: boolean) {
    if (!selected) return;
    const rows = selected.rows.filter((row) => row.disposition === "NEEDS_REVIEW");
    if (!rows.length) { setMessage("Este lote no tiene filas pendientes de decisión manual."); return; }
    const response = await fetch(`/api/backend/imports/${selected.id}/decisions`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ decisions: rows.map((row) => ({ rowId: row.id, approve })) }) });
    if (response.ok) { setSelected(await response.json() as ImportBatch); await load(); setMessage(approve ? "Cambios propuestos aprobados para revisión técnica." : "Cambios propuestos rechazados."); }
  }
  return <div className="import-layout"><section className="form-section"><div className="form-section-heading"><p className="eyebrow">Adapter de archivo</p><h2>Previsualizar CSV o XLSX</h2><p>No altera el catálogo. La aplicación productiva está deshabilitada en Fase 2A.</p></div><form className="stack" onSubmit={preview}><label className="field"><span>Archivo autorizado (máx. 10 000 filas)</span><input className="input file-input" type="file" name="file" accept=".csv,.xlsx,text/csv,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" required /></label><button className="button button-primary" disabled={pending}>{pending ? "Analizando…" : "Generar previsualización"}</button></form>{message && <div className="notice" role="status">{message}</div>}<div className="batch-list"><h3>Lotes recientes</h3>{batches.map((batch) => <button type="button" onClick={() => details(batch.id)} key={batch.id}><span><strong>{batch.filename}</strong><small>{new Date(batch.createdAt).toLocaleString("es-PE")}</small></span><span className="chip">{batch.status}</span></button>)}</div></section>
    <section className="import-preview">{!selected ? <div className="empty"><div><h2>Sin lote seleccionado</h2><p className="muted">Seleccione un historial o cargue un archivo para revisar diferencias.</p></div></div> : <><div className="admin-page-heading"><div><p className="eyebrow">Vista previa segura</p><h2>{selected.filename}</h2><p>{selected.totalRows} filas · {selected.validRows} válidas · {selected.errorRows} con error</p></div><div className="cluster"><button className="button button-secondary" onClick={() => decide(false)}>Rechazar pendientes</button><button className="button button-primary" onClick={() => decide(true)}>Aprobar pendientes</button></div></div><div className="notice">Aplicar permanece bloqueado por <code>IMPORT_APPLY_ENABLED=false</code>. Esta pantalla no publica ni crea registros productivos.</div><div className="table-shell"><table><thead><tr><th>Fila</th><th>Resultado</th><th>Coincidencia</th><th>Validación / diferencias</th></tr></thead><tbody>{selected.rows.map((row) => <tr key={row.id}><td>{row.rowNumber}</td><td><span className="chip">{row.disposition}</span></td><td>{row.matchType ?? "—"}</td><td><code className="diff-code">{row.validationMessages !== "[]" ? row.validationMessages : row.proposedChanges}</code></td></tr>)}</tbody></table></div></>}</section></div>;
}
