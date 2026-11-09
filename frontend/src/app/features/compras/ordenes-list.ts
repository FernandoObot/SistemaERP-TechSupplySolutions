import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { Proveedor } from '../proveedores/proveedor.model';
import { ProveedorService } from '../proveedores/proveedor.service';
import { OrdenCompra } from './compra.model';
import { CompraService } from './compra.service';

@Component({
  selector: 'app-ordenes-list',
  imports: [
    CurrencyPipe,
    DatePipe,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
  ],
  templateUrl: './ordenes-list.html',
  styleUrl: './ordenes-list.scss',
})
export class OrdenesList implements OnInit {
  private readonly service = inject(CompraService);
  private readonly proveedorService = inject(ProveedorService);
  private readonly router = inject(Router);

  protected readonly ordenes = signal<OrdenCompra[]>([]);
  protected readonly proveedores = signal<Proveedor[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly columns = ['folio', 'proveedor', 'fecha', 'estado', 'total', 'acciones'];

  ngOnInit(): void {
    this.load();
    this.proveedorService.list().subscribe({
      next: (page) => this.proveedores.set(page.items),
      error: () => undefined,
    });
  }

  protected proveedorNombre(id: string): string {
    return this.proveedores().find((p) => p.id === id)?.nombre ?? id;
  }

  protected nueva(): void {
    void this.router.navigate(['/compras/nueva']);
  }

  protected ver(orden: OrdenCompra): void {
    void this.router.navigate(['/compras', orden.id]);
  }

  private load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.service.list().subscribe({
      next: (page) => {
        this.ordenes.set(page.items);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar las órdenes de compra.');
        this.loading.set(false);
      },
    });
  }
}
