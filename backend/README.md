# Backend (API Scala)

## Requisitos

- JDK 21 LTS
- sbt 1.13.x (el runner descarga la versión declarada en `project/build.properties`)

## Comandos

```bash
sbt compile     # compila todos los módulos
sbt test        # pruebas unitarias
sbt "server/run" # API en http://localhost:8080
```

## Endpoints del Sprint 0

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/health` | Estado del servicio. |
| GET | `/api/v1/productos?page=0&size=20` | Lista paginada. |
| POST | `/api/v1/productos` | Crea un producto. |
| GET | `/api/v1/productos/{id}` | Obtiene un producto. |
| DELETE | `/api/v1/productos/{id}` | Borrado lógico. |
| GET | `/docs` | Swagger UI. |
| GET | `/docs/docs.yaml` | OpenAPI generado. |

## Módulos

- `domain`: modelo y reglas puras.
- `application`: puertos, validación y casos de uso.
- `adapters`: HTTP (Tapir/http4s), persistencia en memoria.
- `infrastructure`: configuración.
- `server`: `Main` y arranque de Ember.

En el Sprint 1 se reemplaza el repositorio en memoria por Skunk + Supabase.
