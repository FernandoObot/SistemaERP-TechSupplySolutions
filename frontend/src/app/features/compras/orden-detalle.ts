import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { Almacen } from '../almacenes/almacen.model';
import { AlmacenService } from '../almacenes/almacen.service';
import { Producto } from '../productos/producto.model';
import { ProductoService } from '../productos/producto.service';
import { OrdenCompra, Recepcion } from './compra.model';
import { CompraService } from './compra.service';
import { RecepcionDialog } from './recepcion-dialog';

@Component({
  selector: 'app-orden-detalle',
  imports: [
    CurrencyPipe,
    DatePipe,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
  ],
  templateUrl: './orden-detalle.html',
  styleUrl: './orden-detalle.scss',
})
export class OrdenDetalle implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly service = inject(CompraService);
  private readonly almacenService = inject(AlmacenService);
  private readonly productoService = inject(ProductoService);
  private readonly dialog = inject(MatDialog);

  protected readonly orden = signal<OrdenCompra | null>(null);
  protected readonly recepciones = signal<Recepcion[]>([]);
  protected readonly almacenes = signal<Almacen[]>([]);
  protected readonly productos = signal<Producto[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly detalleColumns = ['producto', 'cantidad', 'precio', 'subtotal', 'recibido', 'pendiente'];
  protected readonly recepcionColumns = ['fecha', 'almacen', 'estado', 'detalles'];

  private ordenId = '';

  ngOnInit(): void {
    this.ordenId = this.route.snapshot.paramMap.get('id') ?? '';
    this.almacenService.list().subscribe({ next: (page) => this.almacenes.set(page.items) });
    this.productoService.list().subscribe({ next: (page) => this.productos.set(page.items) });
    this.load();
  }

  protected productoNombre(id: string): string {
    const p = this.productos().find((x) => x.id === id);
    return p ? `${p.sku} — ${p.nombre}` : id;
  }

  protected almacenNombre(id: string): string {
    return this.almacenes().find((a) => a.id === id)?.nombre ?? id;
  }

  protected recibido(detalleId: string): number {
    return this.recepciones().reduce((acc, recepcion) => {
      const detalle = recepcion.detalles.find((d) => d.detalleOcId === detalleId);
      return acc + (detalle?.cantidadRecibida ?? 0);
    }, 0);
  }

  protected recibir(): void {
    const orden = this.orden();
    if (!orden) return;
    this.dialog
      .open(RecepcionDialog, {
        data: {
          orden,
          almacenes: this.almacenes(),
          recepciones: this.recepciones(),
          productos: this.productos(),
        },
        width: '760px',
      })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        this.service.recibir(orden.id, result).subscribe({
          next: () => this.load(),
          error: (err) =>
            this.error.set(err?.error?.message ?? 'No se pudo registrar la recepción.'),
        });
      });
  }

  protected volver(): void {
    void this.router.navigate(['/compras']);
  }

  private load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.service.get(this.ordenId).subscribe({
      next: (orden) => {
        this.orden.set(orden);
        this.service.recepciones(this.ordenId).subscribe({
          next: (recepciones) => {
            this.recepciones.set(recepciones);
            this.loading.set(false);
          },
          error: () => {
            this.recepciones.set([]);
            this.loading.set(false);
          },
        });
      },
      error: () => {
        this.error.set('No se pudo cargar la orden de compra.');
        this.loading.set(false);
      },
    });
  }
}
