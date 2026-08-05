#!/bin/bash
# Local reflection of .github/workflows/pull_request.yml — same commands, same order.
# The iOS job is stubbed out there, so nothing here runs iosSimulatorArm64Test either.
set -e

./gradlew :shared:testAndroidHostTest :server:test detekt --parallel
TZ=UTC ./gradlew :androidApp:verifyRoborazziDebug
