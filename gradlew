#!/usr/bin/env sh
# GameSidekick Gradle launcher. Uses installed Gradle when available;
# otherwise downloads the pinned Gradle 8.9 distribution.
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION=8.9
if command -v gradle >/dev/null 2>&1; then
  exec gradle -p "$APP_HOME" "$@"
fi
GRADLE_USER_HOME=${GRADLE_USER_HOME:-"$HOME/.gradle"}
DIST_DIR="$GRADLE_USER_HOME/wrapper/dists/gradle-$GRADLE_VERSION-bin/manual"
GRADLE_BIN="$DIST_DIR/gradle-$GRADLE_VERSION/bin/gradle"
if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$DIST_DIR"
  ZIP="$DIST_DIR/gradle-$GRADLE_VERSION-bin.zip"
  URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  if command -v curl >/dev/null 2>&1; then curl -fL --retry 3 "$URL" -o "$ZIP"
  elif command -v wget >/dev/null 2>&1; then wget -O "$ZIP" "$URL"
  else echo 'Gradle is not installed and neither curl nor wget is available.' >&2; exit 1; fi
  if command -v unzip >/dev/null 2>&1; then unzip -q -o "$ZIP" -d "$DIST_DIR"
  else echo 'The unzip utility is required to install Gradle.' >&2; exit 1; fi
fi
exec "$GRADLE_BIN" -p "$APP_HOME" "$@"
