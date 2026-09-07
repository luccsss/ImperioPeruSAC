import type { Book, NavigationNode } from "@/lib/types";

const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

async function publicRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${backendUrl}${path}`, {
    ...init,
    headers: { Accept: "application/json", ...init?.headers },
    ...(init?.cache === "no-store" ? {} : { next: { revalidate: 60, tags: ["catalog"] } })
  });
  if (!response.ok) throw new Error(`Backend request failed with ${response.status}`);
  return response.json() as Promise<T>;
}

export async function getNavigation(): Promise<NavigationNode[]> {
  try { return await publicRequest<NavigationNode[]>("/api/v1/public/navigation", { cache: "no-store" }); }
  catch { return []; }
}

export async function getPublishedBooks(limit = 12): Promise<Book[]> {
  try { return await publicRequest<Book[]>(`/api/v1/public/books?limit=${limit}`); }
  catch { return []; }
}

export async function getBook(slug: string): Promise<Book | null> {
  try { return await publicRequest<Book>(`/api/v1/public/books/${encodeURIComponent(slug)}`); }
  catch { return null; }
}

export async function getBookRedirect(slug: string): Promise<string | null> {
  try {
    const value = await publicRequest<{ destinationPath: string; statusCode: number }>(`/api/v1/public/redirects/resolve?path=${encodeURIComponent(`/libro/${slug}/`)}`);
    return value.statusCode === 301 || value.statusCode === 308 ? value.destinationPath : null;
  } catch { return null; }
}

export function mediaUrl(value: string): string {
  if (/^https?:\/\//.test(value)) return value;
  return `${backendUrl}${value}`;
}
