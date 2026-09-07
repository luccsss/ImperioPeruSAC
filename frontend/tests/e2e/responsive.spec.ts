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
  await expect(page.getByRole("dialog", { name: "Menú principal" })).toBeVisible();
  await page.getByRole("button", { name: "Cerrar menú" }).click();
  await expect(page.getByRole("dialog", { name: "Menú principal" })).toBeHidden();
});

test("las líneas futuras se declaran sin simular catálogo", async ({ page }) => {
  await page.goto("/tecnologia");
  await expect(page.getByRole("heading", { level: 1 })).toContainText("Tecnología");
  await expect(page.getByText(/se activará cuando/)).toBeVisible();
});
