import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import { MegaMenu } from "@/components/MegaMenu";

const nodes = [{ id: "root", code: "medicine", label: "Medicina", href: "/explorar?categoria=medicine", rootType: "MEDICINE" as const, sortOrder: 0,
  children: [{ id: "cardio", code: "cardio", label: "Cardiología", href: "/explorar?categoria=cardio", rootType: "MEDICINE" as const, sortOrder: 1, children: [] }] }];

describe("MegaMenu", () => {
  it("construye el menú desde nodos administrativos", () => {
    render(<MegaMenu nodes={nodes} />);
    fireEvent.mouseEnter(screen.getByRole("button", { name: /Categorías/ }).parentElement!);
    expect(screen.getByRole("link", { name: "Cardiología" })).toHaveAttribute("href", "/explorar?categoria=cardio");
  });

  it("muestra las cuatro líneas y cambia las subcategorías al pasar el mouse", () => {
    render(<MegaMenu nodes={nodes} />);
    fireEvent.mouseEnter(screen.getByRole("button", { name: /Categorías/ }).parentElement!);
    fireEvent.mouseEnter(screen.getByRole("tab", { name: /Publicidad/ }));
    expect(screen.getByRole("link", { name: "Carteles" })).toHaveAttribute("href", "/publicidad");
    expect(screen.getByRole("link", { name: "Sellos" })).toBeInTheDocument();
  });

  it("abre y cierra el drawer accesible", () => {
    render(<MegaMenu nodes={nodes} />);
    fireEvent.click(screen.getByRole("button", { name: "Abrir menú" }));
    expect(screen.getByRole("dialog", { name: "Menú principal" })).toBeInTheDocument();
    fireEvent.click(screen.getByRole("button", { name: "Cerrar menú" }));
    expect(screen.queryByRole("dialog")).not.toBeInTheDocument();
  });
});
