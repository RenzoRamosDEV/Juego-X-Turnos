#!/usr/bin/env bash
# Compila el código y los tests, y ejecuta la batería de pruebas con JUnit 5.
set -euo pipefail

cd "$(dirname "$0")"

LOMBOK="lib/lombok.jar"
JUNIT="lib/junit-platform-console-standalone.jar"

echo "Compilando código fuente..."
find src -name "*.java" > .sources.txt
javac -cp "$LOMBOK" -d out @.sources.txt
rm -f .sources.txt

echo "Compilando tests..."
mkdir -p out-test
find test -name "*.java" > .testsources.txt
javac -cp "$LOMBOK:$JUNIT:out" -d out-test @.testsources.txt
rm -f .testsources.txt

echo "Ejecutando tests..."
exec java -jar "$JUNIT" execute \
  -cp "out:out-test" \
  --scan-classpath \
  --details=tree --disable-banner
