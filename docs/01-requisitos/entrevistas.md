# Entrevistas simuladas con stakeholders

> Fase 1 — Análisis. Entrevistas realizadas por el equipo al personal de
> TechSupply Solutions. Se documentan hallazgos y requisitos derivados.

## Metodología

- Entrevistas semiestructuradas de 45–60 minutos.
- Se registran: rol, objetivos, dolores, expectativas y requisitos derivados.
- Cada hallazgo se vincula a un requisito del SRS (`RF-*` / `RNF-*`).

---

## E-01 — Dirección General

**Entrevistado:** Lic. Marta Ruiz — Directora General
**Objetivo:** entender la visión estratégica y las decisiones que hoy no puede tomar.

**Hallazgos clave**

- “No sé cuánto vendí hoy ni cuánto stock real tengo; los reportes llegan cada
  quincena y ya están viejos.”
- Las tres sedes reportan en formatos distintos; consolidar es manual.
- Necesita ver **margen por línea de producto** y **rentabilidad por cliente**.
- Le preocupa el cumplimiento fiscal y la auditoría.

**Dolores**

- Falta de tablero unificado en tiempo real.
- Decisiones basadas en intuición por datos tardíos.

**Requisitos derivados:** RF-REP-01, RF-REP-02, RF-REP-03, RNF-01, RNF-05.

---

## E-02 — Gerencia de Compras

**Entrevistado:** Ing. Carlos Mendoza — Gerente de Compras
**Objetivo:** documentar el ciclo de abastecimiento.

**Hallazgos clave**

- Las requisiciones llegan por correo o papel y se pierden.
- No existe un catálogo único de proveedores ni historial de precios.
- Las órdenes se mecanografían en Word; el seguimiento es por llamadas.
- “Compro de más para no quedarme sin stock, y luego sobra inventario.”

**Dolores**

- Sin alertas de punto de reorden.
- Sin comparación de proveedores por precio/plazo/calidad.
- Recepción de bienes sin verificación formal contra la orden.

**Requisitos derivados:** RF-COM-01..RF-COM-06, RF-MAE-02, RF-INV-04.

---

## E-03 — Almacén y Logística

**Entrevistado:** Sr. Luis Paredes — Jefe de Almacén
**Objetivo:** entender el control físico del inventario.

**Hallazgos clave**

- Conteos manuales irregulares; los ajustes se hacen “de memoria”.
- Varios códigos para el mismo producto según el proveedor.
- No hay trazabilidad de entradas/salidas por ubicación.
- Se despachan pedidos sin validar stock real.

**Dolores**

- Diferencias entre sistema y conteo físico.
- Mercancía obsoleta sin alerta.
- Recepción y despacho lentos.

**Requisitos derivados:** RF-INV-01..RF-INV-06, RF-MAE-01, RF-MAE-05.

---

## E-04 — Gerencia Comercial y Ventas

**Entrevistada:** Lic. Ana Torres — Gerente Comercial
**Objetivo:** documentar el ciclo de ventas y crédito.

**Hallazgos clave**

- Pedidos por teléfono/correo; se confirma stock llamando al almacén.
- Las facturas se emiten en un sistema contable aparte.
- El crédito se controla con llamadas de cobranza; no hay límite visible.
- Los clientes piden un portal para autoservicio.

**Dolores**

- Ventas prometidas sin stock.
- Errores de precios y descuentos.
- Sin visibilidad del estado del pedido.

**Requisitos derivados:** RF-VEN-01..RF-VEN-06, RF-MAE-03, RF-REP-04.

---

## E-05 — Contabilidad y Finanzas

**Entrevistado:** C.P. Jorge Salinas — Contador General
**Objetivo:** documentar la integración contable.

**Hallazgos clave**

- La contabilidad está desacoplada del operativo; se registra después.
- La conciliación entre ventas, compras e inventario es manual.
- Reportes financieros quincenales; auditoría difícil por registros dispersos.
- Requiere asientos automáticos y control presupuestario.

**Dolores**

- Retrasos y errores de captura.
- Sin trazabilidad documento → asiento contable.
- Sin control de presupuesto por departamento.

**Requisitos derivados:** RF-CON-01..RF-CON-06.

---

## E-06 — Recursos Humanos

**Entrevistada:** Lic. Patricia Vega — Jefa de RRHH
**Objetivo:** entender las necesidades básicas de personal.

**Hallazgos clave**

- Nómina en Excel; se integra a contabilidad pegando totales a mano.
- Historial de empleados en carpetas físicas.
- Control de asistencia y vacaciones en hojas separadas.

**Dolores**

- Errores de nómina y retrabajo.
- Sin reporte de costo de personal por departamento.

**Requisitos derivados:** RF-RHH-01..RF-RHH-04.

---

## E-07 — TI / Infraestructura

**Entrevistado:** Ing. Diego Ramírez — Encargado de TI
**Objetivo:** conocer restricciones técnicas y de seguridad.

**Hallazgos clave**

- Conectividad variable en el almacén; la app debe tolerar latencia.
- Se exige autenticación robusta y roles diferenciados.
- Preferencia por soluciones administradas para no operar servidores.
- Necesidad de respaldo y auditoría de cambios.

**Requisitos derivados:** RNF-02, RNF-03, RNF-04, RF-SEG-01..RF-SEG-05.

---

## E-08 — Cliente externo (muestra)

**Entrevistado:** Sr. Pablo Ortiz — Distribuidora Andina (cliente)
**Objetivo:** validar expectativas del portal de clientes.

**Hallazgos clave**

- Quiere consultar catálogo, precios y disponibilidad.
- Quiere repetir pedidos y ver estado/envíos.
- Valora la factura digital y el historial de pagos.

**Requisitos derivados:** RF-VEN-01, RF-VEN-05, RF-VEN-06.

---

## Matriz de participación

| Stakeholder | Rol | Entrevista | Requisitos principales |
|---|---|---|---|
| Marta Ruiz | Dirección | E-01 | Reportería, indicadores |
| Carlos Mendoza | Compras | E-02 | Compras, proveedores |
| Luis Paredes | Almacén | E-03 | Inventario, almacenes |
| Ana Torres | Ventas | E-04 | Ventas, clientes, crédito |
| Jorge Salinas | Contabilidad | E-05 | Contabilidad, presupuesto |
| Patricia Vega | RRHH | E-06 | Nómina, empleados |
| Diego Ramírez | TI | E-07 | Seguridad, disponibilidad |
| Pablo Ortiz | Cliente | E-08 | Portal de clientes |
