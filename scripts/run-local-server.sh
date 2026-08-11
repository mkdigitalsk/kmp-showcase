#!/usr/bin/env bash
#   scripts/run-local-server.sh   — Postgres, then the server. Config comes from .env.
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

[ -f .env ] || { echo "No .env — copy .env.example and fill it in." >&2; exit 2; }

docker compose up db -d
printf 'waiting for postgres'
until [ "$(docker inspect -f '{{.State.Health.Status}}' kmp-showcase-db 2>/dev/null)" = healthy ]; do
  printf '.'; sleep 2
done
echo ' ready'

echo "server on http://localhost:8080 — seed accounts with: node scripts/seed-users.mjs --env=local"
./gradlew :server:run
