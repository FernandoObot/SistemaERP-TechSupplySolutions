export interface Producto {
  id: string;
  sku: string;
  nombre: string;
  categoria: string;
  linea: string;
  precio: number;
  puntoReorden: number;
  proveedorPrincipalId: string | null;
  activo: boolean;
  version: number;
}

export interface ProductoInput {
  sku: string;
  nombre: string;
  categoria: string;
  linea: string;
  precio: number;
  puntoReorden: number;
  proveedorPrincipalId: string | null;
  activo: boolean;
}
