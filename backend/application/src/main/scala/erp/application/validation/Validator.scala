package erp.application.validation

import cats.data.{NonEmptyChain, Validated, ValidatedNec}
import cats.syntax.all.*
import erp.domain.maestra.ProductoInput

final case class ValidationError(code: String, field: String, message: String)

type Result[A] = ValidatedNec[ValidationError, A]

object Validator:
  private def invalid(code: String, field: String, message: String): Result[Nothing] =
    Validated.invalid(NonEmptyChain(ValidationError(code, field, message)))

  def nonEmpty(field: String, value: String): Result[String] =
    if value != null && value.trim.nonEmpty then Validated.valid(value)
    else invalid("REQUERIDO", field, "es obligatorio")

  def nonNegative(field: String, value: BigDecimal): Result[BigDecimal] =
    if value >= 0 then Validated.valid(value)
    else invalid("RANGO", field, "debe ser mayor o igual a 0")

  def nonNegativeInt(field: String, value: Int): Result[Int] =
    if value >= 0 then Validated.valid(value)
    else invalid("RANGO", field, "debe ser mayor o igual a 0")

object ProductoValidator:
  import Validator.*

  def validate(input: ProductoInput): Result[ProductoInput] =
    (
      nonEmpty("sku", input.sku),
      nonEmpty("nombre", input.nombre),
      nonEmpty("categoria", input.categoria),
      nonEmpty("linea", input.linea),
      nonNegative("precio", input.precio),
      nonNegativeInt("puntoReorden", input.puntoReorden)
    ).mapN((_, _, _, _, _, _) => input)
