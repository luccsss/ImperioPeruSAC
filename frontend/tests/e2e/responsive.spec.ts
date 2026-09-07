import { expect, test } from "@playwright/test";

test("la portada no produce desbordamiento horizontal", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByRole("heading", { level: 1 })).toBeVisible();
  const overflow = await page.evaluate(() => document.documentElement.scrollWidth > document.documentElement.clientWidth);
  expect(overflow).toBe(false);
});

test("el menú móvil se puede abrir y cerrar", async ({ page }, testInfo) => {
  test.skip(testInfo.project.name === "desktop", "El escritorio usa mega menú");
  await page.goto("/");
  await page.getByRole("button", { name: "Abrir menú" }).click();
  const drawer = page.getByRole("dialog", { name: "Menú principal" });
  await expect(drawer).toBeVisible();
  await expect(drawer.locator("summary")).toHaveText(["Medicina+", "Odontología+", "Tecnología+", "Publicidad+"]);
  await drawer.locator("summary").filter({ hasText: "Publicidad" }).click();
  await expect(drawer.getByRole("link", { name: "Carteles" })).toBeVisible();
  await expect(drawer.getByRole("link", { name: "Liquidación" })).toBeVisible();
  await page.getByRole("button", { name: "Cerrar menú" }).click();
  await expect(drawer).toBeHidden();
});

test("el mega menú de escritorio muestra líneas, subcategorías y vistas", async ({ page }, testInfo) => {
  test.skip(testInfo.project.name !== "desktop", "El mega menú solo se muestra en escritorio");
  await page.goto("/");

  const categoriesButton = page.getByRole("button", { name: /Categorías/ });
  await categoriesButton.hover();
  await expect(categoriesButton).toHaveAttribute("aria-expanded", "true");

  const catalogTabs = page.getByRole("tablist", { name: "Líneas del catálogo de libros" });
  await expect(catalogTabs.getByRole("tab")).toHaveText(["Medicina›", "Odontología›", "Tecnología›", "Publicidad›"]);
  await catalogTabs.getByRole("tab", { name: /Publicidad/ }).hover();
  await expect(page.getByRole("tabpanel").getByRole("link", { name: "Carteles" })).toBeVisible();
  await expect(page.getByRole("tabpanel").getByRole("link", { name: "Sellos" })).toBeVisible();

  const primaryViews = page.getByRole("navigation", { name: "Vistas principales" });
  await expect(primaryViews.getByRole("link")).toHaveText(["Inicio", "Nosotros", "Eventos", "Liquidación"]);
});

test("las líneas futuras se declaran sin simular catálogo", async ({ page }) => {
  await page.goto("/tecnologia");
  await expect(page.getByRole("heading", { level: 1 })).toContainText("Tecnología");
  await expect(page.getByText(/se activará cuando/)).toBeVisible();
});
