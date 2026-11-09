package erp.adapters.persistence

import cats.effect.IO
import cats.syntax.all.*
import erp.application.{ComprasRepository, Page}
import erp.domain.*
import erp.domain.compras.*
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.UUID
import scala.util.Try

final class ComprasRepositorySkunk(db: DbPool) extends ComprasRepository[IO]:

  private final case class PendienteRow(
      detalleId: UUID,
      productoId: UUID,
      cantidad: Int,
      precioUnitario: BigDecimal,
      recibido: Int
  )

  private val estadoOC: Codec[EstadoOrdenCompra] =
    text.eimap(
      s =>
        EstadoOrdenCompra.values
          .find(_.toString.equalsIgnoreCase(s))
          .toRight(s"Estado inválido: $s")
    )(_.toString.toUpperCase)

  private val estadoRecepcion: Codec[EstadoRecepcion] =
    text.eimap(
      s =>
        EstadoRecepcion.values
          .find(_.toString.equalsIgnoreCase(s))
          .toRight(s"Estado inválido: $s")
    )(_.toString.toUpperCase)

  private val ordenCodec: Codec[OrdenCompra] =
    (
      uuid *: text *: uuid *: timestamptz *: estadoOC *: numeric *: text.opt *: int4 *:
        timestamptz *: timestamptz
    ).imap {
      case (
            id,
            folio,
            proveedorId,
            fecha,
            estado,
            total,
            observaciones,
            version,
            createdAt,
            updatedAt
          ) =>
        OrdenCompra(
          id = OrdenCompraId(id),
          folio = folio,
          proveedorId = ProveedorId(proveedorId),
          fecha = fecha.toInstant,
          estado = estado,
          total = total,
          observaciones = observaciones,
          version = version,
          createdAt = createdAt.toInstant,
          updatedAt = updatedAt.toInstant,
          detalles = Nil
        )
    } { o =>
      (
        o.id.value,
        o.folio,
        o.proveedorId.value,
        OffsetDateTime.ofInstant(o.fecha, ZoneOffset.UTC),
        o.estado,
        o.total,
        o.observaciones,
        o.version,
        OffsetDateTime.ofInstant(o.createdAt, ZoneOffset.UTC),
        OffsetDateTime.ofInstant(o.updatedAt, ZoneOffset.UTC)
      )
    }

  private val detalleCodec: Codec[DetalleOC] =
    (uuid *: uuid *: int4 *: numeric *: numeric).imap {
      case (id, productoId, cantidad, precioUnitario, subtotal) =>
        DetalleOC(id, ProductoId(productoId), cantidad, precioUnitario, subtotal)
    } { d =>
      (d.id, d.productoId.value, d.cantidad, d.precioUnitario, d.subtotal)
    }

  private val pendienteCodec: Codec[PendienteRow] =
    (uuid *: uuid *: int4 *: numeric *: int4).imap {
      case (detalleId, productoId, cantidad, precioUnitario, recibido) =>
        PendienteRow(detalleId, productoId, cantidad, precioUnitario, recibido)
    } { p =>
      (p.detalleId, p.productoId, p.cantidad, p.precioUnitario, p.recibido)
    }

  private val recepcionCodec: Codec[Recepcion] =
    (uuid *: uuid *: uuid *: timestamptz *: estadoRecepcion *: text.opt).imap {
      case (id, ordenId, almacenId, fecha, estado, observaciones) =>
        Recepcion(
          id = RecepcionId(id),
          ordenCompraId = OrdenCompraId(ordenId),
          almacenId = AlmacenId(almacenId),
          fecha = fecha.toInstant,
          estado = estado,
          observaciones = observaciones,
          detalles = Nil
        )
    } { r =>
      (
        r.id.value,
        r.ordenCompraId.value,
        r.almacenId.value,
        OffsetDateTime.ofInstant(r.fecha, ZoneOffset.UTC),
        r.estado,
        r.observaciones
      )
    }

  private val detalleRecepcionCodec: Codec[DetalleRecepcion] =
    (uuid *: uuid *: uuid *: int4).imap {
      case (id, detalleOcId, productoId, cantidad) =>
        DetalleRecepcion(id, detalleOcId, ProductoId(productoId), cantidad)
    } { d =>
      (d.id, d.detalleOcId, d.productoId.value, d.cantidadRecibida)
    }

  private val insertOrden =
    sql"""
      insert into public.ordenes_compra (proveedor_id, estado, total, observaciones)
      values ($uuid, 'ENVIADA', $numeric, ${text.opt})
      returning id, folio, proveedor_id, fecha, estado, total::numeric, observaciones, version,
                created_at, updated_at
    """.query(ordenCodec)

  private val insertDetalle =
    sql"""
      insert into public.detalles_oc
        (orden_compra_id, producto_id, cantidad, precio_unitario, subtotal)
      values ($uuid, $uuid, $int4, $numeric, $numeric)
      returning id, producto_id, cantidad, precio_unitario::numeric, subtotal::numeric
    """.query(detalleCodec)

  private val selectOrden: Query[UUID, OrdenCompra] =
    sql"""
      select id, folio, proveedor_id, fecha, estado, total::numeric, observaciones, version,
             created_at, updated_at
      from public.ordenes_compra
      where id = $uuid
    """.query(ordenCodec)

  private val selectDetalles: Query[UUID, DetalleOC] =
    sql"""
      select id, producto_id, cantidad, precio_unitario::numeric, subtotal::numeric
      from public.detalles_oc
      where orden_compra_id = $uuid
      order by id
    """.query(detalleCodec)

  private val selectPage: Query[(Int, Int), OrdenCompra] =
    sql"""
      select id, folio, proveedor_id, fecha, estado, total::numeric, observaciones, version,
             created_at, updated_at
      from public.ordenes_compra
      order by fecha desc
      limit $int4 offset $int4
    """.query(ordenCodec)

  private val countAll: Query[Void, Long] =
    sql"select count(*) from public.ordenes_compra".query(int8)

  private val selectPendientes: Query[UUID, PendienteRow] =
    sql"""
      select d.id, d.producto_id, d.cantidad, d.precio_unitario::numeric,
             coalesce(sum(r.cantidad_recibida), 0)::int4 as recibido
      from public.detalles_oc d
      left join public.detalles_recepcion r on r.detalle_oc_id = d.id
      where d.orden_compra_id = $uuid
      group by d.id, d.producto_id, d.cantidad, d.precio_unitario
      order by d.id
    """.query(pendienteCodec)

  private val insertRecepcion =
    sql"""
      insert into public.recepciones (orden_compra_id, almacen_id, estado, observaciones)
      values ($uuid, $uuid, 'RECIBIDA', ${text.opt})
      returning id, orden_compra_id, almacen_id, fecha, estado, observaciones
    """.query(recepcionCodec)

  private val insertDetalleRecepcion =
    sql"""
      insert into public.detalles_recepcion
        (recepcion_id, detalle_oc_id, producto_id, cantidad_recibida)
      values ($uuid, $uuid, $uuid, $int4)
      returning id, detalle_oc_id, producto_id, cantidad_recibida
    """.query(detalleRecepcionCodec)

  private val insertMovimiento: Command[(UUID, UUID, Int, String)] =
    sql"""
      insert into public.movimientos_inventario
        (almacen_id, producto_id, tipo, cantidad, referencia)
      values ($uuid, $uuid, 'ENTRADA', $int4, $text)
    """.command

  private val upsertStock: Command[(UUID, UUID, Int)] =
    sql"""
      insert into public.stock (producto_id, almacen_id, cantidad)
      values ($uuid, $uuid, $int4)
      on conflict (producto_id, almacen_id)
      do update set cantidad = public.stock.cantidad + excluded.cantidad, updated_at = now()
    """.command

  private val insertCuenta: Command[(UUID, UUID, UUID, BigDecimal, BigDecimal)] =
    sql"""
      insert into public.cuentas_por_pagar
        (recepcion_id, orden_compra_id, proveedor_id, monto, saldo)
      values ($uuid, $uuid, $uuid, $numeric, $numeric)
    """.command

  private val updateEstadoOC: Command[(String, UUID)] =
    sql"""
      update public.ordenes_compra
      set estado = $text, version = version + 1
      where id = $uuid
    """.command

  private val selectRecepciones: Query[UUID, Recepcion] =
    sql"""
      select id, orden_compra_id, almacen_id, fecha, estado, observaciones
      from public.recepciones
      where orden_compra_id = $uuid
      order by fecha desc
    """.query(recepcionCodec)

  private val selectDetallesRecepcion: Query[UUID, DetalleRecepcion] =
    sql"""
      select id, detalle_oc_id, producto_id, cantidad_recibida
      from public.detalles_recepcion
      where recepcion_id = $uuid
    """.query(detalleRecepcionCodec)

  def createOrden(input: OrdenCompraInput): IO[Either[DomainError, OrdenCompra]] =
    db.transaction { s =>
      val total = OrdenCompra.total(input.detalles)
      val detalles = input.detalles.map { d =>
        DetalleOC(
          id = UUID.randomUUID(),
          productoId = d.productoId,
          cantidad = d.cantidad,
          precioUnitario = d.precioUnitario,
          subtotal = OrdenCompra.subtotal(d)
        )
      }
      for
        oc <- s.unique(insertOrden)((input.proveedorId.value, total, input.observaciones))
        insertados <- detalles.traverse { d =>
          s.unique(insertDetalle)(
            (oc.id.value, d.productoId.value, d.cantidad, d.precioUnitario, d.subtotal)
          )
        }
      yield Right(oc.copy(detalles = insertados))
    }

  def findOrden(id: OrdenCompraId): IO[Option[OrdenCompra]] =
    db.use { s =>
      for
        oc <- s.option(selectOrden)(id.value)
        detalles <- oc.fold(IO.pure(List.empty[DetalleOC]))(_ => s.execute(selectDetalles)(id.value))
      yield oc.map(_.copy(detalles = detalles))
    }

  def listOrdenes(page: Int, size: Int): IO[Page[OrdenCompra]] =
    db.use { s =>
      val safePage = math.max(page, 0)
      val safeSize = math.max(1, math.min(size, 100))
      for
        items <- s.execute(selectPage)((safeSize, safePage * safeSize))
        total <- s.unique(countAll)
      yield Page(items, safePage, safeSize, total)
    }

  def recibir(id: OrdenCompraId, input: RecepcionInput): IO[Either[DomainError, Recepcion]] =
    db.transaction { s =>
      for
        oc <- s.option(selectOrden)(id.value)
        result <- oc match
          case None =>
            IO.pure(Left(DomainError.NotFound("Orden de compra", id.value.toString)))
          case Some(orden) =>
            s.execute(selectPendientes)(id.value).flatMap { pendientes =>
              validarYRegistrar(s, orden, pendientes, input)
            }
      yield result
    }

  def listRecepciones(id: OrdenCompraId): IO[List[Recepcion]] =
    db.use { s =>
      for
        recepciones <- s.execute(selectRecepciones)(id.value)
        conDetalles <- recepciones.traverse { r =>
          s.execute(selectDetallesRecepcion)(r.id.value).map(ds => r.copy(detalles = ds))
        }
      yield conDetalles
    }

  private def validarYRegistrar(
      s: Session[IO],
      orden: OrdenCompra,
      pendientes: List[PendienteRow],
      input: RecepcionInput
  ): IO[Either[DomainError, Recepcion]] =
    val porId = pendientes.map(p => p.detalleId -> p).toMap
    val errores = input.detalles.flatMap { d =>
      porId.get(d.detalleOcId) match
        case None =>
          Some(s"El detalle ${d.detalleOcId} no pertenece a la orden ${orden.folio}")
        case Some(p) =>
          val disponible = p.cantidad - p.recibido
          if d.cantidadRecibida > disponible then
            Some(
              s"Detalle ${p.detalleId}: se intentan recibir ${d.cantidadRecibida} pero solo quedan $disponible"
            )
          else None
    }

    if errores.nonEmpty then IO.pure(Left(DomainError.Business(errores.mkString("; "))))
    else
      for
        recepcion <- s.unique(insertRecepcion)(
          (orden.id.value, input.almacenId.value, input.observaciones)
        )
        detalles <- input.detalles.traverse { d =>
          val p = porId(d.detalleOcId)
          for
            det <- s.unique(insertDetalleRecepcion)(
              (recepcion.id.value, d.detalleOcId, p.productoId, d.cantidadRecibida)
            )
            _ <- s.execute(insertMovimiento)(
              (input.almacenId.value, p.productoId, d.cantidadRecibida, orden.folio)
            )
            _ <- s.execute(upsertStock)(
              (p.productoId, input.almacenId.value, d.cantidadRecibida)
            )
          yield det
        }
        monto = input.detalles
          .map(d => porId(d.detalleOcId).precioUnitario * BigDecimal(d.cantidadRecibida))
          .sum
        _ <- s.execute(insertCuenta)(
          (recepcion.id.value, orden.id.value, orden.proveedorId.value, monto, monto)
        )
        nuevoEstado = if estaCompleta(pendientes, input) then "RECIBIDA" else "PARCIAL"
        _ <- s.execute(updateEstadoOC)((nuevoEstado, orden.id.value))
      yield Right(recepcion.copy(detalles = detalles))

  private def estaCompleta(pendientes: List[PendienteRow], input: RecepcionInput): Boolean =
    val porDetalle = input.detalles.groupBy(_.detalleOcId).view.mapValues(_.map(_.cantidadRecibida).sum).toMap
    pendientes.forall { p =>
      val recibidoAhora = porDetalle.getOrElse(p.detalleId, 0)
      p.recibido + recibidoAhora >= p.cantidad
    }
