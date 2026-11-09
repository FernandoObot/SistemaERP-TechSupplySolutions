# Contrato OpenAPI

`api.yaml` es la especificación **generada desde Tapir** (fuente de verdad) y
exportada desde la API en ejecución:

```bash
curl -s http://localhost:8080/docs/docs.yaml -o api/openapi/api.yaml
```

- `paths` documenta los endpoints actuales (10 paths): health, proveedores,
  productos, almacenes, órdenes de compra y recepciones.
- `components/schemas` contiene los DTO de esos recursos y `ApiError`.
- En los siguientes sprints se agregarán inventario, ventas, contabilidad y reportería.

No editar `api.yaml` a mano: se regenera desde el código.
