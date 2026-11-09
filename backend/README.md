# Backend (API Scala)

## Requisitos

- JDK 21 LTS
- sbt 1.13.x (el runner descarga la versión declarada en `project/build.properties`)
- `DATABASE_URL` (Postgres de Supabase) en el entorno o en `.env`

## Comandos

```bash
./run-dev.sh     # carga ../.env y arranca la API en http://localhost:8080
sbt compile      # compila todos los módulos
sbt test         # pruebas unitarias
```

Si prefieres hacerlo manualmente:

```bash
set -a; source ../.env; set +a
sbt "server/run"
```

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/health` | Estado del servicio. |
| GET/POST | `/api/v1/proveedores` | Lista / crea proveedor. |
| GET/PUT/DELETE | `/api/v1/proveedores/{id}` | Consulta / actualiza / desactiva. |
| GET/POST | `/api/v1/productos` | Lista / crea producto. |
| GET/PUT/DELETE | `/api/v1/productos/{id}` | Consulta / actualiza / desactiva. |
| GET/POST | `/api/v1/almacenes` | Lista / crea almacén. |
| GET/PUT/DELETE | `/api/v1/almacenes/{id}` | Consulta / actualiza / desactiva. |
| GET/POST | `/api/v1/ordenes-compra` | Lista / crea orden de compra. |
| GET | `/api/v1/ordenes-compra/{id}` | Orden con detalle. |
| POST | `/api/v1/ordenes-compra/{id}/recepciones` | Registra recepción. |
| GET | `/api/v1/ordenes-compra/{id}/recepciones` | Historial de recepciones. |
| GET | `/docs` | Swagger UI. |
| GET | `/docs/docs.yaml` | OpenAPI generado. |

## Módulos

- `domain`: modelo y reglas puras (Producto, Proveedor, Almacén, OrdenCompra, Recepción).
- `application`: puertos, validación y casos de uso.
- `adapters`: HTTP (Tapir/http4s) y persistencia (Skunk).
- `infrastructure`: configuración de entorno.
- `server`: `Main`, pool de base de datos y arranque de Ember.

## Notas

- `DbConfig` parsea `DATABASE_URL`.
- En desarrollo se usa `SSL.Trusted` para Postgres; en producción usar `SSL.System`
  con la CA correspondiente en el truststore.
- La autenticación JWT se implementa en el siguiente incremento.
