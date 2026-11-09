import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { Proveedor } from '../proveedores/proveedor.model';
import { ProveedorService } from '../proveedores/proveedor.service';
import { Producto } from './producto.model';
import { ProductoService } from './producto.service';
import { ProductoFormDialog } from './producto-form-dialog';

@Component({
  selector: 'app-productos-list',
  imports: [
    CurrencyPipe,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
  ],
  templateUrl: './productos-list.html',
  styleUrl: './productos-list.scss',
})
export class ProductosList implements OnInit {
  private readonly service = inject(ProductoService);
  private readonly proveedorService = inject(ProveedorService);
  private readonly dialog = inject(MatDialog);

  protected readonly productos = signal<Producto[]>([]);
  protected readonly proveedores = signal<Proveedor[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly columns = [
    'sku',
    'nombre',
    'categoria',
    'precio',
    'puntoReorden',
    'proveedor',
    'activo',
    'acciones',
  ];

  ngOnInit(): void {
    this.load();
    this.loadProveedores();
  }

  protected proveedorNombre(id: string | null): string {
    if (!id) return '—';
    return this.proveedores().find((p) => p.id === id)?.nombre ?? '—';
  }

  protected openDialog(producto: Producto | null): void {
    this.dialog
      .open(ProductoFormDialog, {
        data: { producto, proveedores: this.proveedores() },
        width: '520px',
      })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        const request = producto
          ? this.service.update(producto.id, result)
          : this.service.create(result);
        request.subscribe({
          next: () => this.load(),
          error: () => this.error.set('No se pudo guardar el producto.'),
        });
      });
  }

  protected remove(producto: Producto): void {
    if (!window.confirm(`¿Desactivar el producto "${producto.nombre}"?`)) return;
    this.service.remove(producto.id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('No se pudo desactivar el producto.'),
    });
  }

  private load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.service.list().subscribe({
      next: (page) => {
        this.productos.set(page.items);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(
          'No se pudo conectar con la API. Verifica que el backend esté corriendo en http://localhost:8080.',
        );
        this.loading.set(false);
      },
    });
  }

  private loadProveedores(): void {
    this.proveedorService.list().subscribe({
      next: (page) => this.proveedores.set(page.items),
      error: () => undefined,
    });
  }
}
