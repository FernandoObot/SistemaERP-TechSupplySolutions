package erp.adapters.http

import cats.effect.IO
import erp.application.compras.ComprasService
import erp.application.maestra.{AlmacenService, ProductoService, ProveedorService}
import org.http4s.HttpApp
import org.http4s.implicits.*
import org.http4s.server.middleware.CORS
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

final class HttpApi(
    productoService: ProductoService[IO],
    proveedorService: ProveedorService[IO],
    almacenService: AlmacenService[IO],
    comprasService: ComprasService[IO]
):

  private val endpoints: List[ServerEndpoint[Any, IO]] =
    Health.route ::
      (ProductoEndpoints(productoService).all ++
        ProveedorEndpoints(proveedorService).all ++
        AlmacenEndpoints(almacenService).all ++
        ComprasEndpoints(comprasService).all)

  private val swaggerEndpoints =
    SwaggerInterpreter()
      .fromServerEndpoints[IO](endpoints, "ERP TechSupply Solutions", "0.2.0")

  val routes: HttpApp[IO] =
    val app = Http4sServerInterpreter[IO]()
      .toRoutes(endpoints ++ swaggerEndpoints)
      .orNotFound
    CORS.policy.withAllowOriginAll(app)
