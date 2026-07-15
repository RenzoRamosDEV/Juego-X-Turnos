# Juego por Turnos

Juego de combate por turnos para la terminal. Eliges un personaje y te enfrentas
a la CPU en un duelo uno contra uno.

## Personajes

| Personaje | Vida | Crítico | Ataque especial     |
|-----------|------|---------|---------------------|
| Guerrero  | 140  | 10 %    | Corte Destructor    |
| Mago      | 100  | 10 %    | Meteorito Mágico    |
| Arquero   | 110  | 15 %    | Flecha Perforante   |
| Asesino   | 105  | 25 %    | Golpe Mortal        |

Cada personaje tiene 3 ataques. El daño varía de forma aleatoria en cada golpe y
existe una probabilidad de **crítico** (x1.5). El **ataque especial** pega más
fuerte pero gasta **maná**, que se regenera poco a poco cada turno.

## Cómo jugar

Requisitos: **JDK 17** o superior. Las dependencias (Lombok y JUnit 5) están
incluidas como `.jar` en `lib/`, así que no hace falta ningún gestor de paquetes.

```bash
# Compilar y ejecutar
./run.sh
```

De forma manual:

```bash
find src -name "*.java" > sources.txt
javac -cp lib/lombok.jar -d out @sources.txt && rm sources.txt
java -cp out com.tuio.juegoxturnos.Main
```

## Tests

Los tests usan **JUnit 5** y cubren la aleatoriedad, los ataques, el personaje
(daño, críticos, maná), el balance de los personajes concretos y la IA.

```bash
./test.sh
```

## Dependencias

- **Lombok** (`@Getter`, `@RequiredArgsConstructor`) para eliminar getters y
  constructores repetitivos del modelo.
- **JUnit 5** (`junit-platform-console-standalone`) para las pruebas.

Ambas viven en `lib/`. Si usas un IDE, habilita el *annotation processing* de
Lombok para que reconozca los getters generados.

## Estructura del proyecto

```
src/com/tuio/juegoxturnos/
├── Main.java                 # Punto de entrada
├── combate/
│   ├── Juego.java            # Menú, selección de personaje y bucle de partidas
│   └── Combate.java          # Motor de un combate por turnos
├── ia/
│   └── EstrategiaCPU.java    # Decisiones de la CPU
├── modelo/
│   ├── Ataque.java           # Definición de un ataque
│   ├── Personaje.java        # Clase base de personaje
│   ├── ResultadoAtaque.java  # Resultado inmutable de un golpe
│   └── personajes/           # Guerrero, Mago, Arquero, Asesino
├── ui/
│   ├── Consola.java          # Menús, barras de vida/maná, narración
│   └── Colores.java          # Códigos ANSI de color
└── util/
    └── Aleatorio.java        # Aleatoriedad centralizada (semilla fijable)

test/com/tuio/juegoxturnos/   # Tests JUnit 5 (espejo de la estructura de src/)
├── util/AleatorioTest.java
├── modelo/
│   ├── AtaqueTest.java
│   ├── PersonajeTest.java
│   ├── PersonajeDePrueba.java        # subclase mínima para pruebas
│   └── personajes/PersonajesConcretosTest.java
└── ia/EstrategiaCPUTest.java

lib/                          # Dependencias (Lombok, JUnit 5)
```

## Ideas para próximas versiones

- Efectos de estado (veneno, quemadura, aturdir) por personaje.
- Ítems consumibles (pociones, escudo).
- Modo 2 jugadores en el mismo teclado.
- Sistema de niveles / experiencia y modo torneo.
