# Sprint 1-2 — Gestión Maestra + Compras

**Fase 3 · Sprints 1 y 2.** Implementa la primera funcionalidad real del ERP sobre
Supabase: catálogos maestros, órdenes de compra y recepción de bienes con impacto en
inventario y cuentas por pagar.

## 1. Alcance

| Sprint | Módulo | Entregado |
|---|---|---|
| 1 | Gestión Maestra | Proveedores, Productos, Almacenes (CRUD + búsqueda paginada). |
| 2 | Compras | Órdenes de compra con líneas, recepción parcial/total, stock y cuentas por pagar. |

## 2. Backend

### 2.1 Persistencia real con Skunk

- `DbConfig` parsea `DATABASE_URL`.
- `DbPool` crea un pool de sesiones Skunk (`Session.Builder[IO]`).
- Repositorios: `ProveedorRepository`, `ProductoRepository`, `AlmacenRepository`,
  `ComprasRepositorySkunk`.
- Transacciones para crear OC y registrar recepciones.
- `SSL.Trusted` en desarrollo para evitar el problema de CA del JDK; en producción
  cambiar a `SSL.System` con el certificado correcto en el truststore.

### 2.2 Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET/POST | `/api/v1/proveedores` | Lista / crea proveedor. |
| GET/PUT/DELETE | `/api/v1/proveedores/{id}` | Consulta / actualiza / desactiva. |
| GET/POST | `/api/v1/productos` | Lista / crea producto. |
| GET/PUT/DELETE | `/api/v1/productos/{id}` | Consulta / actualiza / desactiva. |
| GET/POST | `/api/v1/almacenes` | Lista / crea almacén. |
| GET/PUT/DELETE | `/api/v1/almacenes/{id}` | Consulta / actualiza / desactiva. |
| GET/POST | `/api/v1/ordenes-compra` | Lista paginada / crea OC con líneas. |
| GET | `/api/v1/ordenes-compra/{id}` | OC con detalle. |
| POST | `/api/v1/ordenes-compra/{id}/recepciones` | Registra recepción. |
| GET | `/api/v1/ordenes-compra/{id}/recepciones` | Historial de recepciones. |

Swagger: <http://localhost:8080/docs> · OpenAPI: `/docs/docs.yaml`
(copia versionada en `api/openapi/api.yaml`, 10 paths).

### 2.3 Reglas de negocio implementadas

- SKU, código de almacén y folio de OC únicos.
- Total de OC = Σ(cantidad × precio unitario).
- No se puede recibir más de lo pendiente por línea.
- La recepción actualiza el estado de la OC (`Parcial` / `Recibida`).
- Cada recepción crea un movimiento de inventario de entrada, actualiza `stock` y
  genera una cuenta por pagar.
- Las tablas `ordenes_compra`, `recepciones` y `cuentas_por_pagar` tienen auditoría
  automática por trigger.

## 3. Base de datos

Migración aplicada: `20261108130000_compras.sql`.

- `ordenes_compra` + `detalles_oc` (folio generado por secuencia).
- `recepciones` + `detalles_recepcion`.
- `movimientos_inventario` + `stock` (base para el Sprint 3).
- `cuentas_por_pagar`.
- Triggers `updated_at` y auditoría.
- RLS y políticas de lectura para `authenticated`.

Aplicar en la nube:

```bash
npx supabase db push --remote --yes
```

## 4. Frontend

- **Productos:** tabla, alta/edición en diálogo, selección de proveedor principal.
- **Proveedores:** tabla y formulario de contacto/evaluación.
- **Almacenes:** tabla y formulario.
- **Compras:**
  - Listado de OC con folio, proveedor, fecha, estado y total.
  - Formulario de nueva OC con líneas dinámicas y cálculo de subtotales/total.
  - Detalle de OC con cantidades recibidas/pendientes e historial de recepciones.
  - Diálogo de recepción con almacén y cantidades por línea.

## 5. Verificación

| Verificación | Resultado |
|---|---|
| `sbt test` | 10 pruebas, 0 fallos |
| `ng build` | build correcto |
| `ng test --watch=false` | 2 pruebas, 0 fallos |
| Flujo E2E contra Supabase | proveedor → almacén → producto → OC → recepción parcial → recepción total |
| Estado de OC | `Enviada` → `Parcial` → `Recibida` |
| Sobre-recepción | `HTTP 422 REGLA_NEGOCIO` |
| OpenAPI | 10 paths generados |
| Migración remota | `20261108130000` aplicada |

Datos de prueba creados en Supabase durante la verificación (proveedor, almacén,
producto y OC). Se pueden conservar para la demo o limpiar desde el SQL Editor.

## 6. Pendiente / riesgos

- **Autenticación:** la API todavía no exige JWT; el login de Angular es demo. Se
  implementa en el siguiente incremento junto con el interceptor y `AuthPort`.
- **SSL:** `SSL.Trusted` es solo para desarrollo.
- **Búsqueda avanzada:** los listados son paginados; falta filtro `q` y ordenamiento
  configurable (C-02).
- **Optimistic locking:** las actualizaciones usan `version + 1`, pero aún no validan
  la versión enviada por el cliente.

## 7. Siguiente: Sprint 3-4 — Inventario

1. Endpoints de movimientos (entrada/salida/ajuste) y consulta de stock.
2. Conteos cíclicos y ajustes auditados.
3. Alertas de stock mínimo.
4. Reporte de rotación y clasificación ABC.
5. Conectar Supabase Auth (JWT) en backend y frontend.
