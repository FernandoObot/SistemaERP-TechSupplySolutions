package erp.adapters.http

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.json.circe.*

final case class ApiError(
    code: String,
    message: String,
    details: List[String] = Nil,
    traceId: String = ""
)

object ApiError:
  given Encoder[ApiError] = deriveEncoder
  given Decoder[ApiError] = deriveDecoder
  given Schema[ApiError]  = Schema.derived

object ApiErrors:
  type Failure = (StatusCode, ApiError)

  /** Error con código HTTP dinámico. */
  val output: EndpointOutput[Failure] =
    statusCode.and(jsonBody[ApiError])

  def notFound(message: String): Failure =
    (StatusCode.NotFound, ApiError("NO_ENCONTRADO", message))

  def validation(details: List[String]): Failure =
    (StatusCode.UnprocessableEntity, ApiError("VALIDACION", "La solicitud contiene errores", details))

  def business(message: String): Failure =
    (StatusCode.UnprocessableEntity, ApiError("REGLA_NEGOCIO", message))

  def conflict(message: String): Failure =
    (StatusCode.Conflict, ApiError("CONFLICTO", message))
