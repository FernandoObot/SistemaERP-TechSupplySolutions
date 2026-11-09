package erp.domain.maestra

import erp.domain.ProveedorId

import java.time.Instant

final case class ProveedorInput(
    nombre: String,
    contacto: Option[String],
    email: Option[String],
    telefono: Option[String],
    condicionesPago: Option[String],
    evaluacion: Option[Int],
    activo: Boolean
)

final case class Proveedor(
    id: ProveedorId,
    nombre: String,
    contacto: Option[String],
    email: Option[String],
    telefono: Option[String],
    condicionesPago: Option[String],
    evaluacion: Option[Int],
    activo: Boolean,
    version: Int,
    createdAt: Instant,
    updatedAt: Instant
)

object Proveedor:
  def from(
      input: ProveedorInput,
      id: ProveedorId = ProveedorId.random(),
      now: Instant = Instant.now()
  ): Proveedor =
    Proveedor(
      id = id,
      nombre = input.nombre.trim,
      contacto = input.contacto.map(_.trim).filter(_.nonEmpty),
      email = input.email.map(_.trim).filter(_.nonEmpty),
      telefono = input.telefono.map(_.trim).filter(_.nonEmpty),
      condicionesPago = input.condicionesPago.map(_.trim).filter(_.nonEmpty),
      evaluacion = input.evaluacion,
      activo = input.activo,
      version = 1,
      createdAt = now,
      updatedAt = now
    )
