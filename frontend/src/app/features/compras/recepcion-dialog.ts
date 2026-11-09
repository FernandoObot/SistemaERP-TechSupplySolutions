import { Component, inject } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Almacen } from '../almacenes/almacen.model';
import { Producto } from '../productos/producto.model';
import { OrdenCompra, Recepcion, RecepcionInput } from './compra.model';

export interface RecepcionDialogData {
  orden: OrdenCompra;
  almacenes: Almacen[];
  recepciones: Recepcion[];
  productos: Producto[];
}

@Component({
  selector: 'app-recepcion-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './recepcion-dialog.html',
  styleUrl: './recepcion-dialog.scss',
})
export class RecepcionDialog {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<RecepcionDialog>);
  protected readonly data = inject<RecepcionDialogData>(MAT_DIALOG_DATA);
  protected readonly error = { message: '' };

  protected readonly recibidoPorDetalle = new Map<string, number>();

  protected readonly form;

  constructor() {
    for (const recepcion of this.data.recepciones) {
      for (const detalle of recepcion.detalles) {
        const actual = this.recibidoPorDetalle.get(detalle.detalleOcId) ?? 0;
        this.recibidoPorDetalle.set(detalle.detalleOcId, actual + detalle.cantidadRecibida);
      }
    }

    this.form = this.fb.nonNullable.group({
      almacenId: ['', [Validators.required]],
      observaciones: [''],
      detalles: this.fb.array(
        this.data.orden.detalles.map((detalle) => {
          const recibido = this.recibidoPorDetalle.get(detalle.id) ?? 0;
          return this.fb.nonNullable.group({
            detalleOcId: [detalle.id],
            cantidadRecibida: [
              Math.max(detalle.cantidad - recibido, 0),
              [Validators.required, Validators.min(0)],
            ],
          });
        }),
      ),
    });
  }

  protected get detalles(): FormArray {
    return this.form.controls.detalles;
  }

  protected productoNombre(productoId: string): string {
    const p = this.data.productos.find((x) => x.id === productoId);
    return p ? `${p.sku} — ${p.nombre}` : productoId;
  }

  protected pendiente(detalleId: string, cantidad: number): number {
    return Math.max(cantidad - (this.recibidoPorDetalle.get(detalleId) ?? 0), 0);
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const detalles = this.detalles.controls
      .map((control) => control.getRawValue())
      .filter((d) => Number(d.cantidadRecibida) > 0)
      .map((d) => ({
        detalleOcId: d.detalleOcId,
        cantidadRecibida: Number(d.cantidadRecibida),
      }));

    if (detalles.length === 0) {
      this.error.message = 'Ingresa al menos una cantidad mayor a 0.';
      return;
    }

    const input: RecepcionInput = {
      almacenId: value.almacenId,
      observaciones: value.observaciones || null,
      detalles,
    };
    this.dialogRef.close(input);
  }

  protected cancel(): void {
    this.dialogRef.close();
  }
}
