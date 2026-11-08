# Especificación de Requisitos de Software (SRS)

**Proyecto:** Sistema ERP — TechSupply Solutions
**Versión:** 1.0 (Fase 1)
**Estándar de referencia:** IEEE 830 (adaptado)

## 1. Introducción

### 1.1 Propósito

Definir los requisitos funcionales y no funcionales del ERP que integrará compras,
inventario, ventas, contabilidad, recursos humanos, reportería y seguridad de
TechSupply Solutions, sirviendo de base para el diseño arquitectónico y las pruebas.

### 1.2 Alcance

El sistema cubrirá los cinco procesos clave y siete módulos funcionales descritos en
la narrativa del proyecto. La Fase 1 entrega análisis y arquitectura; la implementación
se realiza por sprints en la Fase 3.

### 1.3 Definiciones

| Término | Definición |
|---|---|
| SKU | Identificador único de producto. |
| OC / OV | Orden de compra / orden de venta. |
| RLS | Row Level Security de PostgreSQL. |
| JWT | Token de sesión emitido por Supabase Auth. |
| AS-IS / TO-BE | Proceso actual / proceso deseado. |
| MoSCoW | Prioridad: Must, Should, Could, Won't. |

### 1.4 Referencias

- `docs/01-requisitos/entrevistas.md`
- `docs/01-requisitos/procesos.md`
- `docs/02-arquitectura/sad.md`
- Narrativa del proyecto y rúbrica de evaluación.

## 2. Actores

| Actor | Descripción |
|---|---|
| Administrador | Configura usuarios, roles, catálogos y parámetros. |
| Gerente / Dirección | Consulta dashboards y reportes estratégicos. |
| Comprador | Gestiona proveedores, requisiciones y órdenes de compra. |
| Almacenista | Registra recepciones, movimientos, conteos y despachos. |
| Vendedor | Gestiona clientes, cotizaciones, órdenes y crédito. |
| Contador | Administra plan de cuentas, asientos y estados financieros. |
| Analista de RRHH | Gestiona empleados, nómina, asistencia y vacaciones. |
| Cliente | Consulta catálogo, pedidos, facturas y pagos (portal). |
| Sistema | Ejecuta procesos automáticos (alertas, asientos, notificaciones). |

## 3. Requisitos funcionales

Prioridad: **M** = Must, **S** = Should, **C** = Could.

### 3.1 Módulo Gestión Maestra (MAE)

| ID | Requisito | Prioridad | Criterio de aceptación |
|---|---|---|---|
| RF-MAE-01 | CRUD de productos con SKU único, categoría, línea, precio y proveedor principal. | M | No se permiten SKU duplicados; validación en API y BD. |
| RF-MAE-02 | CRUD de proveedores con contacto, condiciones de pago y evaluación. | M | Proveedor inactivo no puede usarse en nuevas OC. |
| RF-MAE-03 | CRUD de clientes con límite de crédito y zona de entrega. | M | Cliente bloqueado no puede generar OV. |
| RF-MAE-04 | CRUD de empleados con puesto, departamento y salario. | M | Salario y departamento obligatorios. |
| RF-MAE-05 | CRUD de almacenes con código, ubicación y capacidad. | M | Código de almacén único. |
| RF-MAE-06 | Catálogo de rutas de entrega y transportistas. | S | Ruta asignable a una OV. |

### 3.2 Módulo Compras (COM)

| ID | Requisito | Prioridad | Criterio de aceptación |
|---|---|---|---|
| RF-COM-01 | Generar requisición automática al bajar del punto de reorden. | M | Se crea una requisición por producto bajo umbral. |
| RF-COM-02 | Registrar requisiciones manuales con aprobación. | M | Requisición sin aprobar no genera OC. |
| RF-COM-03 | Emitir OC con folio único, proveedor, partidas y totales. | M | Total = suma de partidas + impuestos. |
| RF-COM-04 | Consultar historial de precios y evaluación de proveedores. | S | Se muestra el último precio y promedio. |
| RF-COM-05 | Registrar recepción de bienes contra OC. | M | No se recibe más de lo ordenado sin autorización. |
| RF-COM-06 | Generar cuentas por pagar al recibir la factura del proveedor. | M | Se crea obligación contable asociada a la OC. |

### 3.3 Módulo Inventario (INV)

| ID | Requisito | Prioridad | Criterio de aceptación |
|---|---|---|---|
| RF-INV-01 | Registrar entradas y salidas con tipo, cantidad, usuario y fecha. | M | Todo movimiento actualiza el stock atómicamente. |
| RF-INV-02 | Impedir salidas que dejen stock negativo. | M | La API rechaza la operación con error de negocio. |
| RF-INV-03 | Alertas de stock mínimo, máximo y obsolescencia. | M | Alerta generada al cruzar el umbral. |
| RF-INV-04 | Conteos cíclicos con ajuste auditado. | S | El ajuste requiere motivo y usuario. |
| RF-INV-05 | Reporte de rotación y clasificación ABC. | S | Cálculo por periodo seleccionable. |
| RF-INV-06 | Soporte de códigos de barras en recepción/despacho. | C | Lectura asociada a SKU y movimiento. |

### 3.4 Módulo Ventas (VEN)

| ID | Requisito | Prioridad | Criterio de aceptación |
|---|---|---|---|
| RF-VEN-01 | Portal de clientes con catálogo y disponibilidad en tiempo real. | M | Disponibilidad refleja stock reservado. |
| RF-VEN-02 | Generar cotizaciones con vigencia y conversión a OV. | M | Cotización vencida no se convierte. |
| RF-VEN-03 | Validar límite de crédito antes de confirmar la OV. | M | OV bloqueada si excede el crédito disponible. |
| RF-VEN-04 | Calcular impuestos, descuentos y comisiones. | M | Cálculo determinístico y auditable. |
| RF-VEN-05 | Emitir factura y cuenta por cobrar desde la OV. | M | Factura referencia la OV de origen. |
| RF-VEN-06 | Consultar estado de pedido y envío. | S | Estados: confirmado, despachado, entregado. |

### 3.5 Módulo Contabilidad (CON)

| ID | Requisito | Prioridad | Criterio de aceptación |
|---|---|---|---|
| RF-CON-01 | CRUD de plan de cuentas jerárquico. | M | Cuenta hija hereda naturaleza del padre. |
| RF-CON-02 | Generar asientos automáticos desde transacciones operativas. | M | Cada asiento cuadra (debe = haber). |
| RF-CON-03 | Consultar mayor general y balance de prueba. | M | Saldos calculados en tiempo real. |
| RF-CON-04 | Generar estados financieros (resultados, balance, flujo). | S | Reporte por periodo contable. |
| RF-CON-05 | Control presupuestario por departamento con alertas. | S | Alerta al superar el presupuesto asignado. |
| RF-CON-06 | Registrar auditoría de cambios con valores anteriores/nuevos. | M | Toda modificación queda trazada. |

### 3.6 Módulo Recursos Humanos (RHH)

| ID | Requisito | Prioridad | Criterio de aceptación |
|---|---|---|---|
| RF-RHH-01 | Expediente digital de empleados. | M | Datos personales, contrato y puesto completos. |
| RF-RHH-02 | Cálculo de nómina y registro contable automático. | M | Nómina genera asiento y totales por departamento. |
| RF-RHH-03 | Control de asistencia y vacaciones con aprobación. | S | Solicitud aprobada descuenta días disponibles. |
| RF-RHH-04 | Reporte de costo de personal por departamento. | S | Totales conciliados con contabilidad. |

### 3.7 Módulo Reportería (REP)

| ID | Requisito | Prioridad | Criterio de aceptación |
|---|---|---|---|
| RF-REP-01 | Dashboards gerenciales en tiempo real. | M | Indicadores con antigüedad < 5 min. |
| RF-REP-02 | Reportes parametrizables (fecha, sede, producto, cliente). | M | Filtros aplicados en servidor. |
| RF-REP-03 | Análisis de rentabilidad por cliente y margen por línea. | S | Cálculo reproducible. |
| RF-REP-04 | Exportación de reportes a PDF y Excel. | S | Archivo generado con datos del periodo. |

### 3.8 Módulo Seguridad (SEG)

| ID | Requisito | Prioridad | Criterio de aceptación |
|---|---|---|---|
| RF-SEG-01 | Autenticación con Supabase Auth y sesión JWT. | M | Token expirado redirige al login. |
| RF-SEG-02 | Autorización basada en roles (RBAC). | M | Acceso denegado devuelve 403. |
| RF-SEG-03 | Auditoría de accesos y cambios. | M | Registro con usuario, acción y fecha. |
| RF-SEG-04 | Gestión de usuarios y asignación de roles. | M | Solo Administrador puede asignar roles. |
| RF-SEG-05 | Protección de datos con RLS en PostgreSQL. | M | Un usuario no accede a datos fuera de su alcance. |

## 4. Requisitos no funcionales

| ID | Categoría | Requisito | Métrica |
|---|---|---|---|
| RNF-01 | Disponibilidad | Uptime del servicio. | ≥ 99.5 % (≤ 1 h/mes de mantenimiento). |
| RNF-02 | Rendimiento | Consultas transaccionales. | < 2 s (p95). |
| RNF-03 | Rendimiento | Generación de reportes. | < 5 s (p95). |
| RNF-04 | Escalabilidad | Usuarios concurrentes. | ≥ 200 simultáneos. |
| RNF-05 | Seguridad | Cifrado en tránsito y reposo. | TLS 1.2+ y cifrado de Supabase. |
| RNF-06 | Mantenibilidad | Cobertura de pruebas y documentación. | ≥ 70 % cobertura; API documentada. |
| RNF-07 | Usabilidad | Curva de aprendizaje del personal. | ≤ 2 h de capacitación por módulo. |
| RNF-08 | Compatibilidad | Navegadores soportados. | Últimas 2 versiones de Chrome, Firefox, Edge. |

## 5. Restricciones y supuestos

- El frontend es Angular + TypeScript; el backend, Scala funcional.
- La base de datos y autenticación son Supabase (PostgreSQL).
- La API expone REST documentado con OpenAPI 3.1.
- El equipo es académico y el tiempo total es de un semestre.
- Se asume conectividad a Internet; el almacén puede tener latencia variable.

## 6. Fuera de alcance (Fase 1)

- Aplicación móvil nativa.
- Facturación electrónica certificada ante la autoridad fiscal (solo se modela).
- Integración real con transportistas (se modela la interfaz).
- Contabilidad multiempresa y multimoneda.

## 7. Matriz de trazabilidad (requisito → caso de uso)

| Requisito | Caso de uso |
|---|---|
| RF-MAE-01..06 | CU-01, CU-02 |
| RF-COM-01..06 | CU-03, CU-04, CU-05 |
| RF-INV-01..06 | CU-06, CU-07 |
| RF-VEN-01..06 | CU-08, CU-09, CU-10 |
| RF-CON-01..06 | CU-11, CU-12 |
| RF-RHH-01..04 | CU-13 |
| RF-REP-01..04 | CU-14 |
| RF-SEG-01..05 | CU-00 |

## 8. Criterios de aceptación globales

1. Los cinco procesos operan de extremo a extremo sin captura manual duplicada.
2. Toda transacción es auditable y trazable al documento fuente.
3. La base de datos está al menos en 3FN con integridad referencial.
4. La API responde con errores consistentes y validaciones en backend.
5. Los reportes gerenciales se generan con datos vigentes.
