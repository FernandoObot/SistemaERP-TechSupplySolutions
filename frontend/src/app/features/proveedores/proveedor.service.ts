import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/api/api.service';
import { Page } from '../../core/models/page.model';
import { Proveedor, ProveedorInput } from './proveedor.model';

@Injectable({ providedIn: 'root' })
export class ProveedorService {
  private readonly api = inject(ApiService);

  list(page = 0, size = 100): Observable<Page<Proveedor>> {
    return this.api.get<Page<Proveedor>>(`/proveedores?page=${page}&size=${size}`);
  }

  create(input: ProveedorInput): Observable<Proveedor> {
    return this.api.post<Proveedor>('/proveedores', input);
  }

  update(id: string, input: ProveedorInput): Observable<Proveedor> {
    return this.api.put<Proveedor>(`/proveedores/${id}`, input);
  }

  remove(id: string): Observable<void> {
    return this.api.delete<void>(`/proveedores/${id}`);
  }
}
