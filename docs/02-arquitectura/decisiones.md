# Registro de Decisiones Arquitectónicas (ADR)

Formato: contexto → decisión → consecuencias. Cada decisión queda versionada en Git
para justificar el diseño ante la evaluación.

---

## ADR-001 — Backend en Scala 3 con http4s, Tapir y Cats Effect

**Estado:** aceptada
**Contexto:** el equipo eligió un lenguaje funcional para el backend. Se necesita un
framework que favorezca funciones puras, efectos explícitos y un contrato de API
mantenible.
**Decisión:** usar `http4s` como servidor, `Tapir` para describir endpoints,
`Cats Effect` para efectos y `Circe` para JSON. La documentación OpenAPI se genera
desde las definiciones de Tapir.
**Consecuencias:**
- (+) El contrato REST se deriva del código y no se desincroniza.
- (+) Composición funcional y pruebas sin efectos secundarios.
- (−) Curva de aprendizaje de Cats Effect para el equipo.
- (−) Menos tutoriales que Spring Boot; se requiere disciplina.

---

## ADR-002 — Supabase como plataforma de datos y autenticación

**Estado:** aceptada
**Contexto:** se necesita Postgres administrado, autenticación y despliegue rápido sin
operar infraestructura propia.
**Decisión:** usar Supabase para PostgreSQL, Auth (GoTrue), Storage y RLS.
**Consecuencias:**
- (+) Menos tiempo en infraestructura y seguridad de credenciales.
- (+) RLS permite defensa en profundidad.
- (−) Dependencia de un proveedor; el esquema debe poder migrarse a Postgres puro.
- **Regla:** la `service_role` key nunca se expone en el frontend.

---

## ADR-003 — Acceso a datos con Skunk (no PostgREST para el dominio)

**Estado:** aceptada
**Contexto:** Supabase expone PostgREST, pero las reglas de negocio y transacciones
deben vivir en la API.
**Decisión:** la API Scala se conecta a Postgres con `Skunk` y mantiene repositorios
por agregado. El frontend no consume PostgREST para operaciones de dominio.
**Consecuencias:**
- (+) Lógica transaccional y validaciones centralizadas.
- (+) Tipado de SQL en tiempo de compilación.
- (−) Hay que escribir SQL/mapeos; no se aprovecha el CRUD automático de PostgREST.

---

## ADR-004 — Arquitectura hexagonal (puertos y adaptadores)

**Estado:** aceptada
**Contexto:** se exige separación de responsabilidades y componentes reutilizables.
**Decisión:** organizar el backend en `domain` (modelo y reglas puras), `application`
(casos de uso), `adapters` (HTTP, persistencia, auth, reportes) e `infrastructure`
(configuración, DI). Las dependencias apuntan hacia el dominio.
**Consecuencias:**
- (+) Dominio testeable sin base de datos ni HTTP.
- (+) Adaptadores intercambiables (p. ej. cambiar Skunk por Doobie).
- (−) Más archivos y ceremonia que un CRUD directo.

---

## ADR-005 — Reportes: Metabase + JasperReports

**Estado:** aceptada
**Contexto:** el módulo de Reportería requiere dashboards en tiempo real y documentos
PDF formales (facturas, órdenes, estados financieros).
**Decisión:** Metabase (conexión de solo lectura a Postgres) para dashboards y análisis;
JasperReports para PDFs con formato corporativo.
**Consecuencias:**
- (+) Dashboards en horas, sin desarrollar cada gráfico.
- (+) JasperReports es estándar y cumple el requisito académico.
- (−) Dos herramientas que mantener; Metabase debe usar un rol de solo lectura.

---

## ADR-006 — GitHub Flow + Conventional Commits

**Estado:** aceptada
**Contexto:** equipo pequeño y entregas por sprints; la rúbrica exige ramas
organizadas, `main` estable y commits descriptivos.
**Decisión:** usar **GitHub Flow**: `main` protegida, ramas `feat/*`, `fix/*`,
`docs/*`, `chore/*` de vida corta y Pull Requests con revisión. Commits en formato
**Conventional Commits**. Se conserva `develop` solo si el curso lo exige; en caso
contrario se elimina para evitar duplicidad de integración.
**Consecuencias:**
- (+) Historial legible y trazable a requisitos.
- (+) Menos merges conflictivos que Git Flow completo.
- (−) Requiere disciplina en el tamaño de las ramas.

---

## ADR-007 — Documentación como código

**Estado:** aceptada
**Contexto:** los entregables incluyen SRS, SAD, manuales y diagramas UML.
**Decisión:** Markdown en `docs/`, PlantUML para UML, OpenAPI generado por Tapir y
MkDocs Material como sitio navegable. Los diagramas se renderizan en CI y se versionan.
**Consecuencias:**
- (+) Documentación revisable en PRs y sin dependencia de herramientas propietarias.
- (+) Diagramas reproducibles.
- (−) El equipo debe aprender la sintaxis de PlantUML.

---

## ADR-008 — `SSL.Trusted` para Postgres en desarrollo

**Estado:** aceptada (temporal)
**Contexto:** el JDK 21 no confía en la CA del pooler de Supabase y la conexión falla
con `SunCertPathBuilderException`. El proyecto es académico y se conecta al proyecto
de Supabase Cloud.
**Decisión:** usar `SSL.Trusted` en `DbPool` durante el desarrollo para aceptar el
certificado del servidor sin validar la cadena.
**Consecuencias:**
- (+) Permite avanzar con la integración y las pruebas end-to-end.
- (−) No valida el certificado: **no usar en producción**.
- **Acción futura:** en producción cambiar a `SSL.System` e importar la CA de Supabase
  en el truststore del contenedor.
