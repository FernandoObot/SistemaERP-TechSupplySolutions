package erp.server

import cats.effect.{IO, IOApp}
import erp.adapters.http.HttpApi
import erp.adapters.persistence.InMemoryProductoRepository
import erp.application.maestra.ProductoService
import org.http4s.ember.server.EmberServerBuilder

object Main extends IOApp.Simple:

  def run: IO[Unit] =
    for
      repo    <- InMemoryProductoRepository.create
      service  = ProductoService[IO](repo)
      api      = HttpApi(service)
      _       <- EmberServerBuilder
                   .default[IO]
                   .withHttpApp(api.routes)
                   .build
                   .useForever
    yield ()
