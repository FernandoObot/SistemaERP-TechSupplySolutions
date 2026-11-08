package erp.adapters.http

import cats.effect.IO
import erp.application.maestra.ProductoService
import erp.domain.ProductoId
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint

import java.util.UUID

final class ProductoEndpoints(service: ProductoService[IO]):

  private val base =
    endpoint.in("api" / "v1" / "productos").errorOut(ApiErrors.output)

  val list: ServerEndpoint[Any, IO] =
    base.get
      .in(query[Int]("page").default(0))
      .in(query[Int]("size").default(20))
      .out(jsonBody[PageDto[ProductoDto]])
      .serverLogicSuccess[IO] { (page, size) =>
        service.list(page, size).map(p => PageDto.from(p.map(ProductoDto.from)))
      }

  val create: ServerEndpoint[Any, IO] =
    base.post
      .in(jsonBody[ProductoInputDto])
      .out(jsonBody[ProductoDto])
      .serverLogicSuccess[IO] { input =>
        service.create(input.toDomain).map(ProductoDto.from)
      }

  val find: ServerEndpoint[Any, IO] =
    base.get
      .in(path[String]("id"))
      .out(jsonBody[ProductoDto])
      .serverLogic[IO] { id =>
        service.find(ProductoId(UUID.fromString(id))).map {
          case Some(p) => Right(ProductoDto.from(p))
          case None =>
            Left(ApiError("NO_ENCONTRADO", s"Producto $id no existe"))
        }
      }

  val delete: ServerEndpoint[Any, IO] =
    base.delete
      .in(path[String]("id"))
      .out(statusCode(StatusCode.NoContent))
      .serverLogicSuccess[IO] { id =>
        service.delete(ProductoId(UUID.fromString(id)))
      }

  val all: List[ServerEndpoint[Any, IO]] = List(list, create, find, delete)
