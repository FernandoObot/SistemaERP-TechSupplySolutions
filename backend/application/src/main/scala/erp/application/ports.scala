package erp.application

import erp.domain.ProductoId
import erp.domain.maestra.Producto

final case class Page[A](items: List[A], page: Int, size: Int, total: Long):
  def totalPages: Int =
    if size <= 0 then 0 else ((total + size - 1) / size).toInt

  def map[B](f: A => B): Page[B] =
    Page(items.map(f), page, size, total)

/** Puerto genérico de persistencia para catálogos (C-07). */
trait CrudRepository[F[_], A, Id]:
  def create(a: A): F[A]
  def find(id: Id): F[Option[A]]
  def update(id: Id, a: A): F[A]
  def softDelete(id: Id): F[Unit]
  def list(page: Int, size: Int): F[Page[A]]

/** Puerto de autenticación/autorización (C-01). */
trait AuthPort[F[_]]:
  def currentUser: F[Option[String]]
  def requireRole(role: String): F[Unit]

/** Puerto de auditoría (C-04). */
trait AuditPort[F[_]]:
  def record(actor: String, entity: String, entityId: String, action: String): F[Unit]

/** Puerto de búsqueda avanzada (C-02). */
trait SearchPort[F[_], A]:
  def search(q: Option[String], page: Int, size: Int, sort: Option[String]): F[Page[A]]

/** Puerto de notificaciones (C-05). */
trait NotificationPort[F[_]]:
  def notify(recipient: String, template: String, data: Map[String, String]): F[Unit]

/** Puerto de reportería (C-03). */
trait ReportPort[F[_]]:
  def generate(reportId: String, params: Map[String, String], format: String): F[String]

type ProductoRepository[F[_]] = CrudRepository[F, Producto, ProductoId]
