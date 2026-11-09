package erp.adapters.http

import cats.effect.IO
import erp.application.compras.ComprasService
import erp.domain.{DomainError, OrdenCompraId}
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint

import java.util.UUID

final class ComprasEndpoints(service: ComprasService[IO]):

  private val base = endpoint.in("api" / "v1" / "ordenes-compra").errorOut(ApiErrors.output)

  private def parseId(value: String): Option[OrdenCompraId] =
    scala.util.Try(UUID.fromString(value)).toOption.map(OrdenCompraId(_))

  private def toFailure(e: DomainError): ApiErrors.Failure =
    e match
      case DomainError.NotFound(_, _)       => ApiErrors.notFound(e.message)
      case DomainError.Validation(details)  => ApiErrors.validation(details)
      case DomainError.Business(message)    => ApiErrors.business(message)
      case DomainError.Conflict(message)    => ApiErrors.conflict(message)

  val list: ServerEndpoint[Any, IO] =
    base.get
      .in(query[Int]("page").default(0))
      .in(query[Int]("size").default(20))
      .out(jsonBody[PageDto[OrdenCompraDto]])
      .serverLogicSuccess[IO] { (page, size) =>
        service.list(page, size).map(p => PageDto.from(p.map(OrdenCompraDto.from)))
      }

  val create: ServerEndpoint[Any, IO] =
    base.post
      .in(jsonBody[OrdenCompraInputDto])
      .out(jsonBody[OrdenCompraDto])
      .serverLogic[IO] { input =>
        input.toDomain match
          case Left(error) =>
            IO.pure(Left(ApiErrors.validation(List(error))))
          case Right(domain) =>
            service.create(domain).map {
              case Right(oc) => Right(OrdenCompraDto.from(oc))
              case Left(err) => Left(toFailure(err))
            }
      }

  val find: ServerEndpoint[Any, IO] =
    base.get
      .in(path[String]("id"))
      .out(jsonBody[OrdenCompraDto])
      .serverLogic[IO] { id =>
        parseId(id) match
          case None =>
            IO.pure(Left(ApiErrors.validation(List(s"id: UUID inválido ($id)"))))
          case Some(ocId) =>
            service.find(ocId).map {
              case Some(oc) => Right(OrdenCompraDto.from(oc))
              case None     => Left(ApiErrors.notFound(s"Orden de compra $id no existe"))
            }
      }

  val recibir: ServerEndpoint[Any, IO] =
    base.post
      .in(path[String]("id") / "recepciones")
      .in(jsonBody[RecepcionInputDto])
      .out(jsonBody[RecepcionDto])
      .serverLogic[IO] { case (id, input) =>
        (parseId(id), input.toDomain) match
          case (None, _) =>
            IO.pure(Left(ApiErrors.validation(List(s"id: UUID inválido ($id)"))))
          case (_, Left(error)) =>
            IO.pure(Left(ApiErrors.validation(List(error))))
          case (Some(ocId), Right(domain)) =>
            service.recibir(ocId, domain).map {
              case Right(r)  => Right(RecepcionDto.from(r))
              case Left(err) => Left(toFailure(err))
            }
      }

  val listRecepciones: ServerEndpoint[Any, IO] =
    base.get
      .in(path[String]("id") / "recepciones")
      .out(jsonBody[List[RecepcionDto]])
      .serverLogic[IO] { id =>
        parseId(id) match
          case None =>
            IO.pure(Left(ApiErrors.validation(List(s"id: UUID inválido ($id)"))))
          case Some(ocId) =>
            service.listRecepciones(ocId).map(rs => Right(rs.map(RecepcionDto.from)))
      }

  val all: List[ServerEndpoint[Any, IO]] =
    List(list, create, find, recibir, listRecepciones)
