# C-03 · Componente de Reportería

## Propósito

Generar reportes parametrizables y documentos PDF de forma uniforme. Separa la
**definición** del reporte (parámetros, columnas, formato) de su **generación**
(asíncrona para reportes pesados) y de la **visualización** (dashboards en Metabase).

## Responsabilidades

- Recibir una especificación de reporte validada.
- Ejecutar la consulta y aplicar filtros/agrupaciones.
- Generar PDF (JasperReports), XLSX o CSV.
- Gestionar trabajos asíncronos con estado y expiración.
- Registrar la generación en auditoría.

**Fuera de alcance:** diseño visual de dashboards (Metabase) y reglas de negocio.

## Contrato backend (Scala 3)

```scala
enum ReportFormat { case Pdf, Xlsx, Csv }
enum ReportStatus { case Pending, Running, Ready, Failed }
final case class ReportId(value: String)

final case class ReportSpec(
  reportId: ReportId,
  params: Map[String, String],
  format: ReportFormat
)

final case class ReportJob(id: UUID, status: ReportStatus, url: Option[String], error: Option[String])

trait ReportPort[F[_]]:
  def generate(spec: ReportSpec): F[ReportJob]
  def status(jobId: UUID): F[ReportJob]
  def download(jobId: UUID): F[ReportFile]
```

## Endpoints de la API

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/reportes` | Catálogo de reportes disponibles y parámetros. |
| POST | `/api/v1/reportes` | Crea un trabajo de generación. |
| GET | `/api/v1/reportes/{jobId}` | Consulta estado del trabajo. |
| GET | `/api/v1/reportes/{jobId}/archivo` | Descarga el archivo generado. |

## Catálogo inicial

| Reporte | Parámetros | Formato |
|---|---|---|
| Rotación de inventario | periodo, almacén | PDF/XLSX |
| Análisis ABC | periodo, categoría | XLSX |
| Ventas por cliente | desde, hasta, cliente | PDF/XLSX |
| Margen por línea | periodo | XLSX |
| Balance de prueba | periodo | PDF |
| Estado de resultados | periodo | PDF |
| Costo de personal | periodo, departamento | XLSX |

## Contrato frontend (Angular)

```ts
@Injectable({ providedIn: 'root' })
export class ReportService {
  catalog(): Observable<ReportDefinition[]>;
  generate(spec: ReportSpec): Observable<ReportJob>;
  status(jobId: string): Observable<ReportJob>;
  download(jobId: string): Observable<Blob>;
}
```

Componentes compartidos: `ReportButtonComponent`, `ReportParamsDialogComponent`,
`ReportJobStatusComponent`.

## Contextos de reutilización

| Contexto | Reporte |
|---|---|
| Compras | OC por proveedor y cumplimiento de entrega. |
| Inventario | Rotación y clasificación ABC. |
| Ventas | Ventas por cliente y margen por línea. |
| Contabilidad | Balance de prueba y estado de resultados. |
| RRHH | Costo de personal por departamento. |
| Gerencia | Reportes ejecutivos consolidados. |

## Diseño de ejecución

- Reportes ligeros: respuesta síncrona (< 5 s).
- Reportes pesados: **cola de trabajos** (tabla `report_jobs` + worker).
- Archivos en Supabase Storage con URL firmada y expiración.
- Límite de generaciones concurrentes por usuario.

## Pruebas

- Unitarias: validación de parámetros y armado de `ReportSpec`.
- Integración: generación de PDF/XLSX con datos de prueba.
- Rendimiento: reporte pesado dentro de RNF-03 (< 5 s para consultas, asíncrono para PDF).

## Trazabilidad

RF-REP-01..04 · CU-14 · RNF-03.
