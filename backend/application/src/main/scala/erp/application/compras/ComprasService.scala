package erp.application.compras

import cats.Applicative
import cats.data.Validated
import cats.syntax.all.*
import erp.application.ComprasRepository
import erp.domain.DomainError
import erp.domain.OrdenCompraId
import erp.domain.compras.{OrdenCompra, OrdenCompraInput, Recepcion, RecepcionInput}
import erp.application.validation.ComprasValidator

final class ComprasService[F[_]: Applicative](repo: ComprasRepository[F]):

  def create(input: OrdenCompraInput): F[Either[DomainError, OrdenCompra]] =
    ComprasValidator.validarOrden(input) match
      case Validated.Valid(_) =>
        repo.createOrden(input)
      case Validated.Invalid(errs) =>
        Applicative[F].pure(Left(DomainError.Validation(errs.toList.map(toMessage))))

  def find(id: OrdenCompraId): F[Option[OrdenCompra]] =
    repo.findOrden(id)

  def list(page: Int, size: Int) =
    repo.listOrdenes(page, size)

  def listRecepciones(id: OrdenCompraId) =
    repo.listRecepciones(id)

  def recibir(id: OrdenCompraId, input: RecepcionInput): F[Either[DomainError, Recepcion]] =
    ComprasValidator.validarRecepcion(input) match
      case Validated.Valid(_) =>
        repo.recibir(id, input)
      case Validated.Invalid(errs) =>
        Applicative[F].pure(Left(DomainError.Validation(errs.toList.map(toMessage))))

  private def toMessage(e: erp.application.validation.ValidationError): String =
    s"${e.field}: ${e.message}"
