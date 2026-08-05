#!/bin/bash
# Re-records the golden images — run it when a UI change is intended, then review the diff as the change.
# UTC or your local offset is baked into every timestamp and no other machine can verify them.
set -e

TZ=UTC ./gradlew :androidApp:recordRoborazziDebug
