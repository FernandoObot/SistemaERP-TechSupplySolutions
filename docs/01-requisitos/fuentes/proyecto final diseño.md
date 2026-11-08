Narrativa de Proyecto: Sistema ERP para Distribuidora "TechSupply Solutions"

Contexto y Justificación del Proyecto

TechSupply Solutions es una empresa mediana de distribución de componentes

electrónicos y suministros de TI ubicada en la región latinoamericana. Fundada hace

12 años, ha crecido de manera orgánica, pasando de un pequeño equipo de 5

personas a más de 80 colaboradores distribuidos en tres sedes principales: oficina

administrativa central, almacén logístico y oficina comercial.

A pesar de su crecimiento, la empresa enfrenta desafíos críticos operacionales:

Fragmentación de sistemas: Utiliza soluciones dispares (hojas de cálculo en

Excel, software contable heredado incompatible, sistema de inventario basad

o en papeleta manual)

Pérdida de información: Errores frecuentes en la captura de datos, duplicació

n de registros, inconsistencia entre departamentos

Ineficiencia administrativa: Procesos manuales tediosos que generan cuello

s de botella, especialmente en gestión de compras, ventas e inventario

- Falta de visibilidad: La gerencia no tiene reportes unificados en tiempo real p ara tomar decisiones estratégicas

Conflictos interdepartamentales: Cada área opera independientemente sin i

ntegración, causando desajustes entre ventas, inventario y finanzas

La dirección ha decidido implementar un Sistema de Gestión Empresarial (ERP) que

integre todas las operaciones y proporcione una base sólida para el crecimiento

futuro.

Objetivos del Proyecto

Objetivos Estratégicos

Objetivo

Descripción

Conectar todos los procesos de negocio (compras, ventas, inventario, contabilidad, recursos humanos) en una única plataforma

Integración operacional


## Objetivo Descripción

Eliminar tareas manuales repetitivas y reducir errores humanos en un 85%

Automatización de procesos

Garantizar que cada transacción sea registrada, verificable y auditable

Trazabilidad de información

Proporcionar reportes y dashboards en tiempo real para la toma de decisiones

Inteligencia de negocio

Crear una arquitectura extensible que permita agregar funcionalidades futuras sin afectar el núcleo

Escalabilidad tecnológica

## Objetivos Técnicos

- Desarrollar utilizando el modelo de 4 + 1 vistas para garantizar claridad arquitectónica

- Implementar componentes reutilizables que reduzcan tiempo de desarrollo y faciliten mantenimiento

- Asegurar separación de responsabilidades entre capas de presentación, lógica de negocio y datos

- Crear una base de datos relacional normalizada que garantice integridad refer encial

## Descripción de Procesos Empresariales de TechSupply Solutions

## 1. Gestión de Compras

## Proceso actual (problemático):

- Solicitudes de compra generadas en correo electrónico o papel

- Gerente de compras verifica proveedores mediante búsquedas manuales en a rchivos

- Órdenes de compra mecanografiadas en documentos Word

- Seguimiento mediante llamadas telefónicas y correos

## Proceso deseado con ERP:


- Sistema automático de requisición cuando el inventario baja de umbral mínim o

- Catalogo centralizado de proveedores con histórico de precios y rendimiento

- Generación automática de órdenes de compra con trazabilidad completa

- Recepción de bienes verificada contra orden: cantidad, calidad y precio

- Integración automática con contabilidad (cuentas por pagar)

## 2. Gestión de Inventario

## Problemas actuales:

- Conteos manuales irregulares causando desajustes frecuentes

- Múltiples sistemas de codificación, generando confusión

- No hay trazabilidad de movimientos (entradas, salidas, ajustes)

- Falta de categorización de productos por línea, proveedor o rentabilidad

## Solución esperada:

- Inventario centralizado con código único por producto (SKU)

- Seguimiento automático de entradas/salidas en tiempo real

- Alertas de stock bajo y stock obsoleto

- Categorización multinivel: línea de producto, proveedor, margen de ganancia

- Reportes de rotación de inventario y análisis ABC

- Integración con almacén físico mediante códigos de barras

## 3. Gestión de Ventas y Pedidos

## Estado actual:

- Pedidos capturados manualmente por teléfono o correo

- Verificación manual de disponibilidad en inventario (frecuentemente incorrect a)

- Facturas generadas en sistema contable independiente

- Seguimiento de crédito a clientes basado en llamadas de cobranza

## Mejora esperada:


- Portal de clientes para realizar pedidos en línea

- Verificación automática de disponibilidad y precios en tiempo real

- Generación automática de documentos (orden de venta, remisión, factura)

- Cálculo automático de impuestos, descuentos y comisiones

- Historial completo de cliente: contacto, pedidos, pagos, crédito disponible

- Integración con transportista para seguimiento de envíos

## 4. Gestión Contable y Financiera

## Limitaciones actuales:

- Contabilidad desacoplada del operativo, genera retrasos

- Conciliación manual y tediosa entre ventas, compras e inventario

- Reportes financieros atrasados (generados quincenalmente)

- Falta de control presupuestario por departamento

- Auditoría difícil debido a fragmentación de registros

## Visión con ERP:

- Asientos contables automáticos a partir de transacciones operativas

- Balance de prueba disponible en tiempo real

- Reportes financieros generados automáticamente (Estados de Resultados, Bal ance, Flujo de Caja)

- Control presupuestario integrado con compras y gastos

- Auditoría completa: trazabilidad desde el documento fuente hasta el asiento c ontable

- Cumplimiento fiscal: reportes SAT, IVA y retenciones automáticos

## 5. Gestión de Recursos Humanos (Simplificado)

## Necesidades básicas:

- Nómina automática integrada con la contabilidad

- Historial de empleados: datos personales, contrato, posición, salario

- Control de asistencia y vacaciones


- Reportes de costos de personal por departamento

## Requerimientos Funcionales del Sistema ERP

## Módulos Principales

Módulo

Catálogo de productos, Proveedores, Clientes, Empleados, Almacenes, Rutas de entrega

Gestión

Maestra

Requisiciones, Órdenes de compra, Recepción de bienes, Devoluciones, Control de proveedores

Compras

Entrada/salida de bienes, Ajustes, Conteos cíclicos, Alertas de stock, Clasificación ABC

Inventario

Cotizaciones, Órdenes de venta, Facturación, Gestión de crédito, Seguimiento de pedidos

Ventas

Contabilidad Plan de cuentas, Asientos contables, Mayor general, Balance de

prueba, Estados financieros

Dashboards, Reportes parametrizables, Análisis ABC, Rentabilidad por cliente, Margen operativo

Reportería

Control de acceso basado en roles, Auditoría de cambios, Gestión de usuarios

Seguridad

Funcionalidades Clave

## Requerimientos No Funcionales

- Disponibilidad: 99.5% uptime (máximo 1 hora de mantenimiento mensual)

- Rendimiento: Consultas < 2 segundos, reportes < 5 segundos

- Seguridad: Autenticación robusta, encriptación de datos en tránsito y reposo, cumplimiento de regulaciones fiscales

- Escalabilidad: Soportar crecimiento a 200 usuarios simultáneos


- Mantenibilidad: Código documentado, componentes reutilizables, fácil exten sión

## Alcance del Proyecto Académico

Fase 1: Análisis y Diseño Arquitectónico (Semanas 1-3) a más tardar 8 de octubre.

Los estudiantes deben:

- Realizar entrevistas simuladas con stakeholders de TechSupply Solutions

- Documentar procesos actuales y flujos deseados

- Diseñar la arquitectura usando el modelo 4 + 1 vistas:

- Vista Lógica: Clases, paquetes, componentes del dominio

- Vista de Procesos: Flujos de interacción, sincronización

- Vista de Implementación: Componentes físicos, módulos de código

- Vista de Despliegue: Servidores, bases de datos, infraestructura

- Casos de Uso: Escenarios que unifican las cuatro vistas

## Fase 2: Identificación de Componentes Reutilizables (Semana 4) 15 de octubre.

Diseñar componentes que puedan utilizarse en múltiples módulos:

- Componente de Autenticación: Login, gestión de sesiones

- Componente de Búsqueda Avanzada: Filtrado, paginación, ordenamiento

- Componente de Reportería: Generador de reportes parametrizables

- Componente de Auditoría: Registro de cambios y trazabilidad

- Componente de Notificaciones: Alertas por email, SMS, en-app

- Componente de Validación: Reglas de negocio centralizadas

- Componente CRUD: Operaciones básicas (Create, Read, Update, Delete)

## Fase 3: Desarrollo Incremental (Semanas 5-12) 17 de diciembre.

Implementar por sprints de 2 semanas, priorizando:

Sprint 1-2: Gestión Maestra + Compras


Módulo de Proveedores

Módulo de Productos

Órdenes de Compra

Recepción de bienes

## Sprint 3-4: Inventario

Registro de movimientos

Ajustes y conteos

Alertas de stock

Reportes de rotación

## Sprint 5-6: Ventas

Clientes y cotizaciones

Órdenes de venta

Facturación automática

Gestión de crédito

## Sprint 7-8: Contabilidad

Plan de cuentas

Asientos automáticos

Mayor general

Estados financieros

## Sprint 9-10: Reportería y Dashboards

Paneles gerenciales

Reportes ejecutivos

Análisis de rentabilidad

## Sprint 11-12: Integración y Testing

Pruebas end-to-end

Optimización de rendimiento


- Documentación final

## Fase 4: Pruebas y Validación (Última semana)

- Pruebas de casos de uso críticos

- Validación de integridad de datos

- Pruebas de carga

- Entrega de documentación completa

## Especificación Técnica

## Arquitectura General

│

CAPA DE PRESENTACIÓN

│

│ (Angular/React - Dashboard, Formularios, Reportes) │

│

│ CAPA DE SERVICIOS (API REST)

│

│ Autenticación, Autorización, Orquestación

│

│

│ CAPA DE LÓGICA DE NEGOCIO

│

│ Compras, Ventas, Inventario, Contabilidad

│

│ Componentes Reutilizables (Búsqueda, Reportes) │

│


│ CAPA DE ACCESO A DATOS (DAL) │ Repositorios, Transacciones, Cache

│

│

│

│ CAPA DE PERSISTENCIA

│

│ PostgreSQL / MySQL - Esquema Normalizado

│

## Stack Tecnológico Sugerido (no es obligatoria alguna de las mencionadas)

## Capa Tecnología

## Frontend Angular 16+ o React 18+ + TypeScript

Node.js + Express / Java + Spring Boot / Python + FastAPI

PostgreSQL 14+ (recomendado), MySQL 8+, SQL Server o

Firebird

## Backend

Base de Datos

## Autenticación JWT (JSON Web Tokens)

## Reportes JasperReports o ReportLab

## Control de Versiones Git + GitHub

## Documentación Swagger/OpenAPI

## Esquema de Base de Datos (Conceptual)

Las tablas principales incluirán:

## Maestros:


- productos: SKU, descripción, precio, categoría, proveedor principal

- proveedores: nombre, contacto, dirección, condiciones de pago

- clientes: nombre, contacto, dirección, límite de crédito, zona de entrega

- empleados: nombre, puesto, departamento, salario

- almacenes: código, ubicación, capacidad

## Operativas:

- ordenes_compra: número, proveedor, fecha, estado, total

- detalles_oc: orden_compra_id, producto_id, cantidad, precio_unitario

- movimientos_inventario: almacén, producto, tipo (entrada/salida), cantidad, fe cha

- ordenes_venta: número, cliente, fecha, estado, total

- detalles_ov: orden_venta_id, producto_id, cantidad, precio_unitario, descuent o

- facturas: número, orden_venta_id, fecha, monto, impuestos

- asientos_contables: número, cuenta, debe, haber, descripción, referencia

- pagos: cliente, factura, monto, fecha, medio

## Auditoría:

- auditoria: tabla_origen, registro_id, usuario, acción, fecha, valores_anteriores, valores_nuevos

## Criterios de Evaluación del Proyecto Arquitectura y Diseño (30%)

- Correcta aplicación del modelo 4 + 1 vistas

-  Claridad en la documentación arquitectónica

- ‑ Separación adecuada de responsabilidades

-  Diagramas UML completos y coherentes

## Componentes Reutilizables (20%)

- ‑ Identificación apropiada de componentes candidatos


- ‒ Implementación modular de al menos 5 componentes

-  Documentación de interfaces y contratos

-  Demostración de reutilización en múltiples contextos

## Implementación Funcional (30%)

- ‒ Cobertura de al menos 80% de los requerimientos

-  Flujos completos de negocio operativos

- Integridad de datos garantizada

-  Manejo de errores y validaciones robustas

## Calidad de Código (10%)

-  Código limpio, legible y bien comentado

- Aplicación de patrones de diseño (MVC, DAO, Factory)

- Pruebas unitarias (mínimo 70% de cobertura)

-  Control de versiones usado correctamente

## Documentación (10%)

-  Manual técnico: guía de instalación, configuración, despliegue

- ‑ Manual de usuario: guías de cada módulo con capturas

- ‑ Matriz de trazabilidad: requerimientos  código

-  Presentación ejecutiva del proyecto

## Entregables Finales

- 1. Código fuente (liga a GitHub con rama main estable y entregable final en Eminus)

- 2. Base de datos (scripts SQL de creación e inicialización)

- 1. Documentación Arquitectónica

- 1. Documento de especificación de requisitos (SRS)

- 2. Documento de arquitectura de software (SAD)conlas4+1vistas

- 3. Diagramas UML: casos de uso, clases, secuencia, componentes, despliegue


- 4. Matriz de componentes reutilizables y su alcance

## 2. Manual Técnico

- 1. Guía de instalación y configuración del ambiente de desarrollo

- 2. Instrucciones de despliegue en producción

- 3. Documentación API REST(Swagger/OpenAPI)

- 4. Guía de mantención y extensión del sistema

## 3. Manual de Usuario

- 1. Guías paso a paso por cada módulo

- 2. Capturas de pantalla anotadas

- 3. Procedimientos de casos de uso principales

- 4. Glosario de términos del negocio

## 4. Pruebas y Validación

- 1. Plan de pruebas y casos de prueba documentados

- 2. Reportes de ejecución de pruebas

- 3. Matriz de trazabilidad requisitos-pruebas

- 4. Resultados de pruebas de rendimiento y carga

## 5. Presentación Final

- 1. Demostración en vivo del sistema funcionando

- 2. Justificación de decisiones arquitectónicas

- 3. Lecciones aprendidas y recomendaciones futuras

Fecha de entrega: día del examen final, cada día de retraso es un punto menos, si bajan de 6 se entrega en extraordinario sobre el mismo criterio, sino se cumple se entrega en título y se da tiempo hasta el cierre de actas.

Deben demostrar que TODOS trabajaron.
