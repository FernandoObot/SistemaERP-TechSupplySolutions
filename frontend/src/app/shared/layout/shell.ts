import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Router } from '@angular/router';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  disabled?: boolean;
}

@Component({
  selector: 'app-shell',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
  ],
  templateUrl: './shell.html',
  styleUrl: './shell.scss',
})
export class Shell {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' },
    { label: 'Gestión maestra', icon: 'inventory_2', route: '/productos' },
    { label: 'Compras', icon: 'shopping_cart', route: '/dashboard', disabled: true },
    { label: 'Inventario', icon: 'warehouse', route: '/dashboard', disabled: true },
    { label: 'Ventas', icon: 'point_of_sale', route: '/dashboard', disabled: true },
    { label: 'Contabilidad', icon: 'account_balance', route: '/dashboard', disabled: true },
    { label: 'Recursos humanos', icon: 'groups', route: '/dashboard', disabled: true },
    { label: 'Reportería', icon: 'bar_chart', route: '/dashboard', disabled: true },
  ];

  logout(): void {
    this.auth.logout();
    void this.router.navigate(['/login']);
  }
}
