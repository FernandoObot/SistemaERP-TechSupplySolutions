# C-05 · Componente de Notificaciones

## Propósito

Enviar alertas y mensajes por **email, SMS y en-app** desde cualquier módulo, con
plantillas reutilizables, reintentos e idempotencia. Evita que cada módulo implemente
su propio envío.

## Responsabilidades

- Encolar notificaciones y procesarlas de forma asíncrona.
- Renderizar plantillas con datos del evento.
- Enviar por el canal configurado.
- Reintentar con backoff y registrar fallos.
- Respetar preferencias del usuario y evitar duplicados.

**Fuera de alcance:** campañas de marketing y mensajería masiva.

## Contrato backend (Scala 3)

```scala
enum Channel { case Email, Sms, InApp }
final case class TemplateId(value: String)
final case class Recipient(email: Option[String], phone: Option[String], userId: Option[UserId])

final case class Notification(
  template: TemplateId,
  channel: Channel,
  recipient: Recipient,
  data: Map[String, String],
  idempotencyKey: Option[String]
)

trait NotificationPort[F[_]]:
  def send(n: Notification): F[DeliveryId]
  def sendBatch(ns: List[Notification]): F[List[DeliveryId]]
  def inbox(userId: UserId): F[List[InAppNotification]]
  def markRead(userId: UserId, id: UUID): F[Unit]
```

## Endpoints de la API

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/notificaciones` | Bandeja in-app del usuario. |
| PATCH | `/api/v1/notificaciones/{id}/leida` | Marca como leída. |

> El envío es interno (lo disparan los casos de uso), no un endpoint público.

## Plantillas iniciales

| Template | Evento | Canales |
|---|---|---|
| `stock_bajo` | Stock ≤ punto de reorden | Email, InApp |
| `oc_enviada` | OC emitida al proveedor | Email |
| `recepcion_diferencia` | Recepción con diferencias | Email, InApp |
| `credito_excedido` | OV bloqueada por crédito | Email, InApp |
| `factura_emitida` | Factura al cliente | Email |
| `cobranza_vencida` | Cuenta por cobrar vencida | Email, SMS |
| `aprobacion_pendiente` | Requisición/ajuste por aprobar | InApp |

## Diseño de ejecución

```
Caso de uso ──► notification_outbox (misma transacción)
                         │
                 worker (poll/cola)
                         │
        ┌────────────────┼────────────────┐
     Email(SMTP)      SMS(proveedor)    InApp(BD)
```

- **Idempotencia:** `idempotencyKey` evita duplicados al reintentar.
- **Reintentos:** backoff exponencial (3 intentos) y cola de fallidos.
- **Preferencias:** el usuario puede desactivar canales no críticos.
- **Plantillas versionadas** en el repositorio, con variables validadas.

## Contrato frontend (Angular)

```ts
@Injectable({ providedIn: 'root' })
export class NotificationService {
  inbox(): Observable<InAppNotification[]>;
  markRead(id: string): Observable<void>;
  unreadCount$: Observable<number>;
}
```

Componente compartido: `NotificationBellComponent`.

## Contextos de reutilización

| Contexto | Notificación |
|---|---|
| Compras | Requisición pendiente, OC enviada, diferencias en recepción. |
| Inventario | Stock bajo, producto sin rotación. |
| Ventas | Crédito excedido, factura emitida, pedido despachado. |
| Contabilidad | Periodo por cerrar, presupuesto excedido. |
| RRHH | Nómina procesada, vacaciones por aprobar. |
| Seguridad | Acceso sospechoso, cambio de rol. |

## Pruebas

- Unitarias: render de plantillas y validación de variables.
- Integración: outbox → worker → proveedor simulado (mock).
- Reintentos e idempotencia con fallos inyectados.

## Trazabilidad

RF-INV-03 · RF-COM-01 · CU-03, CU-06 · RNF-01.
