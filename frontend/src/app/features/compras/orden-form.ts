import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';
import { Proveedor } from '../proveedores/proveedor.model';
import { ProveedorService } from '../proveedores/proveedor.service';
import { Producto } from '../productos/producto.model';
import { ProductoService } from '../productos/producto.service';
import { OrdenCompraInput } from './compra.model';
import { CompraService } from './compra.service';

@Component({
  selector: 'app-orden-form',
  imports: [
    CurrencyPipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './orden-form.html',
  styleUrl: './orden-form.scss',
})
export class OrdenForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(CompraService);
  private readonly proveedorService = inject(ProveedorService);
  private readonly productoService = inject(ProductoService);
  private readonly router = inject(Router);

  protected readonly proveedores = signal<Proveedor[]>([]);
  protected readonly productos = signal<Producto[]>([]);
  protected readonly saving = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    proveedorId: ['', [Validators.required]],
    observaciones: [''],
    detalles: this.fb.array([this.nuevaLinea()]),
  });

  ngOnInit(): void {
    this.proveedorService.list().subscribe({
      next: (page) => this.proveedores.set(page.items),
    });
    this.productoService.list().subscribe({
      next: (page) => this.productos.set(page.items),
    });
  }

  protected get detalles(): FormArray {
    return this.form.controls.detalles;
  }

  protected agregarLinea(): void {
    this.detalles.push(this.nuevaLinea());
  }

  protected quitarLinea(index: number): void {
    if (this.detalles.length > 1) {
      this.detalles.removeAt(index);
    }
  }

  protected onProducto(index: number, productoId: string): void {
    const producto = this.productos().find((p) => p.id === productoId);
    if (producto) {
      this.detalles.at(index).get('precioUnitario')?.setValue(producto.precio);
    }
  }

  protected subtotal(index: number): number {
    const value = this.detalles.at(index).getRawValue();
    return Number(value.cantidad ?? 0) * Number(value.precioUnitario ?? 0);
  }

  protected total(): number {
    return this.detalles.controls.reduce((acc, _, index) => acc + this.subtotal(index), 0);
  }

  protected guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const input: OrdenCompraInput = {
      proveedorId: value.proveedorId,
      observaciones: value.observaciones || null,
      detalles: this.detalles.controls.map((control) => {
        const d = control.getRawValue();
        return {
          productoId: d.productoId,
          cantidad: Number(d.cantidad),
          precioUnitario: Number(d.precioUnitario),
        };
      }),
    };

    this.saving.set(true);
    this.error.set(null);
    this.service.create(input).subscribe({
      next: (orden) => {
        this.saving.set(false);
        void this.router.navigate(['/compras', orden.id]);
      },
      error: (err) => {
        this.saving.set(false);
        const message = err?.error?.message ?? 'No se pudo crear la orden de compra.';
        this.error.set(message);
      },
    });
  }

  protected cancelar(): void {
    void this.router.navigate(['/compras']);
  }

  private nuevaLinea() {
    return this.fb.nonNullable.group({
      productoId: ['', [Validators.required]],
      cantidad: [1, [Validators.required, Validators.min(1)]],
      precioUnitario: [0, [Validators.required, Validators.min(0)]],
    });
  }
}
