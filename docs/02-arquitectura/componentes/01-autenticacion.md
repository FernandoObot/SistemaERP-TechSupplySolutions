# C-01 · Componente de Autenticación

## Propósito

Centralizar identidad, sesión y autorización. El **login lo emite Supabase Auth**;
la API Scala solo **valida el JWT** y resuelve roles/permisos. Ningún módulo
implementa su propio login.

## Responsabilidades

- Iniciar/cerrar sesión y refrescar el token (frontend con Supabase).
- Validar firma, expiración y audiencia del JWT (backend con JWKS).
- Resolver usuario, roles y permisos.
- Aplicar control de acceso basado en roles (RBAC).
- Exponer el usuario actual a los casos de uso.

**Fuera de alcance:** almacenar contraseñas, recuperación de cuenta (lo cubre Supabase),
reglas de negocio por módulo.

## Contrato backend (Scala 3)

```scala
final case class UserId(value: UUID)
final case class User(
  id: UserId,
  email: String,
  name: String,
  roles: Set[Role],
  permissions: Set[Permission]
)

enum Action { case Read, Create, Update, Delete, Approve, Export }
final case class Permission(resource: String, action: Action)

trait AuthPort[F[_]] {
  def currentUser: F[User]
  def requireRole(role: Role): F[Unit]
  def requirePermission(resource: String, action: Action): F[Unit]
  def can(resource: String, action: Action): F[Boolean]
}
```

- El adaptador HTTP adjunta el `User` al contexto de la petición tras validar el JWT.
- `requirePermission` devuelve `403 Forbidden` con `code = "SIN_PERMISO"` si falla.

## Contrato frontend (Angular)

```ts
export interface SessionUser {
  id: string;
  email: string;
  roles: string[];
  permissions: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly session$: Observable<SessionUser | null>;
  login(email: string, password: string): Promise<void>;
  logout(): Promise<void>;
  hasRole(role: string): boolean;
  hasPermission(resource: string, action: string): boolean;
}
```

- `authInterceptor` agrega `Authorization: Bearer <jwt>`.
- `authGuard` y `roleGuard` protegen rutas.

## Endpoints de la API

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/auth/me` | Devuelve perfil, roles y permisos del token. |
| GET | `/api/v1/auth/health` | Estado de validación del JWKS. |

> El login/refresh se hace contra Supabase Auth, no contra la API.

## Modelo de roles

| Rol | Alcance |
|---|---|
| `ADMIN` | Configuración total, usuarios y roles. |
| `GERENTE` | Lectura de dashboards y reportes. |
| `COMPRADOR` | Compras y proveedores. |
| `ALMACEN` | Inventario y recepciones. |
| `VENDEDOR` | Ventas, clientes y crédito. |
| `CONTADOR` | Contabilidad y finanzas. |
| `RRHH` | Empleados y nómina. |
| `CLIENTE` | Portal: catálogo y sus pedidos. |

## Contextos de reutilización

| Contexto | Uso |
|---|---|
| Módulos internos | Guards + `requirePermission` en cada endpoint. |
| Portal de clientes | Rol `CLIENTE` con acceso restringido a sus datos. |
| Reportería | Control de exportación y visibilidad por rol. |
| API | Validación transversal de todas las peticiones. |

## Seguridad

- El backend nunca confía en roles enviados por el cliente.
- La `service_role` key vive solo en el backend.
- RLS en Postgres como segunda barrera.
- Tokens cortos + refresh; se registra cada acceso en auditoría.

## Pruebas

- Unitarias: resolución de roles/permisos y matriz RBAC.
- Integración: token válido, expirado, firma inválida y sin permiso.
- Contrato: `401`/`403` con envelope de error.

## Trazabilidad

RF-SEG-01..05 · CU-00, CU-02 · RNF-05.
