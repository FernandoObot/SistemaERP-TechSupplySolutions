import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/api/api.service';
import { Page } from '../../core/models/page.model';
import { Producto, ProductoInput } from './producto.model';

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private readonly api = inject(ApiService);

  list(page = 0, size = 50): Observable<Page<Producto>> {
    return this.api.get<Page<Producto>>(`/productos?page=${page}&size=${size}`);
  }

  get(id: string): Observable<Producto> {
    return this.api.get<Producto>(`/productos/${id}`);
  }

  create(input: ProductoInput): Observable<Producto> {
    return this.api.post<Producto>('/productos', input);
  }

  update(id: string, input: ProductoInput): Observable<Producto> {
    return this.api.put<Producto>(`/productos/${id}`, input);
  }

  remove(id: string): Observable<void> {
    return this.api.delete<void>(`/productos/${id}`);
  }
}
