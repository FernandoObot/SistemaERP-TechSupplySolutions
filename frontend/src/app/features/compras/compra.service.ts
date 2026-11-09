import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/api/api.service';
import { Page } from '../../core/models/page.model';
import { OrdenCompra, OrdenCompraInput, Recepcion, RecepcionInput } from './compra.model';

@Injectable({ providedIn: 'root' })
export class CompraService {
  private readonly api = inject(ApiService);

  list(page = 0, size = 50): Observable<Page<OrdenCompra>> {
    return this.api.get<Page<OrdenCompra>>(`/ordenes-compra?page=${page}&size=${size}`);
  }

  get(id: string): Observable<OrdenCompra> {
    return this.api.get<OrdenCompra>(`/ordenes-compra/${id}`);
  }

  create(input: OrdenCompraInput): Observable<OrdenCompra> {
    return this.api.post<OrdenCompra>('/ordenes-compra', input);
  }

  recibir(id: string, input: RecepcionInput): Observable<Recepcion> {
    return this.api.post<Recepcion>(`/ordenes-compra/${id}/recepciones`, input);
  }

  recepciones(id: string): Observable<Recepcion[]> {
    return this.api.get<Recepcion[]>(`/ordenes-compra/${id}/recepciones`);
  }
}
