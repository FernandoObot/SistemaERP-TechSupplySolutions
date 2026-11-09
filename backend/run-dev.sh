#!/usr/bin/env bash
set -euo pipefail

# Ejecuta la API cargando las variables de ../.env
cd "$(dirname "$0")"

if [[ ! -f ../.env ]]; then
  echo "No existe ../.env. Copia .env.example y completa DATABASE_URL." >&2
  exit 1
fi

set -a
# shellcheck disable=SC1091
source ../.env
set +a

exec sbt "server/run"
