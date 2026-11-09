package erp.domain.compras

import erp.domain.{OrdenCompraId, ProductoId, ProveedorId}

import java.time.Instant
import java.util.UUID

enum EstadoOrdenCompra:
  case Borrador, Enviada, Parcial, Recibida, Cancelada

final case class DetalleOCInput(
    productoId: ProductoId,
    cantidad: Int,
    precioUnitario: BigDecimal
)

final case class OrdenCompraInput(
    proveedorId: ProveedorId,
    observaciones: Option[String],
    detalles: List[DetalleOCInput]
)

final case class DetalleOC(
    id: UUID,
    productoId: ProductoId,
    cantidad: Int,
    precioUnitario: BigDecimal,
    subtotal: BigDecimal
)

final case class OrdenCompra(
    id: OrdenCompraId,
    folio: String,
    proveedorId: ProveedorId,
    fecha: Instant,
    estado: EstadoOrdenCompra,
    total: BigDecimal,
    observaciones: Option[String],
    version: Int,
    createdAt: Instant,
    updatedAt: Instant,
    detalles: List[DetalleOC]
)

object OrdenCompra:
  def subtotal(detalle: DetalleOCInput): BigDecimal =
    detalle.precioUnitario * BigDecimal(detalle.cantidad)

  def total(detalles: List[DetalleOCInput]): BigDecimal =
    detalles.map(subtotal).sum
