# Procesos de negocio AS-IS / TO-BE

Cinco procesos clave de TechSupply Solutions. Para cada uno se documenta el estado
actual (AS-IS), sus problemas y el flujo deseado con el ERP (TO-BE).

---

## P-01 · Gestión de Compras

### AS-IS

1. El área solicitante envía la requisición por correo o papel.
2. Compras busca proveedores en archivos y hojas de cálculo.
3. Se cotiza por teléfono/correo y se elige “a ojo”.
4. La orden de compra se mecanografía en Word.
5. El seguimiento se hace por llamadas; no hay estado centralizado.
6. Al recibir, almacén firma una papeleta; contabilidad registra la factura después.

**Problemas:** requisiciones perdidas, sin historial de precios, sin comparación de
proveedores, recepción sin verificación, cuentas por pagar tardías.

### TO-BE

1. El sistema detecta stock bajo el punto de reorden y **genera una requisición**.
2. Compras revisa el **catálogo de proveedores** (precio, plazo, evaluación).
3. Se emite la **orden de compra** con folio y estado (`borrador → enviada → recibida`).
4. Al llegar la mercancía, almacén registra la **recepción** contra la orden
   (cantidad, calidad, precio) y el sistema calcula diferencias.
5. Inventario se actualiza automáticamente y se genera la **cuenta por pagar**.
6. Trazabilidad completa: requisición → OC → recepción → asiento contable.

**KPIs:** tiempo de ciclo de compra, % de OC con retraso, diferencias en recepción.

---

## P-02 · Gestión de Inventario

### AS-IS

1. Conteos manuales esporádicos en hojas de cálculo.
2. Cada proveedor usa su propio código; el mismo producto tiene varios códigos.
3. Las salidas se anotan en papeleta y se capturan después.
4. No hay alertas de stock ni de obsolescencia.
5. Almacén y ventas no comparten una fuente única de verdad.

**Problemas:** desajustes, duplicidad de productos, sin trazabilidad, sobre-stock y
faltantes simultáneos.

### TO-BE

1. **Catálogo único** con SKU, categoría, línea, proveedor y ubicación.
2. Cada entrada/salida genera un **movimiento** con tipo, cantidad, usuario y fecha.
3. **Alertas** de stock mínimo, stock máximo y productos sin rotación.
4. **Conteos cíclicos** con ajuste auditado.
5. Reportes de **rotación** y **clasificación ABC**.
6. Integración con códigos de barras en recepción/despacho.

**KPIs:** exactitud de inventario, rotación, % de productos obsoletos.

---

## P-03 · Gestión de Ventas y Pedidos

### AS-IS

1. El cliente pide por teléfono/correo.
2. El vendedor llama al almacén para confirmar stock.
3. Se cotiza y se confirma de forma manual.
4. La factura se emite en el sistema contable independiente.
5. El crédito se controla por llamadas de cobranza.
6. El envío se coordina por teléfono con el transportista.

**Problemas:** ventas sin stock, precios/descuentos inconsistentes, sin portal,
facturación desacoplada, sin visibilidad del envío.

### TO-BE

1. El cliente consulta **catálogo y disponibilidad en tiempo real** (portal).
2. Se genera **cotización** y, al aprobarse, **orden de venta**.
3. El sistema **reserva stock** y valida **límite de crédito**.
4. Se calculan impuestos, descuentos y comisiones automáticamente.
5. Se emite **factura** y se registra la **cuenta por cobrar**.
6. Se genera la **remisión** y se integra el seguimiento del transportista.
7. El cliente consulta estado, facturas y pagos en el portal.

**KPIs:** tiempo de cotización a factura, % de pedidos rechazados por stock/crédito,
días de cobranza.

---

## P-04 · Gestión Contable y Financiera

### AS-IS

1. Contabilidad recibe documentos físicos/digitales dispersos.
2. Captura manual de asientos días después de la operación.
3. Conciliación manual entre ventas, compras e inventario.
4. Reportes financieros quincenales en Excel.
5. Sin control presupuestario por departamento.

**Problemas:** retraso, errores, auditoría difícil, sin visión en tiempo real.

### TO-BE

1. Cada transacción operativa **genera asientos automáticos** según reglas contables.
2. **Mayor general** y **balance de prueba** disponibles en tiempo real.
3. **Estados financieros** (resultados, balance, flujo de caja) parametrizables.
4. **Control presupuestario** con alertas al exceder partidas.
5. **Auditoría** con trazabilidad documento fuente → asiento.
6. Reportes fiscales (IVA, retenciones) automatizados.

**KPIs:** tiempo de cierre contable, % de asientos automáticos, diferencias de conciliación.

---

## P-05 · Gestión de Recursos Humanos (simplificado)

### AS-IS

1. Nómina en Excel; totales copiados a contabilidad.
2. Expedientes en carpetas físicas.
3. Asistencia y vacaciones en hojas separadas.

**Problemas:** errores de cálculo, retrabajo, sin costos por departamento.

### TO-BE

1. **Expediente digital** del empleado (datos, contrato, puesto, salario).
2. **Nómina** calculada y registrada automáticamente en contabilidad.
3. **Asistencia y vacaciones** con aprobaciones.
4. **Reporte de costo de personal** por departamento.

**KPIs:** exactitud de nómina, tiempo de procesamiento, ausentismo.

---

## Resumen de mejoras

| Proceso | Dolor principal | Resultado esperado |
|---|---|---|
| Compras | Requisiciones perdidas | Automatización y trazabilidad |
| Inventario | Desajustes de stock | Fuente única y alertas |
| Ventas | Venta sin stock/crédito | Validación en tiempo real |
| Contabilidad | Registro tardío | Asientos automáticos |
| RRHH | Nómina manual | Integración con contabilidad |
