# Sistema ERP — TechSupply Solutions

Proyecto final de la materia de Diseño de Software. ERP para una distribuidora de
componentes electrónicos y suministros de TI, construido con **Angular + Scala
funcional + Supabase**.

## Estado del proyecto

| Fase | Descripción | Estado |
|---|---|---|
| 1 | Análisis y diseño arquitectónico (4+1 vistas) | ✅ Documentada |
| 2 | Componentes reutilizables | ✅ Documentada |
| 3 | Desarrollo incremental (sprints) | ⏳ Pendiente |
| 4 | Pruebas y validación | ⏳ Pendiente |

## Stack

- **Frontend:** Angular 17+ (standalone) + TypeScript.
- **Backend:** Scala 3 + http4s + Tapir + Cats Effect + Circe + Skunk.
- **Datos/Auth:** Supabase (PostgreSQL 15+, Auth, RLS).
- **Reportes:** Metabase (dashboards) + JasperReports (PDF).
- **Docs:** Markdown + MkDocs Material + PlantUML + OpenAPI.

## Documentación

- [Índice de documentación](docs/README.md)
- [Entrevistas simuladas](docs/01-requisitos/entrevistas.md)
- [Procesos AS-IS / TO-BE](docs/01-requisitos/procesos.md)
- [SRS — Especificación de requisitos](docs/01-requisitos/srs.md)
- [Casos de uso](docs/01-requisitos/casos-de-uso.md)
- [SAD — Arquitectura 4+1 vistas](docs/02-arquitectura/sad.md)
- [Decisiones arquitectónicas (ADR)](docs/02-arquitectura/decisiones.md)
- [Componentes reutilizables (Fase 2)](docs/02-arquitectura/componentes/README.md)
- [Ramas y Conventional Commits](docs/04-gestion/ramas-y-commits.md)
- [Stack y justificación](docs/04-gestion/stack-tecnologico.md)

## Diagramas UML

Fuentes PlantUML en [`docs/03-diagramas/plantuml/`](docs/03-diagramas/plantuml/):

| Diagrama | Archivo |
|---|---|
| Casos de uso | `casos-de-uso.puml` |
| Paquetes (vista lógica) | `paquetes.puml` |
| Clases del dominio | `clases-dominio.puml` |
| Componentes (implementación) | `componentes.puml` |
| Componentes reutilizables (Fase 2) | `componentes-reutilizables.puml` |
| Despliegue | `despliegue.puml` |
| Secuencia — compras | `secuencia-compras.puml` |
| Secuencia — ventas | `secuencia-ventas.puml` |
| Secuencia — inventario | `secuencia-inventario.puml` |
| Secuencia — autenticación | `secuencia-autenticacion.puml` |

Renderizar a PNG:

```bash
# Desde la raíz del repositorio; requiere plantuml (o plantuml.jar)
java -jar plantuml.jar -tpng -o "$(pwd)/docs/03-diagramas/exportados" \
  docs/03-diagramas/plantuml/*.puml
```

Los PNG renderizados ya están en `docs/03-diagramas/exportados/`.

## Flujo de trabajo

- `main` protegida y siempre estable.
- Ramas cortas `feat/*`, `fix/*`, `docs/*` y Pull Requests.
- Commits en formato [Conventional Commits](docs/04-gestion/ramas-y-commits.md).

## Equipo

<!-- Completar con los integrantes y su contribución -->
