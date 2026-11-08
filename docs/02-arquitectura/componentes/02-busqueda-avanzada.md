# C-02 · Componente de Búsqueda Avanzada

## Propósito

Proveer filtrado, paginación y ordenamiento uniformes para todos los listados, sin
que cada módulo reimplemente consultas. Se usa tanto en la API (consulta) como en la
UI (barra de búsqueda y tabla paginada).

## Responsabilidades

- Traducir criterios de búsqueda a consultas SQL seguras (sin inyección).
- Aplicar paginación y ordenamiento con límites.
- Devolver metadatos de paginación.
- Permitir filtros por campos declarados y búsqueda de texto.

## Contrato backend (Scala 3)

```scala
enum SortDirection { case Asc, Desc }
final case class SortField(name: String, direction: SortDirection)

enum FilterValue {
  case Text(value: String)
  case Number(value: BigDecimal)
  case Bool(value: Boolean)
  case DateRange(from: Instant, to: Instant)
  case OneOf(values: List[String])
}

final case class SearchRequest(
  q: Option[String],
  filters: Map[String, FilterValue],
  page: Int,          // base 0
  size: Int,          // máx. 100
  sort: List[SortField]
)

final case class Page[A](
  items: List[A],
  page: Int,
  size: Int,
  total: Long
):
  def totalPages: Int = if size == 0 then 0 else ((total + size - 1) / size).toInt

trait SearchPort[F[_], A]:
  def search(criteria: SearchRequest): F[Page[A]]
```

Reglas: `size` se acota a `[1, 100]`; campos de orden/filtro se validan contra una
lista blanca por recurso; el texto (`q`) usa `tsvector`/`ILIKE` según el campo.

## Contrato HTTP

```http
GET /api/v1/{recurso}?q=cable&page=0&size=20&sort=nombre,asc&filter[activo]=true
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

| Recurso | Campos filtrables | Orden por defecto |
|---|---|---|
| productos | sku, nombre, categoria, activo | nombre,asc |
| proveedores | nombre, activo | nombre,asc |
| clientes | nombre, zonaEntrega, activo | nombre,asc |
| ordenes-compra | folio, estado, proveedorId, fecha | fecha,desc |
| facturas | folio, clienteId, fecha | fecha,desc |
| auditoria | tablaOrigen, usuarioId, accion, fecha | fecha,desc |

## Contrato frontend (Angular)

```ts
export interface Page<T> { items: T[]; page: number; size: number; total: number; totalPages: number; }
export interface SearchRequest { q?: string; filters?: Record<string, unknown>; page: number; size: number; sort?: string; }

@Injectable({ providedIn: 'root' })
export class SearchService {
  search<T>(resource: string, req: SearchRequest): Observable<Page<T>>;
}
```

Componentes compartidos: `SearchBarComponent`, `DataTableComponent<T>`,
`PaginatorComponent` y `FilterPanelComponent`.

## Contextos de reutilización

| Contexto | Ejemplo |
|---|---|
| Gestión maestra | Productos, proveedores, clientes, empleados. |
| Compras | Búsqueda de OC por estado/fecha/proveedor. |
| Inventario | Movimientos por producto/almacén/tipo. |
| Ventas | Facturas por cliente y periodo. |
| Auditoría | Eventos por entidad y usuario. |

## Rendimiento

- Índices en columnas de filtro y orden frecuentes.
- Índice GIN/`tsvector` para búsqueda de texto.
- Paginación obligatoria; nunca se devuelve una tabla completa.
- Consultas de conteo optimizadas o estimadas cuando el total es muy grande.

## Pruebas

- Propiedades: `totalPages`, recorte de `size`, orden estable.
- Seguridad: campos no permitidos son rechazados.
- Integración: filtros combinados y texto.

## Trazabilidad

RF-MAE-01..06 · RF-REP-02 · CU-01, CU-14 · RNF-02.
