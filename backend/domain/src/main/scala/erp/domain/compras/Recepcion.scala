package erp.domain.compras

import erp.domain.{AlmacenId, OrdenCompraId, ProductoId, RecepcionId}

import java.time.Instant
import java.util.UUID

enum EstadoRecepcion:
  case Recibida

final case class DetalleRecepcionInput(
    detalleOcId: UUID,
    cantidadRecibida: Int
)

final case class RecepcionInput(
    almacenId: AlmacenId,
    observaciones: Option[String],
    detalles: List[DetalleRecepcionInput]
)

final case class DetalleRecepcion(
    id: UUID,
    detalleOcId: UUID,
    productoId: ProductoId,
    cantidadRecibida: Int
)

final case class Recepcion(
    id: RecepcionId,
    ordenCompraId: OrdenCompraId,
    almacenId: AlmacenId,
    fecha: Instant,
    estado: EstadoRecepcion,
    observaciones: Option[String],
    detalles: List[DetalleRecepcion]
)
