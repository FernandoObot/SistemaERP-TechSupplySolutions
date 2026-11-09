package erp.adapters.http

import cats.data.Validated
import cats.effect.IO
import cats.syntax.all.*
import erp.application.maestra.AlmacenService
import erp.application.validation.{AlmacenValidator, ValidationError}
import erp.domain.AlmacenId
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint

import java.util.UUID

final class AlmacenEndpoints(service: AlmacenService[IO]):

  private val base = endpoint.in("api" / "v1" / "almacenes").errorOut(ApiErrors.output)

  private def parseId(value: String): Option[AlmacenId] =
    scala.util.Try(UUID.fromString(value)).toOption.map(AlmacenId(_))

  private def invalid(errors: List[ValidationError]): ApiErrors.Failure =
    ApiErrors.validation(errors.map(e => s"${e.field}: ${e.message}"))

  val list: ServerEndpoint[Any, IO] =
    base.get
      .in(query[Int]("page").default(0))
      .in(query[Int]("size").default(20))
      .out(jsonBody[PageDto[AlmacenDto]])
      .serverLogicSuccess[IO] { (page, size) =>
        service.list(page, size).map(p => PageDto.from(p.map(AlmacenDto.from)))
      }

  val create: ServerEndpoint[Any, IO] =
    base.post
      .in(jsonBody[AlmacenInputDto])
      .out(jsonBody[AlmacenDto])
      .serverLogic[IO] { input =>
        AlmacenValidator.validate(input.toDomain) match
          case Validated.Valid(valid) =>
            service.create(valid).map(a => Right(AlmacenDto.from(a)))
          case Validated.Invalid(errs) =>
            IO.pure(Left(invalid(errs.toList)))
      }

  val find: ServerEndpoint[Any, IO] =
    base.get
      .in(path[String]("id"))
      .out(jsonBody[AlmacenDto])
      .serverLogic[IO] { id =>
        parseId(id) match
          case None =>
            IO.pure(Left(ApiErrors.validation(List(s"id: UUID inválido ($id)"))))
          case Some(aid) =>
            service.find(aid).map {
              case Some(a) => Right(AlmacenDto.from(a))
              case None    => Left(ApiErrors.notFound(s"Almacén $id no existe"))
            }
      }

  val update: ServerEndpoint[Any, IO] =
    base.put
      .in(path[String]("id"))
      .in(jsonBody[AlmacenInputDto])
      .out(jsonBody[AlmacenDto])
      .serverLogic[IO] { case (id, input) =>
        parseId(id) match
          case None => IO.pure(Left(ApiErrors.validation(List(s"id: UUID inválido ($id)"))))
          case Some(aid) =>
            AlmacenValidator.validate(input.toDomain) match
              case Validated.Invalid(errs) =>
                IO.pure(Left(invalid(errs.toList)))
              case Validated.Valid(valid) =>
                service.update(aid, valid).map {
                  case Some(a) => Right(AlmacenDto.from(a))
                  case None    => Left(ApiErrors.notFound(s"Almacén $id no existe"))
                }
      }

  val delete: ServerEndpoint[Any, IO] =
    base.delete
      .in(path[String]("id"))
      .out(statusCode(StatusCode.NoContent))
      .serverLogic[IO] { id =>
        parseId(id) match
          case None => IO.pure(Left(ApiErrors.validation(List(s"id: UUID inválido ($id)"))))
          case Some(aid) =>
            service.delete(aid).map {
              case true  => Right(())
              case false => Left(ApiErrors.notFound(s"Almacén $id no existe"))
            }
      }

  val all: List[ServerEndpoint[Any, IO]] = List(list, create, find, update, delete)
