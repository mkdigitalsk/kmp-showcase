#!/bin/bash
# UTC or the rendered timestamps carry your local offset and nothing matches.
set -e

TZ=UTC ./gradlew :androidApp:verifyRoborazziDebug
