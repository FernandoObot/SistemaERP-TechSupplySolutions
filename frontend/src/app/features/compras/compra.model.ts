export interface DetalleOC {
  id: string;
  productoId: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface OrdenCompra {
  id: string;
  folio: string;
  proveedorId: string;
  fecha: string;
  estado: string;
  total: number;
  observaciones: string | null;
  version: number;
  detalles: DetalleOC[];
}

export interface DetalleOCInput {
  productoId: string;
  cantidad: number;
  precioUnitario: number;
}

export interface OrdenCompraInput {
  proveedorId: string;
  observaciones: string | null;
  detalles: DetalleOCInput[];
}

export interface DetalleRecepcionInput {
  detalleOcId: string;
  cantidadRecibida: number;
}

export interface RecepcionInput {
  almacenId: string;
  observaciones: string | null;
  detalles: DetalleRecepcionInput[];
}

export interface DetalleRecepcion {
  id: string;
  detalleOcId: string;
  productoId: string;
  cantidadRecibida: number;
}

export interface Recepcion {
  id: string;
  ordenCompraId: string;
  almacenId: string;
  fecha: string;
  estado: string;
  observaciones: string | null;
  detalles: DetalleRecepcion[];
}
