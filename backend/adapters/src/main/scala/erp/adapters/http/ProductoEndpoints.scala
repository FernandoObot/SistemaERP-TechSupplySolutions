package erp.adapters.http

import cats.data.Validated
import cats.effect.IO
import cats.syntax.all.*
import erp.application.maestra.ProductoService
import erp.application.validation.{ProductoValidator, ValidationError}
import erp.domain.ProductoId
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint

import java.util.UUID

final class ProductoEndpoints(service: ProductoService[IO]):

  private val base = endpoint.in("api" / "v1" / "productos").errorOut(ApiErrors.output)

  private def parseId(value: String): Option[ProductoId] =
    scala.util.Try(UUID.fromString(value)).toOption.map(ProductoId(_))

  private def invalid(errors: List[ValidationError]): ApiErrors.Failure =
    ApiErrors.validation(errors.map(e => s"${e.field}: ${e.message}"))

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
      .serverLogic[IO] { input =>
        ProductoValidator.validate(input.toDomain) match
          case Validated.Valid(valid) =>
            service.create(valid).map(p => Right(ProductoDto.from(p)))
          case Validated.Invalid(errs) =>
            IO.pure(Left(invalid(errs.toList)))
      }

  val find: ServerEndpoint[Any, IO] =
    base.get
      .in(path[String]("id"))
      .out(jsonBody[ProductoDto])
      .serverLogic[IO] { id =>
        parseId(id) match
          case None =>
            IO.pure(Left(ApiErrors.validation(List(s"id: UUID inválido ($id)"))))
          case Some(pid) =>
            service.find(pid).map {
              case Some(p) => Right(ProductoDto.from(p))
              case None    => Left(ApiErrors.notFound(s"Producto $id no existe"))
            }
      }

  val update: ServerEndpoint[Any, IO] =
    base.put
      .in(path[String]("id"))
      .in(jsonBody[ProductoInputDto])
      .out(jsonBody[ProductoDto])
      .serverLogic[IO] { case (id, input) =>
        parseId(id) match
          case None => IO.pure(Left(ApiErrors.validation(List(s"id: UUID inválido ($id)"))))
          case Some(pid) =>
            ProductoValidator.validate(input.toDomain) match
              case Validated.Invalid(errs) =>
                IO.pure(Left(invalid(errs.toList)))
              case Validated.Valid(valid) =>
                service.update(pid, valid).map {
                  case Some(p) => Right(ProductoDto.from(p))
                  case None    => Left(ApiErrors.notFound(s"Producto $id no existe"))
                }
      }

  val delete: ServerEndpoint[Any, IO] =
    base.delete
      .in(path[String]("id"))
      .out(statusCode(StatusCode.NoContent))
      .serverLogic[IO] { id =>
        parseId(id) match
          case None => IO.pure(Left(ApiErrors.validation(List(s"id: UUID inválido ($id)"))))
          case Some(pid) =>
            service.delete(pid).map {
              case true  => Right(())
              case false => Left(ApiErrors.notFound(s"Producto $id no existe"))
            }
      }

  val all: List[ServerEndpoint[Any, IO]] = List(list, create, find, update, delete)
