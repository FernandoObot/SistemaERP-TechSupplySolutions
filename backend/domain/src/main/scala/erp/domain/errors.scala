package erp.domain

/** Error de negocio. `code` es estable y se usa en el envelope de error de la API. */
sealed trait DomainError extends Product with Serializable:
  def code: String
  def message: String

object DomainError:
  final case class NotFound(entity: String, id: String) extends DomainError:
    val code: String    = "NO_ENCONTRADO"
    val message: String = s"$entity con id $id no existe"

  final case class Validation(details: List[String]) extends DomainError:
    val code: String    = "VALIDACION"
    val message: String = "La solicitud contiene errores"

  final case class Conflict(message: String) extends DomainError:
    val code: String = "CONFLICTO"

  final case class Business(message: String) extends DomainError:
    val code: String = "REGLA_NEGOCIO"
