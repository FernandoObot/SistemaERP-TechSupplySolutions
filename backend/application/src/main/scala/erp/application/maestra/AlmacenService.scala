package erp.application.maestra

import erp.application.{AlmacenRepository, Page}
import erp.domain.AlmacenId
import erp.domain.maestra.{Almacen, AlmacenInput}

final class AlmacenService[F[_]](repo: AlmacenRepository[F]):
  def create(input: AlmacenInput): F[Almacen] =
    repo.create(Almacen.from(input))

  def find(id: AlmacenId): F[Option[Almacen]] =
    repo.find(id)

  def list(page: Int, size: Int): F[Page[Almacen]] =
    repo.list(page, size)

  def update(id: AlmacenId, input: AlmacenInput): F[Option[Almacen]] =
    repo.update(id, Almacen.from(input, id = id))

  def delete(id: AlmacenId): F[Boolean] =
    repo.softDelete(id)
