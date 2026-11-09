export interface Almacen {
  id: string;
  codigo: string;
  nombre: string;
  ubicacion: string | null;
  capacidad: number;
  activo: boolean;
  version: number;
}

export interface AlmacenInput {
  codigo: string;
  nombre: string;
  ubicacion: string | null;
  capacidad: number;
  activo: boolean;
}
