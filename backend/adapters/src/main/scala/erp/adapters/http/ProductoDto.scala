package erp.adapters.http

import erp.application.Page
import erp.domain.maestra.{Producto, ProductoInput}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*
import sttp.tapir.Schema

final case class ProductoInputDto(
    sku: String,
    nombre: String,
    categoria: String,
    linea: String,
    precio: BigDecimal,
    puntoReorden: Int,
    activo: Boolean
):
  def toDomain: ProductoInput =
    ProductoInput(sku, nombre, categoria, linea, precio, puntoReorden, activo)

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
