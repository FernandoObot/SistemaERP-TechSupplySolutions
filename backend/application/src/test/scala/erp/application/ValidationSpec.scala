package erp.application

import cats.syntax.all.*
import erp.application.validation.ProductoValidator
import erp.domain.maestra.ProductoInput

class ValidationSpec extends munit.FunSuite:

  private def input(
      sku: String = "CAB-001",
      nombre: String = "Cable HDMI",
      categoria: String = "Cables",
      linea: String = "Accesorios",
      precio: BigDecimal = BigDecimal(149.90),
      puntoReorden: Int = 10
  ): ProductoInput =
    ProductoInput(sku, nombre, categoria, linea, precio, puntoReorden, activo = true)

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
