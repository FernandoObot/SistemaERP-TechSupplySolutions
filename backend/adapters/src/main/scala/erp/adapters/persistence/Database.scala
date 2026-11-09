package erp.adapters.persistence

import cats.effect.{IO, Resource}
import org.typelevel.otel4s.metrics.Meter
import org.typelevel.otel4s.trace.Tracer
import skunk.Session
import skunk.SSL

import java.net.URI
import scala.util.Try

final case class DbConfig(
    host: String,
    port: Int,
    user: String,
    password: String,
    database: String
)

object DbConfig:
  def fromUri(value: String): Either[String, DbConfig] =
    if value == null || value.trim.isEmpty then Left("DATABASE_URL no está configurada")
    else
      Try(new URI(value.trim)).toEither.left.map(_.getMessage).flatMap { uri =>
        val scheme = Option(uri.getScheme).getOrElse("")
        if scheme != "postgres" && scheme != "postgresql" then
          Left(s"Esquema no soportado en DATABASE_URL: $scheme")
        else
          val userInfo = Option(uri.getUserInfo).getOrElse("")
          val parts = userInfo.split(":", 2)
          val user = parts.headOption.getOrElse("")
          val password = if parts.length > 1 then parts(1) else ""
          val host = Option(uri.getHost).getOrElse("")
          val port = if uri.getPort > 0 then uri.getPort else 5432
          val database = Option(uri.getPath).map(_.stripPrefix("/")).filter(_.nonEmpty).getOrElse("postgres")
          if host.isEmpty || user.isEmpty then Left("DATABASE_URL incompleta (host o usuario)")
          else Right(DbConfig(host, port, user, password, database))
      }

final class DbPool(private val pool: Resource[IO, Session[IO]]):
  def use[A](f: Session[IO] => IO[A]): IO[A] =
    pool.use(f)

  /** Ejecuta un bloque dentro de una transacción. Se usa la misma sesión. */
  def transaction[A](f: Session[IO] => IO[A]): IO[A] =
    pool.use(session => session.transaction.use(_ => f(session)))

object DbPool:
  given Tracer[IO] = Tracer.noop[IO]
  given Meter[IO]  = Meter.noop[IO]

  def create(config: DbConfig, max: Int = 10): Resource[IO, DbPool] =
    Session
      .Builder[IO]
      .withHost(config.host)
      .withPort(config.port)
      .withUserAndPassword(config.user, config.password)
      .withDatabase(config.database)
      .withSSL(SSL.Trusted)
      .pooled(max)
      .map(new DbPool(_))
