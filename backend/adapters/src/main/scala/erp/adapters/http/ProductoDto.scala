package erp.adapters.http

import erp.application.Page
import erp.domain.{ProductoId, ProveedorId}
import erp.domain.maestra.{Producto, ProductoInput}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*
import sttp.tapir.Schema

import java.util.UUID
import scala.util.Try

final case class ProductoInputDto(
    sku: String,
    nombre: String,
    categoria: String,
    linea: String,
    precio: BigDecimal,
    puntoReorden: Int,
    proveedorPrincipalId: Option[String],
    activo: Boolean
):
  def toDomain: ProductoInput =
    ProductoInput(
      sku = sku,
      nombre = nombre,
      categoria = categoria,
      linea = linea,
      precio = precio,
      puntoReorden = puntoReorden,
      proveedorPrincipalId =
        proveedorPrincipalId.flatMap(s => Try(UUID.fromString(s)).toOption).map(ProveedorId(_)),
      activo = activo
    )

object ProductoInputDto:
  given Encoder[ProductoInputDto] = deriveEncoder
  given Decoder[ProductoInputDto] = deriveDecoder
  given Schema[ProductoInputDto]  = Schema.derived

final case class ProductoDto(
    id: String,
    sku: String,
    nombre: String,
    categoria: String,
    linea: String,
    precio: BigDecimal,
    puntoReorden: Int,
    proveedorPrincipalId: Option[String],
    activo: Boolean,
    version: Int
)

object ProductoDto:
  given Encoder[ProductoDto] = deriveEncoder
  given Decoder[ProductoDto] = deriveDecoder
  given Schema[ProductoDto]  = Schema.derived

  def from(p: Producto): ProductoDto =
    ProductoDto(
      id = p.id.value.toString,
      sku = p.sku,
      nombre = p.nombre,
      categoria = p.categoria,
      linea = p.linea,
      precio = p.precio,
      puntoReorden = p.puntoReorden,
      proveedorPrincipalId = p.proveedorPrincipalId.map(_.value.toString),
      activo = p.activo,
      version = p.version
    )

final case class PageDto[A](
    items: List[A],
    page: Int,
    size: Int,
    total: Long,
    totalPages: Int
)

object PageDto:
  given [A: Encoder]: Encoder[PageDto[A]] = deriveEncoder
  given [A: Decoder]: Decoder[PageDto[A]] = deriveDecoder
  given [A: Schema]: Schema[PageDto[A]]   = Schema.derived

  def from[A](p: Page[A]): PageDto[A] =
    PageDto(p.items, p.page, p.size, p.total, p.totalPages)
