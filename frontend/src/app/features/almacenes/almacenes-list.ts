import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { Almacen } from './almacen.model';
import { AlmacenService } from './almacen.service';
import { AlmacenFormDialog } from './almacen-form-dialog';

@Component({
  selector: 'app-almacenes-list',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
  ],
  templateUrl: './almacenes-list.html',
  styleUrl: './almacenes-list.scss',
})
export class AlmacenesList implements OnInit {
  private readonly service = inject(AlmacenService);
  private readonly dialog = inject(MatDialog);

  protected readonly almacenes = signal<Almacen[]>([]);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly columns = ['codigo', 'nombre', 'ubicacion', 'capacidad', 'activo', 'acciones'];

  ngOnInit(): void {
    this.load();
  }

  protected openDialog(almacen: Almacen | null): void {
    this.dialog
      .open(AlmacenFormDialog, { data: almacen, width: '480px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        const request = almacen
          ? this.service.update(almacen.id, result)
          : this.service.create(result);
        request.subscribe({
          next: () => this.load(),
          error: () => this.error.set('No se pudo guardar el almacén.'),
        });
      });
  }

  protected remove(almacen: Almacen): void {
    if (!window.confirm(`¿Desactivar el almacén "${almacen.nombre}"?`)) return;
    this.service.remove(almacen.id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('No se pudo desactivar el almacén.'),
    });
  }

  private load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.service.list().subscribe({
      next: (page) => {
        this.almacenes.set(page.items);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo conectar con la API. ¿Está corriendo el backend?');
        this.loading.set(false);
      },
    });
  }
}
