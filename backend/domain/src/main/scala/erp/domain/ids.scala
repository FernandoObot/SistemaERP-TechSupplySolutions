package erp.domain

import java.util.UUID

final case class ProductoId(value: UUID)

object ProductoId:
  def random(): ProductoId = ProductoId(UUID.randomUUID())
