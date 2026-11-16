#!/bin/bash
# Re-records the golden images — run it when a UI change is intended, then review the diff as the change.
set -e

./gradlew :androidApp:recordRoborazziDebug
