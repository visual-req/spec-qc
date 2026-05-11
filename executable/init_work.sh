#!/usr/bin/env sh
set -e

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR"

WORK_DIR="work"

mkdir -p "$WORK_DIR/input"
mkdir -p "$WORK_DIR/output"
mkdir -p "$WORK_DIR/quality"
mkdir -p "$WORK_DIR/req_copy"
mkdir -p "$WORK_DIR/revise"

echo "Work directories are ready:"
echo "  $PWD/$WORK_DIR/input"
echo "  $PWD/$WORK_DIR/output"
echo "  $PWD/$WORK_DIR/quality"
echo "  $PWD/$WORK_DIR/req_copy"
echo "  $PWD/$WORK_DIR/revise"
