import { cookies } from "next/headers";
import { NextResponse } from "next/server";

async function proxy(request: Request, context: { params: Promise<{ path: string[] }> }) {
  const { path } = await context.params;
  const token = (await cookies()).get("imperio_admin_token")?.value;
  if (!token) return NextResponse.json({ detail: "Autenticación requerida" }, { status: 401 });
  const requestUrl = new URL(request.url);
  const target = new URL(`/api/v1/admin/${path.join("/")}`, process.env.BACKEND_URL ?? "http://localhost:8080");
  target.search = requestUrl.search;
  const headers = new Headers({ Authorization: `Bearer ${token}`, Accept: "application/json" });
  const contentType = request.headers.get("content-type");
  if (contentType) headers.set("Content-Type", contentType);
  const body = request.method === "GET" || request.method === "HEAD" ? undefined : await request.arrayBuffer();
  const response = await fetch(target, { method: request.method, headers, body, cache: "no-store" });
  const outputHeaders = new Headers();
  outputHeaders.set("Content-Type", response.headers.get("content-type") ?? "application/json");
  return new NextResponse(response.body, { status: response.status, headers: outputHeaders });
}

export const GET = proxy;
export const POST = proxy;
export const PUT = proxy;
export const PATCH = proxy;
export const DELETE = proxy;

