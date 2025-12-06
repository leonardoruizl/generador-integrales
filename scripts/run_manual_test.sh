#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="$ROOT_DIR/out"

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Compila las clases principales con las dependencias locales
find "$ROOT_DIR/src/main/java" -name '*.java' | \
  xargs javac -cp "$ROOT_DIR/lib/*" -d "$OUT_DIR"

# Compila el test manual que no depende de JUnit
javac -cp "$OUT_DIR:$ROOT_DIR/lib/*" -d "$OUT_DIR" \
  "$ROOT_DIR/src/test/java/com/model/ManualIntegralTest.java"

# Ejecuta la validación manual
java -cp "$OUT_DIR:$ROOT_DIR/lib/*" com.model.ManualIntegralTest
