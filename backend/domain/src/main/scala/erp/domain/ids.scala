package erp.domain

import java.util.UUID

final case class ProductoId(value: UUID)
object ProductoId:
  def random(): ProductoId = ProductoId(UUID.randomUUID())

final case class ProveedorId(value: UUID)
object ProveedorId:
  def random(): ProveedorId = ProveedorId(UUID.randomUUID())

final case class ClienteId(value: UUID)
object ClienteId:
  def random(): ClienteId = ClienteId(UUID.randomUUID())

final case class EmpleadoId(value: UUID)
object EmpleadoId:
  def random(): EmpleadoId = EmpleadoId(UUID.randomUUID())

final case class AlmacenId(value: UUID)
object AlmacenId:
  def random(): AlmacenId = AlmacenId(UUID.randomUUID())

final case class OrdenCompraId(value: UUID)
object OrdenCompraId:
  def random(): OrdenCompraId = OrdenCompraId(UUID.randomUUID())

final case class RecepcionId(value: UUID)
object RecepcionId:
  def random(): RecepcionId = RecepcionId(UUID.randomUUID())
