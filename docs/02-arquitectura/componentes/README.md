# Componentes reutilizables (Fase 2)

Identificación, contratos y reutilización de los **7 componentes transversales** del
ERP. Cada componente se define una sola vez, con una interfaz explícita, y se usa en
al menos 3 contextos. La implementación se hará en la Fase 3 respetando estos contratos.

## Principios

1. **Contrato primero:** la interfaz (puerto) se define antes de la implementación.
2. **Una sola fuente:** no se duplica lógica entre módulos; se inyecta el componente.
3. **Dependencias hacia el dominio:** los componentes no dependen de HTTP ni de SQL.
4. **Reutilización verificable:** cada componente tiene una matriz de contextos y pruebas.
5. **Configuración, no copia:** el comportamiento variable se parametriza.

## Inventario

| # | Componente | Tipo | Contrato principal |
|---|---|---|---|
| 1 | [Autenticación](01-autenticacion.md) | Seguridad | `AuthPort` + `AuthService` |
| 2 | [Búsqueda avanzada](02-busqueda-avanzada.md) | Consulta | `SearchPort[A]` |
| 3 | [Reportería](03-reporteria.md) | Salida | `ReportPort` |
| 4 | [Auditoría](04-auditoria.md) | Trazabilidad | `AuditPort` |
| 5 | [Notificaciones](05-notificaciones.md) | Integración | `NotificationPort` |
| 6 | [Validación](06-validacion.md) | Reglas | `Validator[A]` |
| 7 | [CRUD genérico](07-crud.md) | Persistencia/UI | `CrudRepository` + `CrudEndpoints` |

## Matriz de reutilización

| Componente | Maestra | Compras | Inventario | Ventas | Contabilidad | RRHH | Reportería | Seguridad |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| Autenticación | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Búsqueda avanzada | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Reportería | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — |
| Auditoría | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | ✓ |
| Notificaciones | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Validación | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | ✓ |
| CRUD genérico | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | ✓ |

Cada componente supera el mínimo de **3 contextos** exigido; el criterio “Excelente”
de la rúbrica pide ≥ 7 componentes con interfaz documentada y uso en ≥ 3 contextos.

## Contratos transversales

Todo componente comparte estas convenciones:

### Envelope de error

```json
{
  "code": "STOCK_INSUFICIENTE",
  "message": "No hay stock suficiente para el producto ABC-123",
  "details": [{ "field": "cantidad", "issue": "solicitada=10, disponible=4" }],
  "traceId": "3f6b1e9c"
}
```

Códigos HTTP: `400` validación de forma, `401` no autenticado, `403` sin permiso,
`404` no encontrado, `409` conflicto de estado, `422` regla de negocio, `429` límite.

### Paginación y ordenamiento

```http
GET /api/v1/productos?page=0&size=20&sort=nombre,asc&q=cable
```

```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "total": 134,
  "totalPages": 7
}
```

### Idempotencia

Endpoints que crean documentos (OC, OV, factura, pago) aceptan
`Idempotency-Key: <uuid>`; repetir la petición devuelve el mismo resultado.

### Correlación

Cada petición lleva `X-Correlation-Id`; la API lo propaga a logs, auditoría y
notificaciones para trazabilidad end-to-end.

## Reglas de dependencia

```
adapters/http ─┐
adapters/db   ─┼─► application (puertos) ─► domain
adapters/auth ─┘
```

- `domain` no importa nada de infraestructura.
- Los componentes exponen **puertos** (`trait`) y los adaptadores los implementan.
- El frontend consume los servicios vía HTTP y reutiliza componentes de UI en
  `frontend/src/app/shared/`.

## Pruebas por componente

| Nivel | Qué se prueba |
|---|---|
| Unitario | Reglas puras del componente (sin BD ni HTTP). |
| Propiedades | Invariantes (p. ej. paginación, validación, suma de asientos). |
| Integración | Adaptador contra Postgres/Supabase (Testcontainers o proyecto de prueba). |
| Contrato | OpenAPI + esquemas de request/response. |

## Orden de implementación (Fase 3)

1. Validación y CRUD (base de los maestros).
2. Autenticación y Auditoría (transversales desde el inicio).
3. Búsqueda avanzada (listados de todos los módulos).
4. Notificaciones (alertas de stock y compras).
5. Reportería (dashboards y PDFs al final del sprint 9-10).

## Trazabilidad

| Componente | Requisitos | Casos de uso |
|---|---|---|
| Autenticación | RF-SEG-01..05 | CU-00, CU-02 |
| Búsqueda | RF-MAE-01..06, RF-REP-02 | CU-01, CU-14 |
| Reportería | RF-REP-01..04 | CU-14 |
| Auditoría | RF-CON-06, RF-SEG-03 | CU-11, CU-14 |
| Notificaciones | RF-INV-03, RF-COM-01 | CU-03, CU-06 |
| Validación | RNF-05, RF-SEG-01 | Todos |
| CRUD | RF-MAE-01..06 | CU-01 |
