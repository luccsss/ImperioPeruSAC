import type { Money } from "@/lib/types";

export function formatMoney(value: Money): string {
  const prefix = value.currency === "USD" ? "US$" : "S/";
  return `${prefix} ${new Intl.NumberFormat("en-US", { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value.amount)}`;
}

