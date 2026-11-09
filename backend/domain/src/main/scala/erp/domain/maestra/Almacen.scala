package erp.domain.maestra

import erp.domain.AlmacenId

import java.time.Instant

final case class AlmacenInput(
    codigo: String,
    nombre: String,
    ubicacion: Option[String],
    capacidad: Int,
    activo: Boolean
)

final case class Almacen(
    id: AlmacenId,
    codigo: String,
    nombre: String,
    ubicacion: Option[String],
    capacidad: Int,
    activo: Boolean,
    version: Int,
    createdAt: Instant,
    updatedAt: Instant
)

object Almacen:
  def from(
      input: AlmacenInput,
      id: AlmacenId = AlmacenId.random(),
      now: Instant = Instant.now()
  ): Almacen =
    Almacen(
      id = id,
      codigo = input.codigo.trim.toUpperCase,
      nombre = input.nombre.trim,
      ubicacion = input.ubicacion.map(_.trim).filter(_.nonEmpty),
      capacidad = input.capacidad,
      activo = input.activo,
      version = 1,
      createdAt = now,
      updatedAt = now
    )
