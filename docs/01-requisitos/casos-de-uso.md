# Casos de uso

**Fase 1 — Vista de Casos de Uso (unifica las 4+1 vistas).**
Diagrama fuente: `docs/03-diagramas/plantuml/casos-de-uso.puml`.

## 1. Actores

| Actor | Tipo | Descripción |
|---|---|---|
| Administrador | Primario | Configura usuarios, roles y catálogos. |
| Gerente | Primario | Consulta indicadores y reportes. |
| Comprador | Primario | Gestiona compras y proveedores. |
| Almacenista | Primario | Gestiona inventario y recepciones. |
| Vendedor | Primario | Gestiona ventas y crédito. |
| Contador | Primario | Gestiona contabilidad. |
| Analista RRHH | Primario | Gestiona personal y nómina. |
| Cliente | Primario | Consulta catálogo y pedidos (portal). |
| Supabase Auth | Secundario | Autenticación y emisión de JWT. |
| Metabase | Secundario | Dashboards analíticos. |

## 2. Lista de casos de uso

| ID | Caso de uso | Actor principal | Módulo |
|---|---|---|---|
| CU-00 | Autenticarse y gestionar sesión | Todos | Seguridad |
| CU-01 | Gestionar catálogos maestros | Administrador | Maestra |
| CU-02 | Gestionar usuarios y roles | Administrador | Seguridad |
| CU-03 | Generar requisición de compra | Sistema / Comprador | Compras |
| CU-04 | Emitir orden de compra | Comprador | Compras |
| CU-05 | Recibir bienes contra OC | Almacenista | Compras/Inventario |
| CU-06 | Registrar movimiento de inventario | Almacenista | Inventario |
| CU-07 | Ejecutar conteo cíclico y ajuste | Almacenista | Inventario |
| CU-08 | Cotizar y generar orden de venta | Vendedor / Cliente | Ventas |
| CU-09 | Validar crédito y reservar stock | Sistema | Ventas/Inventario |
| CU-10 | Facturar y registrar cuenta por cobrar | Vendedor / Sistema | Ventas/Contabilidad |
| CU-11 | Generar asientos contables | Sistema / Contador | Contabilidad |
| CU-12 | Consultar estados financieros | Contador / Gerente | Contabilidad |
| CU-13 | Procesar nómina | Analista RRHH | RRHH |
| CU-14 | Consultar dashboard y reportes | Gerente | Reportería |

## 3. Especificaciones detalladas

### CU-00 · Autenticarse y gestionar sesión

- **Actores:** Usuario, Supabase Auth.
- **Precondición:** usuario registrado y activo.
- **Flujo principal:**
  1. El usuario ingresa credenciales en Angular.
  2. El frontend llama a Supabase Auth y obtiene el JWT.
  3. El frontend adjunta el JWT en cada llamada a la API Scala.
  4. La API valida firma/expiración con el JWKS y resuelve el rol.
  5. Se muestra el menú según permisos.
- **Alternativos:** credenciales inválidas → mensaje y bloqueo tras N intentos;
  token expirado → refresh o redirección al login.
- **Postcondición:** sesión activa y auditada.

### CU-03 · Generar requisición de compra

- **Actores:** Sistema, Comprador.
- **Precondición:** producto con punto de reorden configurado.
- **Flujo principal:**
  1. Un job o evento detecta stock ≤ punto de reorden.
  2. Se crea la requisición en estado `pendiente` con cantidad sugerida.
  3. El Comprador revisa y aprueba o ajusta.
  4. La requisición aprobada queda disponible para generar OC.
- **Alternativos:** el Comprador rechaza → estado `cancelada` con motivo.
- **Postcondición:** requisición trazable y auditable.

### CU-06 · Registrar movimiento de inventario

- **Actores:** Almacenista.
- **Precondición:** producto y almacén existentes.
- **Flujo principal:**
  1. El Almacenista elige tipo (entrada/salida/ajuste).
  2. Ingresa producto, cantidad y referencia (OC, OV, ajuste).
  3. La API valida que no quede stock negativo.
  4. Se registra el movimiento y se actualiza el stock en una transacción.
  5. Se evalúan alertas de stock y se notifica si aplica.
- **Alternativos:** stock insuficiente → error de negocio, no se persiste nada.
- **Postcondición:** movimiento auditado y stock consistente.

### CU-08 · Cotizar y generar orden de venta

- **Actores:** Vendedor, Cliente.
- **Precondición:** cliente activo y productos con precio vigente.
- **Flujo principal:**
  1. Se seleccionan productos y cantidades.
  2. El sistema verifica disponibilidad y calcula impuestos/descuentos.
  3. Se emite la cotización con vigencia.
  4. Al aprobar el cliente, se convierte en OV.
  5. Se reserva stock y se valida el crédito (CU-09).
- **Alternativos:** cotización vencida → no se convierte; crédito excedido → OV
  bloqueada con autorización pendiente.
- **Postcondición:** OV confirmada o pendiente de autorización.

### CU-10 · Facturar y registrar cuenta por cobrar

- **Actores:** Vendedor, Sistema.
- **Precondición:** OV confirmada y stock reservado/despachado.
- **Flujo principal:**
  1. Se genera la factura con folio y datos fiscales.
  2. Se calculan impuestos y totales.
  3. Se registra la cuenta por cobrar.
  4. Se dispara el asiento contable (CU-11).
  5. El cliente puede consultar la factura en el portal.
- **Postcondición:** factura emitida y contabilidad actualizada.

### CU-11 · Generar asientos contables

- **Actores:** Sistema, Contador.
- **Precondición:** transacción operativa registrada.
- **Flujo principal:**
  1. El sistema aplica las reglas de mapeo (evento → cuentas).
  2. Construye el asiento con debe = haber.
  3. Valida periodo contable abierto.
  4. Persiste el asiento con referencia al documento fuente.
- **Alternativos:** periodo cerrado → el asiento queda en cola para revisión.
- **Postcondición:** asiento auditable y balance consistente.

### CU-14 · Consultar dashboard y reportes

- **Actores:** Gerente.
- **Precondición:** sesión con rol de consulta.
- **Flujo principal:**
  1. El Gerente abre el dashboard.
  2. La API/consultas agregadas devuelven indicadores vigentes.
  3. Aplica filtros (fecha, sede, línea, cliente).
  4. Exporta a PDF/Excel si lo requiere.
- **Postcondición:** información para toma de decisiones, sin exposición de datos
  fuera del alcance del rol.

## 4. Relaciones

- CU-08 **incluye** CU-09 (validación de crédito y reserva).
- CU-10 **incluye** CU-11 (asiento automático).
- CU-05 **extiende** CU-06 (la recepción genera una entrada de inventario).
- CU-12 **extiende** CU-14 (reportes financieros específicos).
