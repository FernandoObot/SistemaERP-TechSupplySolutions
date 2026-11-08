import { Injectable, signal } from '@angular/core';

export interface SessionUser {
  id: string;
  email: string;
  name: string;
  roles: string[];
}

/**
 * Placeholder del componente C-01. En el Sprint 1 se conecta con
 * Supabase Auth (login/refresh) y un interceptor adjunta el JWT.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly user = signal<SessionUser | null>(null);

  login(email: string): void {
    this.user.set({
      id: 'demo',
      email,
      name: 'Usuario Demo',
      roles: ['ADMIN'],
    });
  }

  logout(): void {
    this.user.set(null);
  }

  hasRole(role: string): boolean {
    return this.user()?.roles.includes(role) ?? false;
  }

  hasPermission(resource: string, action: string): boolean {
    return this.hasRole('ADMIN') || `${resource}:${action}`.length > 0;
  }
}
