export interface Producto {
  id: string;
  sku: string;
  nombre: string;
  categoria: string;
  linea: string;
  precio: number;
  puntoReorden: number;
  activo: boolean;
  version: number;
}

export interface Page<T> {
  items: T[];
  page: number;
  size: number;
  total: number;
  totalPages: number;
}
