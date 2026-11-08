# C-04 · Componente de Auditoría

## Propósito

Registrar de forma uniforme **quién** hizo **qué**, **cuándo** y **sobre qué registro**,
incluyendo valores anteriores y nuevos. Es la base de la trazabilidad exigida por la
rúbrica y del cumplimiento fiscal.

## Responsabilidades

- Registrar eventos de creación, modificación, borrado y aprobación.
- Almacenar valores antes/después en formato JSON.
- Asociar el evento al usuario, `traceId` y documento fuente.
- Exponer consulta de historial por entidad.
- Garantizar que el registro no se pierda si falla la transacción principal.

**Fuera de alcance:** logs técnicos de infraestructura y monitoreo de rendimiento.

## Contrato backend (Scala 3)

```scala
enum AuditAction { case Create, Update, Delete, Approve, Login, Export }

final case class AuditEvent(
  entity: String,
  entityId: UUID,
  action: AuditAction,
  actorId: UUID,
  at: Instant,
  before: Option[Json],
  after: Option[Json],
  traceId: String
)

trait AuditPort[F[_]]:
  def record(event: AuditEvent): F[Unit]
  def history(entity: String, entityId: UUID): F[List[AuditEvent]]
```

## Persistencia

Tabla `auditoria`:

| Columna | Tipo |
|---|---|
| id | UUID |
| tabla_origen | text |
| registro_id | UUID |
| usuario_id | UUID |
| accion | text |
| fecha | timestamptz |
| valores_anteriores | jsonb |
| valores_nuevos | jsonb |
| trace_id | text |

- Índices por `(tabla_origen, registro_id, fecha)`.
- **Outbox transaccional:** el evento se inserta en la misma transacción que el cambio;
  un worker lo publica a notificaciones/analítica. Así no se pierde ni se duplica.
- Sin datos sensibles: se enmascaran contraseñas, tokens y datos de tarjeta.

## Endpoint de consulta

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/auditoria?entity=orden_compra&id={uuid}` | Historial de un registro. |
| GET | `/api/v1/auditoria?usuarioId=&desde=&hasta=` | Consulta por usuario/periodo. |

## Integración con casos de uso

```scala
def actualizarProducto(id: ProductoId, data: ProductoInput): F[Producto] =
  for
    before <- repo.find(id).flatMap(_.liftTo[F](NoEncontrado(id)))
    after  <- validator.validate(data) >>= repo.update(id, _)
    _      <- audit.record(AuditEvent.from(before, after))
  yield after
```

- Se registra **dentro de la misma transacción** cuando la operación es crítica.
- Las aprobaciones (OC, OV, ajustes) siempre generan evento.

## Contextos de reutilización

| Contexto | Eventos |
|---|---|
| Gestión maestra | Altas/bajas/cambios de productos, clientes, proveedores. |
| Compras | Emisión y aprobación de OC, recepciones. |
| Inventario | Movimientos, ajustes de conteo. |
| Ventas | Cotización → OV, facturación, pagos. |
| Contabilidad | Asientos manuales y cierres de periodo. |
| Seguridad | Login, cambios de rol, accesos denegados. |

## Retención y cumplimiento

- Retención mínima configurable (p. ej. 5 años) para auditoría fiscal.
- Solo lectura para usuarios no administradores.
- Exportación a CSV/PDF sujeta a permisos y auditada.

## Pruebas

- Unitarias: construcción de `before/after` y enmascarado de datos.
- Integración: el evento se escribe aunque falle el worker (outbox).
- Propiedades: todo cambio de estado genera exactamente un evento.

## Trazabilidad

RF-CON-06 · RF-SEG-03 · CU-11, CU-14.
