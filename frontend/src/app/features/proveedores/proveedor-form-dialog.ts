import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Proveedor, ProveedorInput } from './proveedor.model';

@Component({
  selector: 'app-proveedor-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './proveedor-form-dialog.html',
  styleUrl: './form-dialog.scss',
})
export class ProveedorFormDialog {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<ProveedorFormDialog>);
  protected readonly data = inject<Proveedor | null>(MAT_DIALOG_DATA);

  protected readonly form = this.fb.nonNullable.group({
    nombre: [this.data?.nombre ?? '', [Validators.required]],
    contacto: [this.data?.contacto ?? ''],
    email: [this.data?.email ?? ''],
    telefono: [this.data?.telefono ?? ''],
    condicionesPago: [this.data?.condicionesPago ?? ''],
    evaluacion: [this.data?.evaluacion ?? null],
    activo: [this.data?.activo ?? true],
  });

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const input: ProveedorInput = {
      nombre: v.nombre,
      contacto: v.contacto || null,
      email: v.email || null,
      telefono: v.telefono || null,
      condicionesPago: v.condicionesPago || null,
      evaluacion: v.evaluacion == null ? null : Number(v.evaluacion),
      activo: v.activo,
    };
    this.dialogRef.close(input);
  }

  protected cancel(): void {
    this.dialogRef.close();
  }
}
