#!/usr/bin/env bash
#   scripts/lint-swift.sh [--fix]   — swift format over the iOS host. Ships in the toolchain.
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

# Not iosApp/ — that descends into build/ and lints the vendored Firebase sources.
readonly SOURCES=iosApp/iosApp

if [ "${1:-}" = "--fix" ]; then
  swift format --in-place --recursive "$SOURCES"
else
  swift format lint --strict --recursive "$SOURCES"
fi
