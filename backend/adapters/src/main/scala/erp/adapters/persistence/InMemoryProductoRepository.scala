package erp.adapters.persistence

import cats.effect.{IO, Ref}
import cats.syntax.all.*
import erp.application.{CrudRepository, Page}
import erp.domain.ProductoId
import erp.domain.maestra.Producto

/** Implementación en memoria para el Sprint 0. Se reemplaza por Skunk en el Sprint 1. */
final class InMemoryProductoRepository(ref: Ref[IO, Map[ProductoId, Producto]])
    extends CrudRepository[IO, Producto, ProductoId]:

  def create(a: Producto): IO[Producto] =
    ref.update(_ + (a.id -> a)).as(a)

  def find(id: ProductoId): IO[Option[Producto]] =
    ref.get.map(_.get(id))

  def update(id: ProductoId, a: Producto): IO[Producto] =
    ref.modify { map =>
      val updated = map.get(id) match
        case Some(existing) =>
          a.copy(version = existing.version + 1, createdAt = existing.createdAt)
        case None =>
          a
      (map + (id -> updated), updated)
    }

  def softDelete(id: ProductoId): IO[Unit] =
    ref.update { map =>
      map.get(id) match
        case Some(existing) => map + (id -> existing.copy(activo = false))
        case None           => map
    }

  def list(page: Int, size: Int): IO[Page[Producto]] =
    ref.get.map { map =>
      val all      = map.values.toList.sortBy(_.nombre)
      val safePage = math.max(page, 0)
      val safeSize = math.max(1, math.min(size, 100))
      Page(
        items = all.drop(safePage * safeSize).take(safeSize),
        page = safePage,
        size = safeSize,
        total = all.size.toLong
      )
    }

object InMemoryProductoRepository:
  def create: IO[InMemoryProductoRepository] =
    Ref.of[IO, Map[ProductoId, Producto]](Map.empty).map(new InMemoryProductoRepository(_))
