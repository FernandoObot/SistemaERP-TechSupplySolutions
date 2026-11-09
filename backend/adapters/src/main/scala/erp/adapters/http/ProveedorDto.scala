package erp.adapters.http

import erp.domain.ProveedorId
import erp.domain.maestra.{Proveedor, ProveedorInput}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*
import sttp.tapir.Schema

final case class ProveedorInputDto(
    nombre: String,
    contacto: Option[String],
    email: Option[String],
    telefono: Option[String],
    condicionesPago: Option[String],
    evaluacion: Option[Int],
    activo: Boolean
):
  def toDomain: ProveedorInput =
    ProveedorInput(
      nombre = nombre,
      contacto = contacto,
      email = email,
      telefono = telefono,
      condicionesPago = condicionesPago,
      evaluacion = evaluacion,
      activo = activo
    )

object ProveedorInputDto:
  given Encoder[ProveedorInputDto] = deriveEncoder
  given Decoder[ProveedorInputDto] = deriveDecoder
  given Schema[ProveedorInputDto]  = Schema.derived

final case class ProveedorDto(
    id: String,
    nombre: String,
    contacto: Option[String],
    email: Option[String],
    telefono: Option[String],
    condicionesPago: Option[String],
    evaluacion: Option[Int],
    activo: Boolean,
    version: Int
)

object ProveedorDto:
  given Encoder[ProveedorDto] = deriveEncoder
  given Decoder[ProveedorDto] = deriveDecoder
  given Schema[ProveedorDto]  = Schema.derived

  def from(p: Proveedor): ProveedorDto =
    ProveedorDto(
      id = p.id.value.toString,
      nombre = p.nombre,
      contacto = p.contacto,
      email = p.email,
      telefono = p.telefono,
      condicionesPago = p.condicionesPago,
      evaluacion = p.evaluacion,
      activo = p.activo,
      version = p.version
    )
