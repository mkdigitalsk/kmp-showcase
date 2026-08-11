#!/usr/bin/env bash
# The server against a local Postgres, in one command.
#
#   scripts/run-local-server.sh
#
# Postgres rather than the H2 the config defaults to: H2 has no UPDATE … RETURNING, so a conditional
# write — every note edit — fails there in a way that reads as a bug in the route.
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

# Regenerating the signing key each run would invalidate the token in every open browser tab, so it is
# generated once and kept in the gitignored local.properties.
SECRET_FILE=local.properties
if ! grep -q '^jwt.secret=' "$SECRET_FILE" 2>/dev/null; then
  echo "jwt.secret=$(openssl rand -base64 48)" >> "$SECRET_FILE"
  echo "generated a signing key into $SECRET_FILE"
fi
JWT_SECRET=$(grep '^jwt.secret=' "$SECRET_FILE" | cut -d= -f2-)

docker compose up db -d
printf 'waiting for postgres'
until [ "$(docker inspect -f '{{.State.Health.Status}}' kmp-showcase-db 2>/dev/null)" = healthy ]; do
  printf '.'; sleep 2
done
echo ' ready'

echo "server on http://localhost:8080 — seed accounts with: node scripts/seed-users.mjs --env=local"
USE_H2=false \
DATABASE_URL=jdbc:postgresql://localhost:5432/kmpshowcase \
DATABASE_USER=postgres \
DATABASE_PASSWORD=postgres \
CORS_ALLOWED_HOSTS=localhost \
JWT_SECRET="$JWT_SECRET" \
  ./gradlew :server:run
