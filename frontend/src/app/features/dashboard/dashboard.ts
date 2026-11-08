import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

interface Metric {
  label: string;
  value: string;
  icon: string;
  hint: string;
}

@Component({
  selector: 'app-dashboard',
  imports: [MatCardModule, MatIconModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard {
  protected readonly metrics: Metric[] = [
    { label: 'Ventas del día', value: '$0.00', icon: 'payments', hint: 'Sin datos aún' },
    {
      label: 'Productos con stock bajo',
      value: '0',
      icon: 'warning',
      hint: 'Alerta de inventario',
    },
    {
      label: 'Órdenes de compra pendientes',
      value: '0',
      icon: 'shopping_cart',
      hint: 'Sprint 1-2',
    },
    {
      label: 'Cuentas por cobrar',
      value: '$0.00',
      icon: 'receipt_long',
      hint: 'Sprint 5-6',
    },
  ];
}
