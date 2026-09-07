"use client";

import { useRouter } from "next/navigation";
import { FormEvent, useState } from "react";

export default function AdminLoginPage() {
  const router = useRouter();
  const [error, setError] = useState("");
  const [pending, setPending] = useState(false);
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault(); setPending(true); setError("");
    const form = new FormData(event.currentTarget);
    const response = await fetch("/api/auth/login", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ email: form.get("email"), password: form.get("password") }) });
    if (!response.ok) { setError("Credenciales inválidas o servicio no disponible."); setPending(false); return; }
    router.push("/admin"); router.refresh();
  }
  return <div className="admin-login"><form className="admin-login-card" onSubmit={submit}><div className="brand"><span className="brand-mark">IP</span><span><strong>Imperio Perú</strong><small>Panel administrativo</small></span></div><div><p className="eyebrow">Acceso seguro</p><h1>Bienvenido</h1><p className="muted">Use la cuenta creada mediante variables de entorno.</p></div><label className="field"><span>Correo</span><input className="input" name="email" type="email" autoComplete="username" required /></label><label className="field"><span>Contraseña</span><input className="input" name="password" type="password" autoComplete="current-password" required /></label>{error && <p className="form-error" role="alert">{error}</p>}<button className="button button-primary" disabled={pending}>{pending ? "Verificando…" : "Ingresar"}</button></form></div>;
}

