package erp.adapters.persistence

import cats.effect.IO
import erp.application.{CrudRepository, Page}
import erp.domain.AlmacenId
import erp.domain.maestra.Almacen
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.UUID

final class AlmacenRepository(db: DbPool) extends CrudRepository[IO, Almacen, AlmacenId]:

  private val codec: Codec[Almacen] =
    (
      uuid *: text *: text *: text.opt *: int4 *: bool *: int4 *: timestamptz *: timestamptz
    ).imap {
      case (id, codigo, nombre, ubicacion, capacidad, activo, version, createdAt, updatedAt) =>
        Almacen(
          id = AlmacenId(id),
          codigo = codigo,
          nombre = nombre,
          ubicacion = ubicacion,
          capacidad = capacidad,
          activo = activo,
          version = version,
          createdAt = createdAt.toInstant,
          updatedAt = updatedAt.toInstant
        )
    } { a =>
      (
        a.id.value,
        a.codigo,
        a.nombre,
        a.ubicacion,
        a.capacidad,
        a.activo,
        a.version,
        OffsetDateTime.ofInstant(a.createdAt, ZoneOffset.UTC),
        OffsetDateTime.ofInstant(a.updatedAt, ZoneOffset.UTC)
      )
    }

  private val selectById: Query[UUID, Almacen] =
    sql"""
      select id, codigo, nombre, ubicacion, capacidad, activo, version, created_at, updated_at
      from public.almacenes
      where id = $uuid
    """.query(codec)

  private val selectPage: Query[(Int, Int), Almacen] =
    sql"""
      select id, codigo, nombre, ubicacion, capacidad, activo, version, created_at, updated_at
      from public.almacenes
      order by codigo
      limit $int4 offset $int4
    """.query(codec)

  private val countAll: Query[Void, Long] =
    sql"select count(*) from public.almacenes".query(int8)

  private val insert =
    sql"""
      insert into public.almacenes (codigo, nombre, ubicacion, capacidad, activo)
      values ($text, $text, ${text.opt}, $int4, $bool)
      returning id, codigo, nombre, ubicacion, capacidad, activo, version, created_at, updated_at
    """.query(codec)

  private val update =
    sql"""
      update public.almacenes
      set codigo = $text,
          nombre = $text,
          ubicacion = ${text.opt},
          capacidad = $int4,
          activo = $bool,
          version = version + 1
      where id = $uuid
      returning id, codigo, nombre, ubicacion, capacidad, activo, version, created_at, updated_at
    """.query(codec)

  private val softDelete: Query[UUID, UUID] =
    sql"""
      update public.almacenes
      set activo = false, version = version + 1
      where id = $uuid
      returning id
    """.query(uuid)

  def create(a: Almacen): IO[Almacen] =
    db.use(s => s.unique(insert)((a.codigo, a.nombre, a.ubicacion, a.capacidad, a.activo)))

  def find(id: AlmacenId): IO[Option[Almacen]] =
    db.use(s => s.option(selectById)(id.value))

  def update(id: AlmacenId, a: Almacen): IO[Option[Almacen]] =
    db.use { s =>
      s.option(update)((a.codigo, a.nombre, a.ubicacion, a.capacidad, a.activo, id.value))
    }

  def softDelete(id: AlmacenId): IO[Boolean] =
    db.use(s => s.option(softDelete)(id.value).map(_.isDefined))

  def list(page: Int, size: Int): IO[Page[Almacen]] =
    db.use { s =>
      val safePage = math.max(page, 0)
      val safeSize = math.max(1, math.min(size, 100))
      for
        items <- s.execute(selectPage)((safeSize, safePage * safeSize))
        total <- s.unique(countAll)
      yield Page(items, safePage, safeSize, total)
    }
