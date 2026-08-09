#!/bin/bash
# Drop every table the server owns and let it recreate them on next boot.
#
# The server calls SchemaUtils.createMissingTablesAndColumns, which adds a missing column and never
# drops one — so a removed column survives on an already-populated database and breaks the next
# insert. This is the reset that clears it.
#
#   scripts/reset-db.sh --yes                    # local docker-compose database
#   DATABASE_URL=jdbc:postgresql://host/db DATABASE_USER=u DATABASE_PASSWORD=p scripts/reset-db.sh --yes
#
# Destroys data. It prints the target and refuses to run without --yes.

set -euo pipefail

JDBC_URL="${DATABASE_URL:-jdbc:postgresql://localhost:5432/kmpshowcase}"
PGUSER_="${DATABASE_USER:-postgres}"
PGPASSWORD_="${DATABASE_PASSWORD:-postgres}"

command -v psql >/dev/null || { echo "psql not found — brew install libpq" >&2; exit 1; }

# jdbc:postgresql://host:port/db?params -> host, port, db
STRIPPED="${JDBC_URL#jdbc:postgresql://}"
HOSTPORT="${STRIPPED%%/*}"
DBNAME="${STRIPPED#*/}"; DBNAME="${DBNAME%%\?*}"
HOST="${HOSTPORT%%:*}"
PORT="${HOSTPORT##*:}"; [ "$PORT" = "$HOST" ] && PORT=5432

echo "target: $DBNAME on $HOST:$PORT as $PGUSER_"

[ "${1:-}" = "--yes" ] || { echo "refusing without --yes (this destroys every table)" >&2; exit 1; }

# DROP SCHEMA rather than per-table DROPs: the point is to clear whatever is there, including tables
# an older build created and this one no longer declares.
PGPASSWORD="$PGPASSWORD_" psql -v ON_ERROR_STOP=1 -h "$HOST" -p "$PORT" -U "$PGUSER_" -d "$DBNAME" \
    -c 'DROP SCHEMA public CASCADE; CREATE SCHEMA public;'

echo "done — restart the server to recreate the tables"
