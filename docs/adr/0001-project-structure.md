# ADR 0001 — Estructura de Fase 2A

## Estado

Aceptada para Fase 2A.

## Decisión

El repositorio contiene dos aplicaciones independientes:

- `frontend/`: Next.js, React y TypeScript.
- `backend/`: Java y Spring Boot.

No se adopta tooling de monorepo. Cada aplicación conserva su propio manifiesto, build, pruebas, imagen Docker y ciclo de despliegue. La raíz contiene únicamente documentación, variables de ejemplo y el entorno local de integración.

## Razón

El frontend público y la API tienen necesidades de ejecución, escalado y despliegue diferentes. Compartir un orquestador de paquetes no aporta valor en Fase 2A y aumentaría el acoplamiento. El contrato estable entre ambos es HTTP bajo `/api/v1`, documentado mediante OpenAPI.

## Restricciones de alcance

- `/libro/{slug}/` es la única forma pública de producto congelada.
- Las rutas de hubs y categorías permanecen configurables y no se publican como definitivas.
- No se importa ni publica el catálogo AMOLCA productivo.
- No se implementa scraping.
- Los fixtures de desarrollo deben estar desactivados por defecto y claramente identificados.

