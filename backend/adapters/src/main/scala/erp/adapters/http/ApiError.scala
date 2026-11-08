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
  val output: EndpointOutput[ApiError] =
    oneOf[ApiError](
      oneOfVariant(StatusCode.BadRequest, jsonBody[ApiError]),
      oneOfVariant(StatusCode.NotFound, jsonBody[ApiError]),
      oneOfVariant(StatusCode.Conflict, jsonBody[ApiError]),
      oneOfVariant(StatusCode.UnprocessableEntity, jsonBody[ApiError])
    )
