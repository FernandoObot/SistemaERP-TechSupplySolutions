# Sprint 0 — Base del proyecto y contrato

**Fase 3 · Sprint 0.** Deja listo el esqueleto del monorepo, la API mínima con
contrato OpenAPI, el esquema inicial de Supabase y el shell del frontend.

## 1. Estructura del repositorio

```
.
├── backend/                 # Scala 3 + http4s + Tapir (sbt multi-módulo)
│   ├── domain/
│   ├── application/
│   ├── adapters/
│   ├── infrastructure/
│   └── server/
├── frontend/                # Angular 22 + Angular Material
├── supabase/
│   ├── config.toml
│   └── migrations/          # SQL versionado
├── api/openapi/api.yaml     # OpenAPI generado desde Tapir
├── docs/
└── .github/workflows/ci.yml
```

## 2. Requisitos

| Herramienta | Versión | Nota |
|---|---|---|
| JDK | 21 LTS | SDKMAN: `sdk install java 21.0.8-tem` |
| sbt | 1.13.x | El runner descarga la versión de `project/build.properties` |
| Node.js | 24 | |
| Angular CLI | 22 | `ng version` |
| Supabase CLI | 3.x | Se usa con `npx supabase`, sin instalar global |
| Docker | — | **No necesario** (Supabase Cloud) |

## 3. Supabase Cloud (sin Docker)

1. Crea un proyecto en <https://supabase.com/dashboard>.
2. Copia `Project URL`, `anon key` y `service_role key` a tu `.env` local
   (usa `.env.example` como plantilla; **no** subas `.env`).
3. Vincula y aplica las migraciones:

```bash
npx supabase login
npx supabase link --project-ref <project-ref>
npx supabase db push
```

La migración `20261108120000_init_maestra.sql` crea:

- `auditoria` con triggers de auditoría (`audit_row`).
- `proveedores`, `productos`, `clientes`, `empleados`, `almacenes`, `rutas_entrega`.
- `updated_at` automático, `version` para bloqueo optimista.
- RLS: lectura para `authenticated`; escritura vía API con `service_role`.

> La `service_role` key solo se usa en el backend. Nunca en el frontend ni en Git.

## 4. Backend

```bash
cd backend
sbt test
sbt "server/run"
```

- API: <http://localhost:8080>
- Health: `GET /health`
- Swagger UI: <http://localhost:8080/docs>
- OpenAPI: `GET /docs/docs.yaml` (copia en `api/openapi/api.yaml`)
- Productos: `GET/POST /api/v1/productos`, `GET/DELETE /api/v1/productos/{id}`

En este sprint el repositorio de productos es **en memoria**. En el Sprint 1 se
reemplaza por Skunk + Supabase sin cambiar el contrato HTTP.

## 5. Frontend

```bash
cd frontend
npm ci
npm start        # http://localhost:4200
npm run build
npm test -- --watch=false
```

Pantallas incluidas: login de demostración, shell con navegación, dashboard y
listado de productos (consume la API real; muestra un mensaje claro si el backend
no está corriendo).

La URL de la API está en `src/environments/environment.development.ts`.

## 6. CI

`.github/workflows/ci.yml` ejecuta en cada push/PR a `main` y `develop`:

- Backend: `sbt test`.
- Frontend: `npm ci`, build y tests.
- Diagramas: render de todos los `.puml` con PlantUML.

## 7. Verificación realizada

| Verificación | Resultado |
|---|---|
| `sbt test` | 6 pruebas, 0 fallos |
| `ng build` | build correcto |
| `ng test --watch=false` | 2 pruebas, 0 fallos |
| `GET /health` | `ok` |
| `POST /api/v1/productos` | 200 con producto creado |
| `GET /api/v1/productos` | página con el producto creado |
| `GET /docs/docs.yaml` | OpenAPI 3.1 generado |
| `plantuml -checkonly` | 10 diagramas válidos |

## 8. Definition of Done

- [x] Monorepo con backend y frontend separados.
- [x] API compila, responde y genera OpenAPI.
- [x] Contrato de Productos disponible en Swagger.
- [x] Migración SQL versionada y RLS.
- [x] UI con Material, navegación y listado de productos.
- [x] CI configurada.
- [ ] Proyecto Supabase creado y migración aplicada (acción del equipo).
- [ ] Conectar Supabase Auth en el login (Sprint 1).

## 9. Siguiente: Sprint 1 — Gestión Maestra + Compras

1. Reemplazar el repositorio en memoria por Skunk (productos, proveedores, clientes,
   empleados, almacenes, rutas).
2. Implementar CRUD y búsqueda avanzada (C-02, C-06, C-07).
3. Conectar Supabase Auth y el interceptor JWT (C-01).
4. Implementar auditoría real (C-04) al persistir cambios.
5. Primer flujo de compras: requisición → OC → recepción.
