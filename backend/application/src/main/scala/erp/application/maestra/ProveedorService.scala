package erp.application.maestra

import erp.application.{Page, ProveedorRepository}
import erp.domain.ProveedorId
import erp.domain.maestra.{Proveedor, ProveedorInput}

final class ProveedorService[F[_]](repo: ProveedorRepository[F]):
  def create(input: ProveedorInput): F[Proveedor] =
    repo.create(Proveedor.from(input))

  def find(id: ProveedorId): F[Option[Proveedor]] =
    repo.find(id)

  def list(page: Int, size: Int): F[Page[Proveedor]] =
    repo.list(page, size)

  def update(id: ProveedorId, input: ProveedorInput): F[Option[Proveedor]] =
    repo.update(id, Proveedor.from(input, id = id))

  def delete(id: ProveedorId): F[Boolean] =
    repo.softDelete(id)
