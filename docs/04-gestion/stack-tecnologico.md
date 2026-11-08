# Stack tecnológico y justificación

## Frontend

- **Angular 17+** con componentes standalone, `Signals` y `RxJS`.
- **TypeScript** estricto (`strict: true`).
- **Angular Material** o **Tailwind CSS** para UI (decidir en Fase 3).
- Cliente Supabase (`@supabase/supabase-js`) **solo para Auth** y Storage.
- Consumo de la API vía `HttpClient` con interceptores para el JWT.

## Backend (API REST)

- **Scala 3** — lenguaje funcional, tipado estático, JVM.
- **http4s** — servidor HTTP funcional.
- **Tapir** — descripción de endpoints y generación de OpenAPI 3.1 + Swagger UI.
- **Cats Effect 3** — efectos y concurrencia.
- **Circe** — serialización JSON.
- **Skunk** — acceso a PostgreSQL funcional y type-safe.
- **MUnit / ScalaCheck** — pruebas unitarias y de propiedades.

Alternativas evaluadas: Play Framework (menos funcional, más pesado), Akka HTTP
(ecosistema en transición de licencia), ZIO HTTP (válida si el equipo adopta ZIO).

## Datos y autenticación

- **Supabase PostgreSQL 15+** como base relacional.
- **Migraciones** con Supabase CLI (`supabase migration new`), versionadas en Git.
- **Row Level Security (RLS)** habilitado en todas las tablas de dominio.
- **Supabase Auth (GoTrue)** para registro, login, recuperación y sesiones.
- La API valida el JWT con el JWKS de Supabase; nunca confía en el cliente.

## Reportería

- **Metabase** — dashboards y análisis (conexión de solo lectura, rol dedicado).
- **JasperReports** — PDFs formales (OC, facturas, estados financieros).

## Documentación

- **Markdown** en `docs/`.
- **MkDocs Material** — sitio navegable (`mkdocs serve`).
- **PlantUML** — diagramas UML versionados.
- **OpenAPI** — generado automáticamente por Tapir.
- **ADR** — decisiones arquitectónicas en `docs/02-arquitectura/decisiones.md`.

## DevOps

- **Docker** para empaquetar la API y JasperReports.
- **GitHub Actions** para lint, tests, cobertura, render de PlantUML y export de OpenAPI.
- Despliegue sugerido: frontend en Vercel/Netlify/Nginx; API en Render/Railway/Fly.io;
  Supabase administrado.
