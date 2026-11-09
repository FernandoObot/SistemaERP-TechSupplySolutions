package erp.adapters.persistence

import cats.effect.IO
import erp.application.{CrudRepository, Page}
import erp.domain.{ProductoId, ProveedorId}
import erp.domain.maestra.Producto
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.UUID

final class ProductoRepository(db: DbPool) extends CrudRepository[IO, Producto, ProductoId]:

  private val codec: Codec[Producto] =
    (
      uuid *: text *: text *: text *: text *: numeric *: int4 *: uuid.opt *: bool *:
        int4 *: timestamptz *: timestamptz
    ).imap {
      case (
            id,
            sku,
            nombre,
            categoria,
            linea,
            precio,
            puntoReorden,
            proveedorPrincipalId,
            activo,
            version,
            createdAt,
            updatedAt
          ) =>
        Producto(
          id = ProductoId(id),
          sku = sku,
          nombre = nombre,
          categoria = categoria,
          linea = linea,
          precio = precio,
          puntoReorden = puntoReorden,
          proveedorPrincipalId = proveedorPrincipalId.map(ProveedorId(_)),
          activo = activo,
          version = version,
          createdAt = createdAt.toInstant,
          updatedAt = updatedAt.toInstant
        )
    } { p =>
      (
        p.id.value,
        p.sku,
        p.nombre,
        p.categoria,
        p.linea,
        p.precio,
        p.puntoReorden,
        p.proveedorPrincipalId.map(_.value),
        p.activo,
        p.version,
        OffsetDateTime.ofInstant(p.createdAt, ZoneOffset.UTC),
        OffsetDateTime.ofInstant(p.updatedAt, ZoneOffset.UTC)
      )
    }

  private val selectById: Query[UUID, Producto] =
    sql"""
      select id, sku, nombre, categoria, linea, precio::numeric, punto_reorden,
             proveedor_principal_id, activo, version, created_at, updated_at
      from public.productos
      where id = $uuid
    """.query(codec)

  private val selectPage: Query[(Int, Int), Producto] =
    sql"""
      select id, sku, nombre, categoria, linea, precio::numeric, punto_reorden,
             proveedor_principal_id, activo, version, created_at, updated_at
      from public.productos
      order by nombre
      limit $int4 offset $int4
    """.query(codec)

  private val countAll: Query[Void, Long] =
    sql"select count(*) from public.productos".query(int8)

  private val insert =
    sql"""
      insert into public.productos
        (sku, nombre, categoria, linea, precio, punto_reorden, proveedor_principal_id, activo)
      values ($text, $text, $text, $text, $numeric, $int4, ${uuid.opt}, $bool)
      returning id, sku, nombre, categoria, linea, precio::numeric, punto_reorden,
                proveedor_principal_id, activo, version, created_at, updated_at
    """.query(codec)

  private val update =
    sql"""
      update public.productos
      set sku = $text,
          nombre = $text,
          categoria = $text,
          linea = $text,
          precio = $numeric,
          punto_reorden = $int4,
          proveedor_principal_id = ${uuid.opt},
          activo = $bool,
          version = version + 1
      where id = $uuid
      returning id, sku, nombre, categoria, linea, precio::numeric, punto_reorden,
                proveedor_principal_id, activo, version, created_at, updated_at
    """.query(codec)

  private val softDelete: Query[UUID, UUID] =
    sql"""
      update public.productos
      set activo = false, version = version + 1
      where id = $uuid
      returning id
    """.query(uuid)

  def create(a: Producto): IO[Producto] =
    db.use { s =>
      s.unique(insert)(
        (
          a.sku,
          a.nombre,
          a.categoria,
          a.linea,
          a.precio,
          a.puntoReorden,
          a.proveedorPrincipalId.map(_.value),
          a.activo
        )
      )
    }

  def find(id: ProductoId): IO[Option[Producto]] =
    db.use(s => s.option(selectById)(id.value))

  def update(id: ProductoId, a: Producto): IO[Option[Producto]] =
    db.use { s =>
      s.option(update)(
        (
          a.sku,
          a.nombre,
          a.categoria,
          a.linea,
          a.precio,
          a.puntoReorden,
          a.proveedorPrincipalId.map(_.value),
          a.activo,
          id.value
        )
      )
    }

  def softDelete(id: ProductoId): IO[Boolean] =
    db.use(s => s.option(softDelete)(id.value).map(_.isDefined))

  def list(page: Int, size: Int): IO[Page[Producto]] =
    db.use { s =>
      val safePage = math.max(page, 0)
      val safeSize = math.max(1, math.min(size, 100))
      for
        items <- s.execute(selectPage)((safeSize, safePage * safeSize))
        total <- s.unique(countAll)
      yield Page(items, safePage, safeSize, total)
    }
