# Documento de Arquitectura de Software (SAD)

**Proyecto:** Sistema ERP — TechSupply Solutions
**Versión:** 1.0 (Fase 1) · Modelo **4+1 vistas** (Kruchten)

## 1. Introducción

Este documento describe la arquitectura del ERP siguiendo el modelo 4+1: las vistas
**Lógica**, **de Procesos**, **de Implementación** y **de Despliegue**, unificadas por
la vista de **Casos de Uso**. Las decisiones se justifican en
`docs/02-arquitectura/decisiones.md` (ADR) y los requisitos en
`docs/01-requisitos/srs.md`.

## 2. Drivers arquitectónicos

| Driver | Fuente | Respuesta arquitectónica |
|---|---|---|
| Integración de 5 procesos | Narrativa | Lógica de dominio compartida + API única |
| Trazabilidad y auditoría | RF-CON-06, RF-SEG-03 | Auditoría transversal y asientos referenciados |
| Separación de responsabilidades | Rúbrica | Arquitectura hexagonal por capas |
| Reutilización (≥ 7 componentes) | Rúbrica | Componentes transversales con contratos |
| Escalabilidad 200 usuarios | RNF-04 | API sin estado + Postgres administrado |
| Seguridad | RNF-05, RF-SEG | Supabase Auth + JWT + RLS |
| Entregas por sprints | Fase 3 | Módulos desacoplados por bounded context |

## 3. Estilo arquitectónico

Se adopta **arquitectura hexagonal (puertos y adaptadores)** sobre un modelo de capas.
La dependencia apunta siempre hacia el dominio; los detalles (HTTP, SQL, proveedores)
son adaptadores reemplazables.

```
┌──────────────────────────────────────────────────────────────┐
│ CAPA DE PRESENTACIÓN — Angular 17+ (SPA)                     │
│ Dashboard · Formularios · Portal de clientes · Reportes       │
└───────────────────────────────┬──────────────────────────────┘
                                │ HTTPS / REST (JSON) + JWT
┌───────────────────────────────▼──────────────────────────────┐
│ CAPA DE SERVICIOS / API — Scala 3 (http4s + Tapir)           │
│ Autenticación · Autorización RBAC · Orquestación · OpenAPI   │
└───────────────────────────────┬──────────────────────────────┘
┌───────────────────────────────▼──────────────────────────────┐
│ CAPA DE LÓGICA DE NEGOCIO — Dominio puro (Cats)              │
│ Compras · Inventario · Ventas · Contabilidad · RRHH          │
│ Componentes reutilizables: búsqueda, validación, auditoría…  │
└───────────────────────────────┬──────────────────────────────┘
┌───────────────────────────────▼──────────────────────────────┐
│ CAPA DE ACCESO A DATOS — Puertos + adaptadores Skunk         │
│ Repositorios · Transacciones · Consultas de reportes         │
└───────────────────────────────┬──────────────────────────────┘
┌───────────────────────────────▼──────────────────────────────┐
│ CAPA DE PERSISTENCIA — Supabase PostgreSQL 15+               │
│ Esquema normalizado (3FN) · RLS · Auditoría · Migraciones    │
└──────────────────────────────────────────────────────────────┘
```

Servicios externos: **Supabase Auth** (JWT), **Metabase** (dashboards) y
**JasperReports** (PDF). Ver vista de despliegue.

---

## 4. Vista Lógica

Describe el modelo de dominio, sus paquetes y responsabilidades. Diagramas:
`plantuml/clases-dominio.puml` y `plantuml/paquetes.puml`.

### 4.1 Paquetes del dominio

| Paquete | Responsabilidad | Agregados principales |
|---|---|---|
| `maestra` | Catálogos base | Producto, Proveedor, Cliente, Empleado, Almacén, Ruta |
| `compras` | Abastecimiento | Requisicion, OrdenCompra, Recepcion |
| `inventario` | Existencias | Movimiento, Stock, Conteo, Alerta |
| `ventas` | Comercialización | Cotizacion, OrdenVenta, Factura, Pago |
| `contabilidad` | Registro financiero | PlanCuenta, Asiento, Periodo, Presupuesto |
| `rrhh` | Personal | Nomina, Asistencia, Vacacion |
| `seguridad` | Identidad y acceso | Usuario, Rol, Permiso, Auditoria |
| `reporteria` | Consulta agregada | Reporte, Indicador |

### 4.2 Entidades y relaciones clave

- Un **Producto** pertenece a una **Categoría/Línea** y tiene un **Proveedor** principal.
- Una **Requisición** aprobada origina una o varias **Órdenes de Compra**.
- Una **OC** tiene **Detalles** y una o varias **Recepciones**; cada recepción genera
  **Movimientos** de entrada.
- Una **Cotización** aprobada se convierte en **Orden de Venta**, con **Detalles**,
  reserva de **Stock** y validación de **Cliente.limiteCredito**.
- Una **OV** genera **Factura** y **Cuenta por Cobrar**; la factura origina **Asientos**.
- Toda operación escribe en **Auditoría** (tabla origen, registro, usuario, antes/después).

### 4.3 Reglas de negocio invariantes

1. `stock >= 0` siempre; una salida no puede dejar stock negativo.
2. `debe = haber` en todo asiento contable.
3. Una factura no puede exceder el crédito disponible sin autorización.
4. No se recibe más cantidad de la ordenada sin autorización.
5. SKU, folios y códigos son únicos.
6. Todo cambio de estado relevante es auditado.

---

## 5. Vista de Procesos

Describe la interacción y concurrencia. Diagramas de secuencia en
`plantuml/secuencia-*.puml`.

### 5.1 Flujo de compras (requisición → OC → recepción → contabilidad)

1. Un job consulta stock y, si `stock <= puntoReorden`, crea una **Requisición**.
2. El Comprador aprueba y se emite la **OC** (estado `enviada`).
3. Al recibir, el Almacenista registra la **Recepción** contra partidas de la OC.
4. En una **transacción**: se actualiza stock, se registra movimiento y se crea la
   **cuenta por pagar**.
5. Se dispara el **asiento contable** asociado a la recepción.

### 5.2 Flujo de ventas (cotización → OV → factura → cobro)

1. El cliente/vendedor crea la **Cotización**; el sistema consulta disponibilidad.
2. Al aprobar, se crea la **OV** y se **reserva stock** (transacción).
3. Se valida el **límite de crédito**; si excede, queda pendiente de autorización.
4. Se emite **Factura** y **cuenta por cobrar**; se genera el **asiento**.
5. El **pago** actualiza el saldo y el estado del pedido.

### 5.3 Concurrencia y transacciones

- La API es **sin estado**; el JWT identifica al usuario en cada petición.
- Las operaciones que mueven stock o dinero usan **transacciones de base de datos**
  con nivel `READ COMMITTED` y bloqueo optimista (`version` en agregados).
- La reserva de stock usa una **actualización condicional**
  (`UPDATE ... WHERE stock >= :cantidad`) para evitar sobreventa.
- Las alertas y notificaciones se procesan de forma **asíncrona** (cola de trabajos)
  sin bloquear la transacción principal.
- Idempotencia en endpoints críticos mediante `Idempotency-Key`.

### 5.4 Autenticación

1. Angular autentica contra **Supabase Auth** y recibe el JWT.
2. Cada petición a la API incluye `Authorization: Bearer <jwt>`.
3. La API valida firma y expiración contra el **JWKS** de Supabase y resuelve el rol.
4. La API aplica RBAC; RLS es la segunda barrera a nivel de base de datos.

---

## 6. Vista de Implementación

Describe la organización física del código, los módulos y los componentes
reutilizables. Diagrama: `plantuml/componentes.puml`.

### 6.1 Estructura del repositorio (monorepo)

```
/
├── frontend/                 # Angular 17+ (standalone)
│   └── src/app/
│       ├── core/             # auth, interceptores, guards
│       ├── shared/           # componentes reutilizables UI
│       └── features/         # maestra, compras, inventario, ventas, ...
├── backend/                  # Scala 3
│   ├── domain/               # modelo y reglas puras
│   ├── application/          # casos de uso (puertos)
│   ├── adapters/
│   │   ├── http/             # endpoints Tapir + http4s
│   │   ├── persistence/      # repositorios Skunk
│   │   ├── auth/             # validación JWT Supabase
│   │   └── reporting/        # JasperReports
│   └── infrastructure/       # config, DI, migraciones
├── db/                       # migraciones Supabase (SQL)
├── docs/                     # SRS, SAD, ADR, diagramas
└── .github/workflows/        # CI/CD
```

### 6.2 Módulos y capas

| Capa | Artefacto | Depende de |
|---|---|---|
| Presentación | `frontend/` | API REST |
| Servicios | `adapters/http` | `application` |
| Negocio | `domain`, `application` | Nada externo |
| Datos | `adapters/persistence` | `domain` (implementa puertos) |
| Persistencia | Supabase Postgres | — |

### 6.3 Componentes reutilizables (7)

| Componente | Contrato (interfaz) | Contextos de uso |
|---|---|---|
| **Autenticación** | `AuthService.login/logout/session/roles` | Todos los módulos, portal, API |
| **Búsqueda avanzada** | `SearchCriteria(filtros, page, sort) → Page<T>` | Productos, clientes, OC, facturas |
| **Reportería** | `ReportService.generate(ReportSpec) → File` | Ventas, inventario, contabilidad |
| **Auditoría** | `Audit.record(entity, action, before, after)` | Todos los agregados |
| **Notificaciones** | `Notifier.send(channel, template, data)` | Stock bajo, OC, cobranza |
| **Validación** | `Validator[T].validate(value) → Validated[Errors, T]` | Formularios y casos de uso |
| **CRUD genérico** | `Repository[T, Id]` + `CrudEndpoints[T]` | Maestros y catálogos |

Cada componente se implementa una vez y se reutiliza en ≥ 3 contextos, cumpliendo
el criterio de la rúbrica (interfaz documentada + reutilización demostrada).

### 6.4 Contrato de API

- REST versionada: `/api/v1/...`
- OpenAPI 3.1 generado por **Tapir** (`/docs`, `/openapi.json`).
- Errores uniformes: `{ "code", "message", "details", "traceId" }`.
- Paginación: `?page=0&size=20&sort=campo,asc`.
- Filtros por query params tipados.

---

## 7. Vista de Despliegue

Diagrama: `plantuml/despliegue.puml`.

### 7.1 Nodos

| Nodo | Tecnología | Función |
|---|---|---|
| Cliente | Navegador | Ejecuta la SPA Angular |
| CDN / Hosting | Vercel/Netlify/Nginx | Sirve el frontend (HTTPS) |
| API | Contenedor Docker (Render/Railway/Fly.io) | API Scala/http4s |
| Reportes | Contenedor JasperReports | Generación de PDF |
| Supabase | Servicio administrado | Postgres, Auth, Storage, RLS |
| Metabase | Servicio administrado/contenedor | Dashboards de solo lectura |
| CI/CD | GitHub Actions | Build, tests, OpenAPI, PlantUML |

### 7.2 Ambientes

- **Desarrollo:** Supabase local (CLI) o proyecto de desarrollo; API y frontend locales.
- **Staging:** réplica con datos anonimizados para pruebas end-to-end.
- **Producción:** Supabase productivo, API en contenedor, frontend en CDN, TLS.

### 7.3 Red y seguridad

- Todo el tráfico por **HTTPS/TLS 1.2+**.
- La API valida JWT y aplica RBAC; RLS en Postgres como defensa en profundidad.
- La `service_role` key solo existe en el entorno del backend (nunca en el frontend).
- Metabase usa un usuario **de solo lectura**.
- Respaldos automáticos de Supabase y auditoría de accesos.

### 7.4 Escalabilidad y disponibilidad

- API sin estado → escalado horizontal tras el balanceador.
- Conexiones a Postgres con pool; lecturas de reportes aisladas (réplica/rol de lectura).
- Jobs asíncronos para alertas y reportes pesados.
- Objetivo 99.5 % de disponibilidad (RNF-01).

---

## 8. Vista de Casos de Uso

Unifica las cuatro vistas. Ver `docs/01-requisitos/casos-de-uso.md` y
`plantuml/casos-de-uso.puml`.

| Vista | Cómo la recorren los casos de uso |
|---|---|
| Lógica | CU-06 usa `Movimiento`, `Stock`, `Producto` |
| Procesos | CU-05/CU-10 disparan secuencias y asientos |
| Implementación | CU-00 usa el componente de autenticación |
| Despliegue | CU-14 consume API y Metabase |
| Casos de uso | Todos se originan en actores y requisitos |

---

## 9. Atributos de calidad y tácticas

| Atributo | Táctica | Requisito |
|---|---|---|
| Disponibilidad | Réplicas de API, respaldos, health checks | RNF-01 |
| Rendimiento | Índices, paginación, consultas agregadas, caché | RNF-02/03 |
| Escalabilidad | API sin estado, pool de conexiones | RNF-04 |
| Seguridad | JWT, RBAC, RLS, TLS, secretos gestionados | RNF-05 |
| Mantenibilidad | Hexagonal, pruebas, ADR, OpenAPI | RNF-06 |
| Auditabilidad | Auditoría transversal + asientos referenciados | RF-CON-06 |

## 10. Riesgos

| Riesgo | Impacto | Mitigación |
|---|---|---|
| Curva de Cats Effect | Retraso | Spike inicial y pair programming |
| Acoplamiento a Supabase | Portabilidad | SQL estándar; capa de repositorios |
| Sobreventa por concurrencia | Integridad | Transacciones + bloqueo optimista |
| Reglas contables complejas | Correctitud | Reglas versionadas y pruebas |
| Tiempo del semestre | Entrega | Priorización MoSCoW por sprint |
