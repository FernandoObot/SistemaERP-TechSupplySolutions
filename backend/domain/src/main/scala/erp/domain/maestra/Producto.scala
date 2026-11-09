package erp.domain.maestra

import erp.domain.{ProductoId, ProveedorId}

import java.time.Instant

final case class ProductoInput(
    sku: String,
    nombre: String,
    categoria: String,
    linea: String,
    precio: BigDecimal,
    puntoReorden: Int,
    proveedorPrincipalId: Option[ProveedorId],
    activo: Boolean
)

final case class Producto(
    id: ProductoId,
    sku: String,
    nombre: String,
    categoria: String,
    linea: String,
    precio: BigDecimal,
    puntoReorden: Int,
    proveedorPrincipalId: Option[ProveedorId],
    activo: Boolean,
    version: Int,
    createdAt: Instant,
    updatedAt: Instant
)

object Producto:
  def from(
      input: ProductoInput,
      id: ProductoId = ProductoId.random(),
      now: Instant = Instant.now()
  ): Producto =
    Producto(
      id = id,
      sku = input.sku.trim.toUpperCase,
      nombre = input.nombre.trim,
      categoria = input.categoria.trim,
      linea = input.linea.trim,
      precio = input.precio,
      puntoReorden = input.puntoReorden,
      proveedorPrincipalId = input.proveedorPrincipalId,
      activo = input.activo,
      version = 1,
      createdAt = now,
      updatedAt = now
    )
