import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { Proveedor } from './proveedor.model';
import { ProveedorService } from './proveedor.service';
import { ProveedorFormDialog } from './proveedor-form-dialog';

@Component({
  selector: 'app-proveedores-list',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
  ],
  templateUrl: './proveedores-list.html',
  styleUrl: './proveedores-list.scss',
})
export class ProveedoresList implements OnInit {
  private readonly service = inject(ProveedorService);
  private readonly dialog = inject(MatDialog);

  protected readonly proveedores = signal<Proveedor[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly columns = ['nombre', 'contacto', 'email', 'telefono', 'evaluacion', 'activo', 'acciones'];

  ngOnInit(): void {
    this.load();
  }

  protected openDialog(proveedor: Proveedor | null): void {
    this.dialog
      .open(ProveedorFormDialog, { data: proveedor, width: '520px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        const request = proveedor
          ? this.service.update(proveedor.id, result)
          : this.service.create(result);
        request.subscribe({
          next: () => this.load(),
          error: () => this.error.set('No se pudo guardar el proveedor.'),
        });
      });
  }

  protected remove(proveedor: Proveedor): void {
    if (!window.confirm(`¿Desactivar el proveedor "${proveedor.nombre}"?`)) return;
    this.service.remove(proveedor.id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('No se pudo desactivar el proveedor.'),
    });
  }

  private load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.service.list().subscribe({
      next: (page) => {
        this.proveedores.set(page.items);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo conectar con la API. ¿Está corriendo el backend?');
        this.loading.set(false);
      },
    });
  }
}
