package erp.adapters.http

import cats.syntax.all.*
import erp.domain.{AlmacenId, OrdenCompraId, ProductoId, ProveedorId}
import erp.domain.compras.*
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*
import sttp.tapir.Schema

import java.util.UUID

private def parseUuid(value: String): Either[String, UUID] =
  scala.util.Try(UUID.fromString(value)).toEither.left.map(_ => s"UUID inválido: $value")

final case class DetalleOCInputDto(
    productoId: String,
    cantidad: Int,
    precioUnitario: BigDecimal
)

object DetalleOCInputDto:
  given Encoder[DetalleOCInputDto] = deriveEncoder
  given Decoder[DetalleOCInputDto] = deriveDecoder
  given Schema[DetalleOCInputDto]  = Schema.derived

final case class OrdenCompraInputDto(
    proveedorId: String,
    observaciones: Option[String],
    detalles: List[DetalleOCInputDto]
):
  def toDomain: Either[String, OrdenCompraInput] =
    for
      proveedor <- parseUuid(proveedorId)
      detalles <- this.detalles.traverse { d =>
        parseUuid(d.productoId).map(pid => DetalleOCInput(ProductoId(pid), d.cantidad, d.precioUnitario))
      }
    yield OrdenCompraInput(ProveedorId(proveedor), observaciones, detalles)

object OrdenCompraInputDto:
  given Encoder[OrdenCompraInputDto] = deriveEncoder
  given Decoder[OrdenCompraInputDto] = deriveDecoder
  given Schema[OrdenCompraInputDto]  = Schema.derived

final case class DetalleOCDto(
    id: String,
    productoId: String,
    cantidad: Int,
    precioUnitario: BigDecimal,
    subtotal: BigDecimal
)

object DetalleOCDto:
  given Encoder[DetalleOCDto] = deriveEncoder
  given Decoder[DetalleOCDto] = deriveDecoder
  given Schema[DetalleOCDto]  = Schema.derived

  def from(d: DetalleOC): DetalleOCDto =
    DetalleOCDto(d.id.toString, d.productoId.value.toString, d.cantidad, d.precioUnitario, d.subtotal)

final case class OrdenCompraDto(
    id: String,
    folio: String,
    proveedorId: String,
    fecha: String,
    estado: String,
    total: BigDecimal,
    observaciones: Option[String],
    version: Int,
    detalles: List[DetalleOCDto]
)

object OrdenCompraDto:
  given Encoder[OrdenCompraDto] = deriveEncoder
  given Decoder[OrdenCompraDto] = deriveDecoder
  given Schema[OrdenCompraDto]  = Schema.derived

  def from(o: OrdenCompra): OrdenCompraDto =
    OrdenCompraDto(
      id = o.id.value.toString,
      folio = o.folio,
      proveedorId = o.proveedorId.value.toString,
      fecha = o.fecha.toString,
      estado = o.estado.toString,
      total = o.total,
      observaciones = o.observaciones,
      version = o.version,
      detalles = o.detalles.map(DetalleOCDto.from)
    )

final case class DetalleRecepcionInputDto(
    detalleOcId: String,
    cantidadRecibida: Int
)

object DetalleRecepcionInputDto:
  given Encoder[DetalleRecepcionInputDto] = deriveEncoder
  given Decoder[DetalleRecepcionInputDto] = deriveDecoder
  given Schema[DetalleRecepcionInputDto]  = Schema.derived

final case class RecepcionInputDto(
    almacenId: String,
    observaciones: Option[String],
    detalles: List[DetalleRecepcionInputDto]
):
  def toDomain: Either[String, RecepcionInput] =
    for
      almacen <- parseUuid(almacenId)
      detalles <- this.detalles.traverse { d =>
        parseUuid(d.detalleOcId).map(detId => DetalleRecepcionInput(detId, d.cantidadRecibida))
      }
    yield RecepcionInput(AlmacenId(almacen), observaciones, detalles)

object RecepcionInputDto:
  given Encoder[RecepcionInputDto] = deriveEncoder
  given Decoder[RecepcionInputDto] = deriveDecoder
  given Schema[RecepcionInputDto]  = Schema.derived

final case class DetalleRecepcionDto(
    id: String,
    detalleOcId: String,
    productoId: String,
    cantidadRecibida: Int
)

object DetalleRecepcionDto:
  given Encoder[DetalleRecepcionDto] = deriveEncoder
  given Decoder[DetalleRecepcionDto] = deriveDecoder
  given Schema[DetalleRecepcionDto]  = Schema.derived

  def from(d: DetalleRecepcion): DetalleRecepcionDto =
    DetalleRecepcionDto(d.id.toString, d.detalleOcId.toString, d.productoId.value.toString, d.cantidadRecibida)

final case class RecepcionDto(
    id: String,
    ordenCompraId: String,
    almacenId: String,
    fecha: String,
    estado: String,
    observaciones: Option[String],
    detalles: List[DetalleRecepcionDto]
)

object RecepcionDto:
  given Encoder[RecepcionDto] = deriveEncoder
  given Decoder[RecepcionDto] = deriveDecoder
  given Schema[RecepcionDto]  = Schema.derived

  def from(r: Recepcion): RecepcionDto =
    RecepcionDto(
      id = r.id.value.toString,
      ordenCompraId = r.ordenCompraId.value.toString,
      almacenId = r.almacenId.value.toString,
      fecha = r.fecha.toString,
      estado = r.estado.toString,
      observaciones = r.observaciones,
      detalles = r.detalles.map(DetalleRecepcionDto.from)
    )
