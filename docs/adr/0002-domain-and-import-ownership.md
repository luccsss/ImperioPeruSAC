# ADR 0002 — Dominio bibliográfico, comercial e importación

## Decisión

- `Book` contiene identidad y datos bibliográficos.
- `BookOffer` contiene SKU, precio, promoción, disponibilidad y contenido comercial de Imperio Perú.
- `Publisher` representa la editorial y referencia una `Organization`.
- `CatalogSource` representa el origen técnico de un archivo o feed.
- `OrganizationRole` permite que una organización tenga varios roles sin confundirlos.
- `FieldOwnershipPolicy` define qué campos acepta una importación y cuáles requieren aprobación.

AMOLCA puede tener roles `PUBLISHER` y `CATALOG_SOURCE`. `SUPPLIER` no se infiere. Imperio Perú tiene roles `DISTRIBUTOR` y `SELLER`.

## Protección

Precio, promoción, stock, descripción comercial, slug, SEO, categorías editoriales, destacados y estado de publicación son campos protegidos. El importador genera diferencias y requiere una decisión administrativa antes de sustituirlos.

