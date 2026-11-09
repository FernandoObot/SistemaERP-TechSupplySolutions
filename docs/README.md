# Documentación — Sistema ERP TechSupply Solutions

Índice de la documentación del proyecto académico. La **Fase 1** (análisis y diseño
arquitectónico) está contenida en `01-requisitos/` y `02-arquitectura/`; la **Fase 2**
(componentes reutilizables) en `02-arquitectura/componentes/`; la **Fase 3**
(desarrollo incremental) en `05-desarrollo/`.

## Estructura

```
docs/
├── 01-requisitos/
│   ├── entrevistas.md           # Entrevistas simuladas con stakeholders
│   ├── procesos.md              # Procesos AS-IS y TO-BE (5 procesos clave)
│   ├── srs.md                   # Especificación de requisitos (SRS)
│   ├── casos-de-uso.md          # Actores, casos de uso y especificaciones
│   └── fuentes/                 # Narrativa y rúbrica originales del curso
├── 02-arquitectura/
│   ├── sad.md                   # Documento de arquitectura (4+1 vistas)
│   ├── decisiones.md            # ADR: decisiones arquitectónicas
│   └── componentes/             # Fase 2: 7 componentes reutilizables
│       ├── README.md            # Índice y matriz de reutilización
│       └── 01..07-*.md          # Contrato de cada componente
├── 03-diagramas/
│   ├── plantuml/                # Fuente .puml versionada
│   └── exportados/              # PNG/SVG generados (CI o local)
├── 04-gestion/
│   ├── ramas-y-commits.md       # Git Flow / GitHub Flow y Conventional Commits
│   └── stack-tecnologico.md     # Resumen del stack y justificación
└── 05-desarrollo/
    ├── sprint-0.md              # Base del proyecto, contrato y arranque
    └── sprint-1-2.md            # Maestra + Compras sobre Supabase
```

## Stack decidido

| Capa | Tecnología | Justificación |
|---|---|---|
| Frontend | **Angular 22 (standalone) + TypeScript** | Requisito del equipo; tipado fuerte, inyección de dependencias, soporte empresarial. |
| API / Backend | **Scala 3.9 + http4s + Tapir + Cats Effect + Circe** | Programación funcional pura, efectos controlados, endpoints tipados. |
| Persistencia | **Supabase (PostgreSQL 15+) + Skunk** | Postgres administrado, migraciones y RLS; Skunk es un cliente funcional y type-safe. |
| Autenticación | **Supabase Auth (GoTrue) + JWT** | Login, registro, recuperación y sesiones sin construir un IdP propio. El backend valida el JWT (JWKS). |
| Reportes | **Metabase** (dashboards) + **JasperReports** (PDF formales) | Metabase entrega inteligencia de negocio rápida; JasperReports cubre reportes imprimibles. |
| Documentación | **Markdown + MkDocs Material + PlantUML + OpenAPI** | Documentación versionada y navegable; OpenAPI se genera desde Tapir. |
| Versionado | **Git + GitHub, GitHub Flow + Conventional Commits** | Ramas cortas, PRs revisados, historial semántico y `main` estable. |
| CI/CD | **GitHub Actions** | Lint, pruebas, cobertura, exportación de OpenAPI y render de PlantUML. |

### ¿Por qué Tapir y no Play/Akka HTTP/ZIO HTTP?

`Tapir` es una librería de **descripción de endpoints** independiente del servidor.
Permite declarar el contrato una vez y derivar de ahí: implementación en `http4s`,
**OpenAPI 3.1** (Swagger), cliente HTTP tipado y documentación. Esto cumple el
requisito de documentación de API y evita que el contrato se desincronice del código.
Alternativa válida si el equipo prefiere ZIO: `zio-http` + `tapir-zio`.

### Nota sobre Supabase y la API

El frontend usa `@supabase/supabase-js` **solo** para autenticación y, si se decide,
Storage. Las reglas de negocio viven en la API Scala; el frontend **no** debe escribir
directamente en las tablas del dominio. La API se conecta a Postgres (Skunk) y valida
el JWT emitido por Supabase. Las políticas RLS actúan como defensa en profundidad.
