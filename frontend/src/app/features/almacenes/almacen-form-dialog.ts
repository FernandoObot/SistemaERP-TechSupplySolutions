import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Almacen, AlmacenInput } from './almacen.model';

@Component({
  selector: 'app-almacen-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './almacen-form-dialog.html',
  styleUrl: './almacen-form-dialog.scss',
})
export class AlmacenFormDialog {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<AlmacenFormDialog>);
  protected readonly data = inject<Almacen | null>(MAT_DIALOG_DATA);

  protected readonly form = this.fb.nonNullable.group({
    codigo: [this.data?.codigo ?? '', [Validators.required]],
    nombre: [this.data?.nombre ?? '', [Validators.required]],
    ubicacion: [this.data?.ubicacion ?? ''],
    capacidad: [this.data?.capacidad ?? 0, [Validators.required, Validators.min(0)]],
    activo: [this.data?.activo ?? true],
  });

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const input: AlmacenInput = {
      codigo: v.codigo,
      nombre: v.nombre,
      ubicacion: v.ubicacion || null,
      capacidad: Number(v.capacidad),
      activo: v.activo,
    };
    this.dialogRef.close(input);
  }

  protected cancel(): void {
    this.dialogRef.close();
  }
}
