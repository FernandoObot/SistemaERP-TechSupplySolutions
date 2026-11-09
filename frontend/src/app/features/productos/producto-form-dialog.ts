import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Proveedor } from '../proveedores/proveedor.model';
import { Producto, ProductoInput } from './producto.model';

export interface ProductoDialogData {
  producto: Producto | null;
  proveedores: Proveedor[];
}

@Component({
  selector: 'app-producto-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './producto-form-dialog.html',
  styleUrl: './producto-form-dialog.scss',
})
export class ProductoFormDialog {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<ProductoFormDialog>);
  protected readonly data = inject<ProductoDialogData>(MAT_DIALOG_DATA);
  protected readonly producto = this.data.producto;

  protected readonly form = this.fb.nonNullable.group({
    sku: [this.producto?.sku ?? '', [Validators.required]],
    nombre: [this.producto?.nombre ?? '', [Validators.required]],
    categoria: [this.producto?.categoria ?? '', [Validators.required]],
    linea: [this.producto?.linea ?? '', [Validators.required]],
    precio: [this.producto?.precio ?? 0, [Validators.required, Validators.min(0)]],
    puntoReorden: [this.producto?.puntoReorden ?? 0, [Validators.required, Validators.min(0)]],
    proveedorPrincipalId: [this.producto?.proveedorPrincipalId ?? null],
    activo: [this.producto?.activo ?? true],
  });

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const input: ProductoInput = {
      sku: v.sku,
      nombre: v.nombre,
      categoria: v.categoria,
      linea: v.linea,
      precio: Number(v.precio),
      puntoReorden: Number(v.puntoReorden),
      proveedorPrincipalId: v.proveedorPrincipalId,
      activo: v.activo,
    };
    this.dialogRef.close(input);
  }

  protected cancel(): void {
    this.dialogRef.close();
  }
}
