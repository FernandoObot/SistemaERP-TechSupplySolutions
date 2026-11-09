package erp.adapters.persistence

import cats.effect.IO
import erp.application.{CrudRepository, Page}
import erp.domain.ProveedorId
import erp.domain.maestra.Proveedor
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.UUID

final class ProveedorRepository(db: DbPool) extends CrudRepository[IO, Proveedor, ProveedorId]:

  private val codec: Codec[Proveedor] =
    (
      uuid *: text *: text.opt *: text.opt *: text.opt *: text.opt *: int2.opt *: bool *:
        int4 *: timestamptz *: timestamptz
    ).imap {
      case (
            id,
            nombre,
            contacto,
            email,
            telefono,
            condicionesPago,
            evaluacion,
            activo,
            version,
            createdAt,
            updatedAt
          ) =>
        Proveedor(
          id = ProveedorId(id),
          nombre = nombre,
          contacto = contacto,
          email = email,
          telefono = telefono,
          condicionesPago = condicionesPago,
          evaluacion = evaluacion.map(_.toInt),
          activo = activo,
          version = version,
          createdAt = createdAt.toInstant,
          updatedAt = updatedAt.toInstant
        )
    } { p =>
      (
        p.id.value,
        p.nombre,
        p.contacto,
        p.email,
        p.telefono,
        p.condicionesPago,
        p.evaluacion.map(_.toShort),
        p.activo,
        p.version,
        OffsetDateTime.ofInstant(p.createdAt, ZoneOffset.UTC),
        OffsetDateTime.ofInstant(p.updatedAt, ZoneOffset.UTC)
      )
    }

  private val selectById: Query[UUID, Proveedor] =
    sql"""
      select id, nombre, contacto, email, telefono, condiciones_pago, evaluacion, activo,
             version, created_at, updated_at
      from public.proveedores
      where id = $uuid
    """.query(codec)

  private val selectPage: Query[(Int, Int), Proveedor] =
    sql"""
      select id, nombre, contacto, email, telefono, condiciones_pago, evaluacion, activo,
             version, created_at, updated_at
      from public.proveedores
      order by nombre
      limit $int4 offset $int4
    """.query(codec)

  private val countAll: Query[Void, Long] =
    sql"select count(*) from public.proveedores".query(int8)

  private val insert =
    sql"""
      insert into public.proveedores
        (nombre, contacto, email, telefono, condiciones_pago, evaluacion, activo)
      values ($text, ${text.opt}, ${text.opt}, ${text.opt}, ${text.opt}, ${int2.opt}, $bool)
      returning id, nombre, contacto, email, telefono, condiciones_pago, evaluacion, activo,
                version, created_at, updated_at
    """.query(codec)

  private val update =
    sql"""
      update public.proveedores
      set nombre = $text,
          contacto = ${text.opt},
          email = ${text.opt},
          telefono = ${text.opt},
          condiciones_pago = ${text.opt},
          evaluacion = ${int2.opt},
          activo = $bool,
          version = version + 1
      where id = $uuid
      returning id, nombre, contacto, email, telefono, condiciones_pago, evaluacion, activo,
                version, created_at, updated_at
    """.query(codec)

  private val softDelete: Query[UUID, UUID] =
    sql"""
      update public.proveedores
      set activo = false, version = version + 1
      where id = $uuid
      returning id
    """.query(uuid)

  def create(a: Proveedor): IO[Proveedor] =
    db.use { s =>
      s.unique(insert)(
        (
          a.nombre,
          a.contacto,
          a.email,
          a.telefono,
          a.condicionesPago,
          a.evaluacion.map(_.toShort),
          a.activo
        )
      )
    }

  def find(id: ProveedorId): IO[Option[Proveedor]] =
    db.use(s => s.option(selectById)(id.value))

  def update(id: ProveedorId, a: Proveedor): IO[Option[Proveedor]] =
    db.use { s =>
      s.option(update)(
        (
          a.nombre,
          a.contacto,
          a.email,
          a.telefono,
          a.condicionesPago,
          a.evaluacion.map(_.toShort),
          a.activo,
          id.value
        )
      )
    }

  def softDelete(id: ProveedorId): IO[Boolean] =
    db.use(s => s.option(softDelete)(id.value).map(_.isDefined))

  def list(page: Int, size: Int): IO[Page[Proveedor]] =
    db.use { s =>
      val safePage = math.max(page, 0)
      val safeSize = math.max(1, math.min(size, 100))
      for
        items <- s.execute(selectPage)((safeSize, safePage * safeSize))
        total <- s.unique(countAll)
      yield Page(items, safePage, safeSize, total)
    }
