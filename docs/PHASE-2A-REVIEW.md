# Revisión de Fase 2A

Fecha de corte: 28 de agosto de 2026.

## Alcance entregado

- Repositorio único sin tooling de monorepo; `frontend` y `backend` son aplicaciones separadas y desplegables de manera independiente.
- Monolito modular Spring Boot con catálogo, organizaciones, SEO, inventario, importación, medios, autenticación y auditoría.
- Separación `Book` / `BookOffer`; todos los precios de libros conservan `amount` y `currency=USD`.
- Autores many-to-many, `Publisher` separado de `CatalogSource`, categorías jerárquicas y libros many-to-many mediante `ItemCategory` con una sola principal.
- 32 categorías médicas, 15 odontológicas y 2 raíces sembradas como taxonomía administrativa. `publicPath` queda vacío y los slugs son candidatos.
- URL canónica estable de producto `/libro/{slug}/`, soporte de redirect histórico y metadatos SEO editables.
- Inventario real con reserva, disponible, umbral, bloqueo optimista e historial de movimientos.
- Administrador para libros, categorías e importaciones, con roles, permisos operativos y auditoría.
- Adaptador local CSV/XLSX con checksum, límites, validación, matching, diff y aprobación. Aplicación productiva deshabilitada.
- Infraestructura de imágenes con comprobación de firma, límites de tamaño, autorización y metadatos.
- Next.js App Router, TypeScript estricto, Server Components por defecto, diseño responsive, navegación derivada de la taxonomía, home, ficha de libro y estados vacíos/error/carga.

## Bloqueos preservados

- No se importó el catálogo productivo de AMOLCA.
- No se implementó scraping, feed, API externa ni sincronización automática.
- No se congelaron URLs públicas de categorías ni se generó SEO masivo.
- No se publicaron libros en masa, redirects reales, sitemap productivo ni integración con Search Console.
- Checkout y pagos continúan fuera de Fase 2A. La arquitectura monetaria aprobada de pedidos separados por moneda no fue alterada.
- Tecnología y Publicidad se muestran como líneas futuras sin inventar productos ni precios.

## Evidencia de verificación

- Backend: compilación y 11 pruebas aprobadas localmente; una prueba adicional de migraciones PostgreSQL queda preparada y se omite automáticamente cuando Docker no está disponible.
- Frontend: TypeScript estricto, ESLint, 4 pruebas de componentes y build de producción aprobados.
- Responsive: 8 escenarios Playwright aprobados en móvil, tablet y escritorio; 1 escenario de menú se omite deliberadamente en escritorio.
- Dependencias npm: auditoría con 0 vulnerabilidades conocidas al cierre.

## Decisiones que requieren aprobación posterior

- Taxonomía SEO pública y URLs finales de categorías.
- Reglas de publicación y lote productivo inicial.
- Derechos y procedencia de imágenes y textos comerciales.
- Activación de la aplicación de importaciones.
- Infraestructura de producción, pasarela de pago y avance a checkout/pedidos separados por moneda.
