# C-06 · Componente de Validación

## Propósito

Centralizar las reglas de negocio y de formato para que se apliquen de forma
consistente en **backend (autoritativo)** y **frontend (feedback inmediato)**. Evita
validaciones duplicadas y divergentes.

## Responsabilidades

- Definir validadores composables y reutilizables.
- Acumular **todos** los errores (no detenerse en el primero).
- Distinguir error de formato (`400`) de regla de negocio (`422`).
- Compartir las mismas reglas con Angular.

## Contrato backend (Scala 3)

```scala
import cats.data.{Validated, ValidatedNec}

final case class ValidationError(code: String, field: String, message: String)

type Result[A] = ValidatedNec[ValidationError, A]

trait Validator[A]:
  def validate(value: A): Result[A]

object Validator:
  def combine[A](rules: (A => Result[A])*): Validator[A] = ...
  given Validator[ProductoInput] = ...
```

Reglas por tipo:

| Regla | Ejemplo |
|---|---|
| Requerido | `sku` no vacío |
| Formato | email válido, SKU `[A-Z0-9-]{3,20}` |
| Rango | cantidad > 0, precio ≥ 0 |
| Unicidad | SKU no repetido (consulta) |
| Referencial | proveedor/cliente activo |
| Negocio | stock ≥ cantidad, crédito disponible |
| Transición | OC `borrador → enviada` válida |

## Formato de error

```json
{
  "code": "VALIDACION",
  "message": "La solicitud contiene errores",
  "details": [
    { "field": "sku", "issue": "ya existe" },
    { "field": "precio", "issue": "debe ser >= 0" }
  ],
  "traceId": "3f6b1e9c"
}
```

- Formato/estructura → `400`.
- Regla de negocio → `422`.
- El frontend mapea `details[].field` a los controles del formulario.

## Contrato frontend (Angular)

```ts
export interface ValidationIssue { field: string; issue: string; }

@Injectable({ providedIn: 'root' })
export class ValidationService {
  applyServerErrors(form: FormGroup, issues: ValidationIssue[]): void;
}

// Validadores reutilizables
export const skuValidator: ValidatorFn;
export const positiveNumber: ValidatorFn;
export const creditLimit: ValidatorFn;
```

- Los formularios usan `ReactiveFormsModule` y los mismos mensajes.
- El backend **siempre** revalida; el frontend solo mejora la experiencia.

## Contextos de reutilización

| Contexto | Validaciones |
|---|---|
| Gestión maestra | SKU único, email, salario, capacidad. |
| Compras | Cantidad > 0, proveedor activo, recepción ≤ ordenado. |
| Inventario | Stock suficiente, motivo de ajuste obligatorio. |
| Ventas | Crédito disponible, vigencia de cotización. |
| Contabilidad | Debe = haber, periodo abierto, cuenta imputable. |
| RRHH | Fechas de contrato, días de vacaciones disponibles. |

## Pruebas

- Unitarias por regla y composición (`combine`).
- Propiedades: toda entrada inválida produce al menos un error; la válida pasa.
- Contrato: el frontend interpreta correctamente `details`.
- Diferencias: probar que backend y frontend aceptan/rechazan lo mismo.

## Trazabilidad

RF-SEG-01 · RNF-05 · Todos los casos de uso.
