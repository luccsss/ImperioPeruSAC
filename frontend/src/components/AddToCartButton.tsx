"use client";

import { useState } from "react";
import type { Money } from "@/lib/types";

type CartItem = { slug: string; title: string; price: Money; quantity: number };

export function AddToCartButton({ slug, title, price, disabled }: { slug: string; title: string; price: Money; disabled?: boolean }) {
  const [added, setAdded] = useState(false);
  function add() {
    const current = JSON.parse(localStorage.getItem("imperio-cart-v1") ?? "[]") as CartItem[];
    const existing = current.find((item) => item.slug === slug);
    if (existing) existing.quantity += 1;
    else current.push({ slug, title, price, quantity: 1 });
    localStorage.setItem("imperio-cart-v1", JSON.stringify(current));
    setAdded(true);
  }
  return <button className="button button-primary add-cart" type="button" onClick={add} disabled={disabled}>{disabled ? "Sin stock" : added ? "Añadido al carrito ✓" : "Agregar al carrito"}</button>;
}

