import { NextResponse } from "next/server";

export async function POST(request: Request) {
  const body: unknown = await request.json();
  const response = await fetch(`${process.env.BACKEND_URL ?? "http://localhost:8080"}/api/v1/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json", Accept: "application/json" },
    body: JSON.stringify(body),
    cache: "no-store"
  });
  const payload: unknown = await response.json().catch(() => ({ detail: "No se pudo iniciar sesión" }));
  if (!response.ok) return NextResponse.json(payload, { status: response.status });
  const token = payload as { accessToken: string; expiresAt: string; displayName: string; authorities: string[] };
  const result = NextResponse.json({ displayName: token.displayName, authorities: token.authorities });
  result.cookies.set("imperio_admin_token", token.accessToken, {
    httpOnly: true,
    sameSite: "lax",
    secure: process.env.NODE_ENV === "production",
    path: "/",
    expires: new Date(token.expiresAt)
  });
  return result;
}

