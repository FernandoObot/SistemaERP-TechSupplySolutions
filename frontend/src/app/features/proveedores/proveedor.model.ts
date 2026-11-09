export interface Proveedor {
  id: string;
  nombre: string;
  contacto: string | null;
  email: string | null;
  telefono: string | null;
  condicionesPago: string | null;
  evaluacion: number | null;
  activo: boolean;
  version: number;
}

export interface ProveedorInput {
  nombre: string;
  contacto: string | null;
  email: string | null;
  telefono: string | null;
  condicionesPago: string | null;
  evaluacion: number | null;
  activo: boolean;
}
