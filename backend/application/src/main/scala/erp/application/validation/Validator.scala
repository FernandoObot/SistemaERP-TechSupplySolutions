package erp.application.validation

import cats.data.{NonEmptyChain, Validated, ValidatedNec}
import cats.syntax.all.*
import erp.domain.compras.{OrdenCompraInput, RecepcionInput}
import erp.domain.maestra.{AlmacenInput, ProductoInput, ProveedorInput}

final case class ValidationError(code: String, field: String, message: String)

type Result[A] = ValidatedNec[ValidationError, A]

object Validator:
  def invalid(code: String, field: String, message: String): Result[Nothing] =
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

  def positiveInt(field: String, value: Int): Result[Int] =
    if value > 0 then Validated.valid(value)
    else invalid("RANGO", field, "debe ser mayor que 0")

  def inRange(field: String, value: Int, min: Int, max: Int): Result[Int] =
    if value >= min && value <= max then Validated.valid(value)
    else invalid("RANGO", field, s"debe estar entre $min y $max")

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

object ProveedorValidator:
  import Validator.*

  def validate(input: ProveedorInput): Result[ProveedorInput] =
    (
      nonEmpty("nombre", input.nombre),
      input.evaluacion match
        case Some(v) => inRange("evaluacion", v, 1, 5).map(Some(_))
        case None    => Validated.valid(None)
    ).mapN((_, _) => input)

object AlmacenValidator:
  import Validator.*

  def validate(input: AlmacenInput): Result[AlmacenInput] =
    (
      nonEmpty("codigo", input.codigo),
      nonEmpty("nombre", input.nombre),
      nonNegativeInt("capacidad", input.capacidad)
    ).mapN((_, _, _) => input)

object ComprasValidator:
  import Validator.*

  def validarOrden(input: OrdenCompraInput): Result[OrdenCompraInput] =
    if input.detalles.isEmpty then
      invalid("REQUERIDO", "detalles", "debe incluir al menos una línea")
    else
      input.detalles.zipWithIndex
        .traverse { case (d, i) =>
          (
            positiveInt(s"detalles[$i].cantidad", d.cantidad),
            nonNegative(s"detalles[$i].precioUnitario", d.precioUnitario)
          ).mapN((_, _) => ())
        }
        .map(_ => input)

  def validarRecepcion(input: RecepcionInput): Result[RecepcionInput] =
    if input.detalles.isEmpty then
      invalid("REQUERIDO", "detalles", "debe incluir al menos una línea recibida")
    else
      input.detalles.zipWithIndex
        .traverse { case (d, i) =>
          positiveInt(s"detalles[$i].cantidadRecibida", d.cantidadRecibida).map(_ => ())
        }
        .map(_ => input)
