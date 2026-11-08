package erp.adapters.http

import cats.effect.IO
import erp.application.maestra.ProductoService
import org.http4s.HttpApp
import org.http4s.implicits.*
import org.http4s.server.middleware.CORS
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

final class HttpApi(productoService: ProductoService[IO]):

  private val productoEndpoints = ProductoEndpoints(productoService).all
  private val serverEndpoints   = Health.route :: productoEndpoints

  private val swaggerEndpoints =
    SwaggerInterpreter()
      .fromServerEndpoints[IO](serverEndpoints, "ERP TechSupply Solutions", "0.1.0")

  val routes: HttpApp[IO] =
    val app = Http4sServerInterpreter[IO]()
      .toRoutes(serverEndpoints ++ swaggerEndpoints)
      .orNotFound
    CORS.policy.withAllowOriginAll(app)
