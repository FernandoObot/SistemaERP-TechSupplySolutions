package erp.application

import cats.syntax.all.*
import erp.application.validation.ComprasValidator
import erp.application.validation.ProductoValidator
import erp.domain.{AlmacenId, ProductoId, ProveedorId, RecepcionId}
import erp.domain.compras.*
import erp.domain.maestra.ProductoInput

import java.util.UUID

class ValidationSpec extends munit.FunSuite:

  private def input(
      sku: String = "CAB-001",
      nombre: String = "Cable HDMI",
      categoria: String = "Cables",
      linea: String = "Accesorios",
      precio: BigDecimal = BigDecimal(149.90),
      puntoReorden: Int = 10
  ): ProductoInput =
    ProductoInput(sku, nombre, categoria, linea, precio, puntoReorden, None, activo = true)

  test("un producto válido pasa la validación") {
    assert(ProductoValidator.validate(input()).isValid)
  }

  test("el SKU vacío es inválido") {
    val result = ProductoValidator.validate(input(sku = "  "))
    assert(result.isInvalid)
    assertEquals(result.swap.toOption.map(_.toList.map(_.field)), Some(List("sku")))
  }

  test("el precio negativo es inválido") {
    val result = ProductoValidator.validate(input(precio = BigDecimal(-1)))
    assert(result.isInvalid)
  }

  test("se acumulan varios errores") {
    val result = ProductoValidator.validate(input(sku = "", nombre = "", precio = BigDecimal(-5)))
    assert(result.isInvalid)
    assertEquals(result.swap.toOption.map(_.toList.size), Some(3))
  }

class PageSpec extends munit.FunSuite:

  test("totalPages calcula el número de páginas") {
    assertEquals(Page(List(1, 2, 3), page = 0, size = 2, total = 5L).totalPages, 3)
  }

  test("map transforma los elementos y conserva metadatos") {
    val page = Page(List(1, 2), page = 1, size = 2, total = 4L).map(_ * 10)
    assertEquals(page.items, List(10, 20))
    assertEquals(page.total, 4L)
  }

class ComprasSpec extends munit.FunSuite:

  private val productoId = ProductoId(UUID.randomUUID())
  private val proveedorId = ProveedorId(UUID.randomUUID())

  test("el total de la orden suma cantidad por precio") {
    val detalles = List(
      DetalleOCInput(productoId, 2, BigDecimal(10.50)),
      DetalleOCInput(productoId, 3, BigDecimal(4.00))
    )
    assertEquals(OrdenCompra.total(detalles), BigDecimal(33.00))
  }

  test("una orden sin líneas es inválida") {
    val result = ComprasValidator.validarOrden(OrdenCompraInput(proveedorId, None, Nil))
    assert(result.isInvalid)
  }

  test("una línea con cantidad cero es inválida") {
    val result = ComprasValidator.validarOrden(
      OrdenCompraInput(proveedorId, None, List(DetalleOCInput(productoId, 0, BigDecimal(10))))
    )
    assert(result.isInvalid)
  }

  test("una recepción sin líneas es inválida") {
    val result = ComprasValidator.validarRecepcion(
      RecepcionInput(AlmacenId(UUID.randomUUID()), None, Nil)
    )
    assert(result.isInvalid)
  }
