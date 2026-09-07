import { formatMoney } from "@/lib/money";
import type { Money } from "@/lib/types";

export function MoneyText({ value, className }: { value: Money; className?: string }) {
  return <span className={className} aria-label={`${value.currency} ${value.amount.toFixed(2)}`}>{formatMoney(value)}</span>;
}

