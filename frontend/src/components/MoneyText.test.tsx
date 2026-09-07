import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { MoneyText } from "@/components/MoneyText";

describe("MoneyText", () => {
  it("muestra dólares sin símbolo ambiguo", () => {
    render(<MoneyText value={{ amount: 65, currency: "USD" }} />);
    expect(screen.getByLabelText("USD 65.00")).toHaveTextContent("US$ 65.00");
  });

  it("muestra soles con el prefijo comercial", () => {
    render(<MoneyText value={{ amount: 250, currency: "PEN" }} />);
    expect(screen.getByLabelText("PEN 250.00")).toHaveTextContent("S/ 250.00");
  });
});
