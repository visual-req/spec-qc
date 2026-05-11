#!/usr/bin/env sh
set -e

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR"

if [ "$#" -eq 0 ]; then
  exec java -jar "spec-qc-0.1.0.jar" web
fi

exec java -jar "spec-qc-0.1.0.jar" "$@"
