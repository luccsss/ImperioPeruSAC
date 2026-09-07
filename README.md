# Consorcio Imperio Perú SAC — Plataforma de catálogo

Fundaciones de Fase 2A para el catálogo de libros médicos, con Tecnología y Publicidad preservadas como líneas comerciales independientes todavía no activas. Esta entrega no importa, publica ni sincroniza el catálogo productivo de AMOLCA.

## Estructura

```text
backend/     API y administrador de dominio — Spring Boot 4 / Java 25
frontend/    Web pública y panel — Next.js 16 / React 19 / TypeScript
docs/        ADR, contrato de importación y corte para revisión
compose.yaml PostgreSQL + backend + frontend para desarrollo reproducible
```

Se eligió un solo repositorio para coordinar contratos y ejecución local, pero sin workspace manager ni acoplamiento de builds. Cada aplicación conserva dependencias, Dockerfile y ciclo de despliegue propios. La decisión está documentada en `docs/adr/0001-project-structure.md`.

## Inicio rápido con Docker

Requisitos: Docker Desktop con Compose v2.

1. Copie `.env.example` a `.env` y cambie todas las credenciales.
2. Mantenga `IMPORT_APPLY_ENABLED=false` durante esta fase.
3. Ejecute:

```powershell
docker compose up --build
```

Servicios:

- Web: `http://localhost:3000`
- Administración: `http://localhost:3000/admin/login`
- API: `http://localhost:8080/api/v1`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Swagger UI: `http://localhost:8080/docs`
- Salud: `http://localhost:8080/actuator/health`

Flyway aplica automáticamente `V1__initial_schema.sql` y `V2__seed_organizations_and_taxonomy.sql` sobre una base nueva. Los volúmenes `postgres-data` y `media-storage` son persistentes.

## Ejecución local sin contenedores

Requisitos exactos usados en la verificación: PostgreSQL 18.4, JDK 25, Maven 3.9.11, Node.js 24 y npm 11.

Backend:

```powershell
cd backend
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/imperio_peru"
$env:DATABASE_USERNAME="imperio"
$env:DATABASE_PASSWORD="su-clave-local"
$env:JWT_SECRET="una-clave-aleatoria-de-al-menos-32-caracteres"
$env:ADMIN_EMAIL="admin@imperioperu.local"
$env:ADMIN_PASSWORD="una-clave-administrativa-segura"
$env:IMPORT_APPLY_ENABLED="false"
mvn spring-boot:run
```

Frontend, en otra terminal:

```powershell
cd frontend
$env:BACKEND_URL="http://localhost:8080"
$env:NEXT_PUBLIC_SITE_URL="http://localhost:3000"
npm ci
npm run dev
```

El administrador inicial se crea una sola vez a partir de las variables de entorno. Cambiar las variables después no modifica una cuenta existente.

## Verificación

Cada `push` o pull request hacia `main` ejecuta automáticamente las validaciones de backend y frontend mediante GitHub Actions, incluyendo las pruebas responsivas del menú en Chromium.

```powershell
cd backend
mvn test

cd ../frontend
npm ci
npm run typecheck
npm run lint
npm test
npx playwright install chromium
npm run test:responsive
npm run build
npm audit --audit-level=high
```

La prueba `PostgresMigrationIntegrationTest` usa Testcontainers cuando Docker está disponible y valida también los conteos 32/15 de la taxonomía. Sin Docker se omite de forma explícita; las demás pruebas siguen ejecutándose.

## Importación segura

Use [docs/import-template.csv](docs/import-template.csv) como contrato de ejemplo. La carga se limita a CSV/XLSX, rechaza fórmulas, normaliza encabezados, calcula SHA-256 y crea una previsualización persistente con matching y diferencias. Los campos comerciales de Imperio —SKU, precio, stock, descripciones comerciales, SEO, categorías y estado— están protegidos.

Aunque un lote sea aprobado, `/apply` responde con error mientras `IMPORT_APPLY_ENABLED=false`. No cambie esa bandera sin una aprobación comercial posterior.

## Límites vigentes

El corte exacto y los puntos que requieren nueva aprobación están en [docs/PHASE-2A-REVIEW.md](docs/PHASE-2A-REVIEW.md). Esta entrega se detiene en Fase 2A.
