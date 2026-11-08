import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { Producto } from './producto.model';
import { ProductoService } from './producto.service';

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

  protected readonly productos = signal<Producto[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly columns = [
    'sku',
    'nombre',
    'categoria',
    'precio',
    'puntoReorden',
    'activo',
  ];

  ngOnInit(): void {
    this.load();
  }

  protected reload(): void {
    this.load();
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
}
