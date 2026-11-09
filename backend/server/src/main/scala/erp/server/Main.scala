package erp.server

import cats.effect.{IO, IOApp}
import erp.adapters.http.HttpApi
import erp.adapters.persistence.{
  AlmacenRepository,
  ComprasRepositorySkunk,
  DbConfig,
  DbPool,
  ProductoRepository,
  ProveedorRepository
}
import erp.application.compras.ComprasService
import erp.application.maestra.{AlmacenService, ProductoService, ProveedorService}
import erp.infrastructure.AppConfig
import org.http4s.ember.server.EmberServerBuilder

object Main extends IOApp.Simple:

  def run: IO[Unit] =
    val config = AppConfig.load
    DbConfig.fromUri(config.databaseUrl) match
      case Left(error) =>
        IO.raiseError(new RuntimeException(s"Configuración de base de datos inválida: $error"))
      case Right(dbConfig) =>
        DbPool.create(dbConfig).use { db =>
          val api = HttpApi(
            ProductoService(ProductoRepository(db)),
            ProveedorService(ProveedorRepository(db)),
            AlmacenService(AlmacenRepository(db)),
            ComprasService(ComprasRepositorySkunk(db))
          )
          EmberServerBuilder
            .default[IO]
            .withHttpApp(api.routes)
            .build
            .useForever
        }
