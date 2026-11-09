package erp.adapters.http

import cats.effect.IO
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint

object Health:
  private val healthEndpoint: PublicEndpoint[Unit, ApiErrors.Failure, String, Any] =
    endpoint.get.in("health").out(stringBody).errorOut(ApiErrors.output)

  val route: ServerEndpoint[Any, IO] =
    healthEndpoint.serverLogicSuccess[IO](_ => IO.pure("ok"))
