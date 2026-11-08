# C-07 · Componente CRUD genérico

## Propósito

Estandarizar las operaciones Create/Read/Update/Delete de los catálogos maestros y
evitar repetir endpoints, repositorios y pantallas para cada entidad. Se parametriza
por tipo y por reglas de validación/permiso.

## Responsabilidades

- Proveer repositorio genérico sobre Postgres (Skunk).
- Exponer endpoints REST uniformes (Tapir).
- Soportar búsqueda, paginación y ordenamiento (C-02).
- Aplicar validación (C-06), permisos (C-01) y auditoría (C-04).
- Implementar **borrado lógico** y **bloqueo optimista**.

**Fuera de alcance:** procesos con lógica compleja (OC, facturación, asientos), que
usan casos de uso específicos.

## Contrato backend (Scala 3)

```scala
trait CrudRepository[F[_], A, Id]:
  def create(a: A): F[A]
  def find(id: Id): F[Option[A]]
  def update(id: Id, a: A): F[A]
  def softDelete(id: Id): F[Unit]
  def search(criteria: SearchRequest): F[Page[A]]

final case class CrudConfig[A, Id](
  resource: String,
  validator: Validator[A],
  audit: AuditPort[IO],
  permissions: PermissionSet,
  versioned: Boolean = true
)

trait CrudEndpoints[F[_], A, Id]:
  def routes(config: CrudConfig[A, Id]): HttpRoutes[F]
```

## Endpoints generados

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/{recurso}` | Lista paginada y filtrable. |
| GET | `/api/v1/{recurso}/{id}` | Obtiene un registro. |
| POST | `/api/v1/{recurso}` | Crea (valida + audita). |
| PUT | `/api/v1/{recurso}/{id}` | Actualiza (versión optimista). |
| DELETE | `/api/v1/{recurso}/{id}` | Borrado lógico. |

## Entidades que lo usan

| Entidad | Recurso | Reglas propias |
|---|---|---|
| Producto | `productos` | SKU único, precio ≥ 0 |
| Proveedor | `proveedores` | Evaluación 1–5 |
| Cliente | `clientes` | Límite de crédito ≥ 0 |
| Empleado | `empleados` | Salario > 0 |
| Almacén | `almacenes` | Código único |
| Ruta de entrega | `rutas` | Transportista requerido |
| Plan de cuentas | `plan-cuentas` | Código jerárquico único |

## Contrato frontend (Angular)

```ts
export interface CrudService<T, Id = string> {
  list(req: SearchRequest): Observable<Page<T>>;
  get(id: Id): Observable<T>;
  create(value: T): Observable<T>;
  update(id: Id, value: T): Observable<T>;
  remove(id: Id): Observable<void>;
}
```

Componentes compartidos: `CrudTableComponent<T>`, `CrudFormComponent<T>`,
`ConfirmDialogComponent`.

## Reglas transversales

- **Borrado lógico:** `deleted_at`; los registros eliminados no aparecen en listados.
- **Bloqueo optimista:** columna `version`; conflicto → `409`.
- **Auditoría automática:** create/update/delete generan evento (C-04).
- **Validación:** se ejecuta antes de persistir (C-06).
- **Permisos:** `recurso:acción` verificado por C-01.

## Cuándo NO usarlo

- Órdenes de compra/venta, facturación, asientos, nómina y movimientos de inventario:
  tienen invariantes y transacciones propias → casos de uso dedicados que **reutilizan**
  los servicios comunes (validación, auditoría, búsqueda) pero no el CRUD genérico.

## Pruebas

- Unitarias: mapeo entidad ↔ fila y reglas de borrado lógico.
- Integración: CRUD completo contra Postgres.
- Contrato: endpoints y errores uniformes.
- Concurrencia: dos actualizaciones con la misma `version` → una falla con `409`.

## Trazabilidad

RF-MAE-01..06 · CU-01 · RNF-06.
