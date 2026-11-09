import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/api/api.service';
import { Page } from '../../core/models/page.model';
import { Almacen, AlmacenInput } from './almacen.model';

@Injectable({ providedIn: 'root' })
export class AlmacenService {
  private readonly api = inject(ApiService);

  list(page = 0, size = 100): Observable<Page<Almacen>> {
    return this.api.get<Page<Almacen>>(`/almacenes?page=${page}&size=${size}`);
  }

  create(input: AlmacenInput): Observable<Almacen> {
    return this.api.post<Almacen>('/almacenes', input);
  }

  update(id: string, input: AlmacenInput): Observable<Almacen> {
    return this.api.put<Almacen>(`/almacenes/${id}`, input);
  }

  remove(id: string): Observable<void> {
    return this.api.delete<void>(`/almacenes/${id}`);
  }
}
