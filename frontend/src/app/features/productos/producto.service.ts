import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/api/api.service';
import { Page, Producto } from './producto.model';

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private readonly api = inject(ApiService);

  list(page = 0, size = 20): Observable<Page<Producto>> {
    return this.api.get<Page<Producto>>(`/productos?page=${page}&size=${size}`);
  }
}
