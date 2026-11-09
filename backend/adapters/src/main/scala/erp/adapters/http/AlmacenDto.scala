package erp.adapters.http

import erp.domain.maestra.{Almacen, AlmacenInput}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*
import sttp.tapir.Schema

final case class AlmacenInputDto(
    codigo: String,
    nombre: String,
    ubicacion: Option[String],
    capacidad: Int,
    activo: Boolean
):
  def toDomain: AlmacenInput =
    AlmacenInput(codigo, nombre, ubicacion, capacidad, activo)

object AlmacenInputDto:
  given Encoder[AlmacenInputDto] = deriveEncoder
  given Decoder[AlmacenInputDto] = deriveDecoder
  given Schema[AlmacenInputDto]  = Schema.derived

final case class AlmacenDto(
    id: String,
    codigo: String,
    nombre: String,
    ubicacion: Option[String],
    capacidad: Int,
    activo: Boolean,
    version: Int
)

object AlmacenDto:
  given Encoder[AlmacenDto] = deriveEncoder
  given Decoder[AlmacenDto] = deriveDecoder
  given Schema[AlmacenDto]  = Schema.derived

  def from(a: Almacen): AlmacenDto =
    AlmacenDto(
      id = a.id.value.toString,
      codigo = a.codigo,
      nombre = a.nombre,
      ubicacion = a.ubicacion,
      capacidad = a.capacidad,
      activo = a.activo,
      version = a.version
    )
