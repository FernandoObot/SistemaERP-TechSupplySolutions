package erp.application.maestra

import erp.application.{Page, ProductoRepository}
import erp.domain.ProductoId
import erp.domain.maestra.{Producto, ProductoInput}

final class ProductoService[F[_]](repo: ProductoRepository[F]):
  def create(input: ProductoInput): F[Producto] =
    repo.create(Producto.from(input))

  def find(id: ProductoId): F[Option[Producto]] =
    repo.find(id)

  def list(page: Int, size: Int): F[Page[Producto]] =
    repo.list(page, size)

  def update(id: ProductoId, input: ProductoInput): F[Option[Producto]] =
    repo.update(id, Producto.from(input, id = id))

  def delete(id: ProductoId): F[Boolean] =
    repo.softDelete(id)
