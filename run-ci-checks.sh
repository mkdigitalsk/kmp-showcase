#!/bin/bash
# Local reflection of .github/workflows/pull_request.yml — same commands, same order.
# The iOS job is stubbed out there, so nothing here runs iosSimulatorArm64Test either.
set -e

./gradlew :shared:testAndroidHostTest :server:test detekt --parallel
TZ=UTC ./gradlew :androidApp:verifyRoborazziDebug

# Green — record the tree proved, the index with the tracked edits beside it, for the push check in studio-seed's
# commit hook (rules/pre-commit-review.md § Push). A tree the list does not hold is one no gate ran on.
STASHED="$(git stash create)"
git rev-parse "${STASHED:-HEAD}^{tree}" >> "$(git rev-parse --git-common-dir)/ci-green"
