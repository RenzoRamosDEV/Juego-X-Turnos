#!/usr/bin/env bash
# Compila y ejecuta el juego por turnos.
set -euo pipefail

cd "$(dirname "$0")"

echo "Compilando..."
find src -name "*.java" > .sources.txt
javac -cp lib/lombok.jar -d out @.sources.txt
rm -f .sources.txt

echo "Iniciando el juego..."
exec java -cp out com.tuio.juegoxturnos.Main
