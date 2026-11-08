# Gestión de ramas y commits

## Estrategia recomendada: GitHub Flow

Para un equipo pequeño con entregas por sprints, **GitHub Flow** es más simple y
seguro que Git Flow completo (que introduce `develop`, `release/*` y `hotfix/*` con
poca ganancia en un proyecto académico).

```
main ────●────────●────────●────────●───►  (siempre estable/entregable)
          \      /         /        /
           feat/...      fix/...   docs/...
```

Reglas:

1. `main` está protegida: no se hace push directo, solo PR aprobado.
2. Cada rama nace de `main` con nombre semántico y vida corta (≤ 3 días ideal).
3. Un PR = un cambio coherente; debe pasar CI (lint, tests, build).
4. Se elimina la rama al hacer merge (squash o merge commit según el equipo).
5. `develop` solo se conserva si la cátedra lo exige; si no, se elimina para no
   duplicar la integración.

### Convención de nombres de rama

| Prefijo | Uso | Ejemplo |
|---|---|---|
| `feat/` | nueva funcionalidad | `feat/modulo-proveedores` |
| `fix/` | corrección de defecto | `fix/validacion-stock` |
| `docs/` | documentación | `docs/sad-4mas1` |
| `refactor/` | reestructuración sin cambio funcional | `refactor/repositorio-compras` |
| `test/` | pruebas | `test/cobertura-ventas` |
| `chore/` | configuración/build | `chore/ci-github-actions` |

## Conventional Commits

Formato: `<tipo>(<alcance opcional>): <descripción en imperativo>`

```
feat(compras): generar orden de compra desde requisición
fix(inventario): impedir salida con stock insuficiente
docs(sad): agregar vista de despliegue
test(ventas): cubrir cálculo de impuestos
chore(ci): publicar OpenAPI en artifacts
```

Tipos permitidos: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`,
`build`, `ci`, `chore`, `revert`.

Herramientas sugeridas:

- `commitlint` + `husky` para validar el mensaje en el hook `commit-msg`.
- `standard-version` o `semantic-release` para generar `CHANGELOG.md` y versiones.
- Etiquetas `v0.1.0`, `v0.2.0`, … al cerrar cada sprint.

## Trazabilidad con la rúbrica

- Vincular cada PR a un requisito (`RF-COM-01`) o caso de uso (`CU-05`) en la
  descripción y en el commit.
- Mantener `main` compilando y con la suite de pruebas en verde.
