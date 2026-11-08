# Contrato OpenAPI

`api.yaml` es la especificación **generada desde Tapir** (fuente de verdad) y
exportada desde la API en ejecución:

```bash
curl -s http://localhost:8080/docs/docs.yaml -o api/openapi/api.yaml
```

- `paths` documenta los endpoints actuales (`/health`, `/api/v1/productos`).
- `components/schemas` contiene `ApiError`, `ProductoDto`, `ProductoInputDto` y `PageDto`.
- En el Sprint 1 se agregarán los recursos restantes de Gestión Maestra:
  proveedores, clientes, empleados, almacenes y rutas de entrega.

No editar `api.yaml` a mano: se regenera desde el código.
