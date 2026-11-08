# Frontend — ERP TechSupply Solutions

Aplicación Angular 22 (standalone) con Angular Material.

## Comandos

```bash
npm ci
npm start                      # http://localhost:4200
npm run build
npm test -- --watch=false
```

## Configuración

La URL de la API se define en `src/environments/environment.development.ts`:

```ts
apiUrl: 'http://localhost:8080/api/v1'
```

## Estructura

```
src/app/
├── core/          # API, autenticación (stub de Supabase en Sprint 1)
├── shared/        # layout y componentes reutilizables
└── features/      # dashboard, login, productos
```

## Sprint 0

- Login de demostración (sin Supabase Auth todavía).
- Shell con navegación lateral.
- Dashboard con métricas placeholder.
- Listado de productos consumiendo `GET /api/v1/productos`.
