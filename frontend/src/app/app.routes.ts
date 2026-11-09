import { Routes } from '@angular/router';
import { AlmacenesList } from './features/almacenes/almacenes-list';
import { OrdenDetalle } from './features/compras/orden-detalle';
import { OrdenForm } from './features/compras/orden-form';
import { OrdenesList } from './features/compras/ordenes-list';
import { Dashboard } from './features/dashboard/dashboard';
import { Login } from './features/login/login';
import { ProductosList } from './features/productos/productos-list';
import { ProveedoresList } from './features/proveedores/proveedores-list';
import { Shell } from './shared/layout/shell';

export const routes: Routes = [
  { path: 'login', component: Login },
  {
    path: '',
    component: Shell,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: Dashboard },
      { path: 'productos', component: ProductosList },
      { path: 'proveedores', component: ProveedoresList },
      { path: 'almacenes', component: AlmacenesList },
      { path: 'compras', component: OrdenesList },
      { path: 'compras/nueva', component: OrdenForm },
      { path: 'compras/:id', component: OrdenDetalle },
    ],
  },
  { path: '**', redirectTo: '' },
];
