# Juego por Turnos

Juego de combate por turnos para la terminal. Eliges un personaje y te enfrentas
a la CPU en un duelo uno contra uno.

## Personajes

| Personaje | Vida | Crítico | Ataque especial     | Efecto del especial       |
|-----------|------|---------|---------------------|---------------------------|
| Guerrero  | 140  | 10 %    | Corte Destructor    | Aturdimiento (50 %)       |
| Mago      | 100  | 10 %    | Meteorito Mágico    | Quemadura (7/turno, 3t)   |
| Arquero   | 110  | 15 %    | Flecha Perforante   | Sangrado (6/turno, 2t)    |
| Asesino   | 105  | 25 %    | Golpe Mortal        | Veneno (6/turno, 3t)      |

Cada personaje tiene 3 ataques. El daño varía de forma aleatoria en cada golpe y
existe una probabilidad de **crítico** (x1.5). El **ataque especial** pega más
fuerte pero gasta **maná**, que se regenera poco a poco cada turno.

### Efectos de estado

El ataque especial aplica un efecto que se procesa al inicio de cada turno del
afectado:

- **Veneno / Quemadura / Sangrado:** daño por turno durante varios turnos.
- **Aturdimiento:** el enemigo pierde su siguiente turno.

### Objetos

Cada combatiente empieza con un inventario y puede usar un objeto en su turno en
lugar de atacar:

- **Poción de Vida (x2):** restaura 35 de vida.
- **Escudo (x1):** absorbe los próximos 30 puntos de daño.
- **Antídoto (x1):** elimina todos los efectos de estado activos.

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
│   ├── Combate.java          # Motor de un combate por turnos
│   └── AccionTurno.java      # Acción de turno: atacar o usar un objeto
├── ia/
│   └── EstrategiaCPU.java    # Decisiones de la CPU
├── modelo/
│   ├── Ataque.java           # Definición de un ataque (puede aplicar un efecto)
│   ├── Personaje.java        # Clase base de personaje (vida, maná, escudo, efectos)
│   ├── ResultadoAtaque.java  # Resultado inmutable de un golpe
│   ├── personajes/           # Guerrero, Mago, Arquero, Asesino
│   ├── efectos/              # EfectoEstado, DanioPorTurno, Aturdimiento, Efectos
│   └── items/                # Item, PocionVida, Escudo, Antidoto, Inventario
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
│   ├── personajes/PersonajesConcretosTest.java
│   ├── efectos/                      # DanioPorTurnoTest, AturdimientoTest
│   └── items/                        # InventarioTest, ItemsTest
└── ia/EstrategiaCPUTest.java

lib/                          # Dependencias (Lombok, JUnit 5)
```

## Ideas para próximas versiones

- Modo 2 jugadores en el mismo teclado.
- Sistema de niveles / experiencia y modo torneo.
- Más objetos y efectos (aturdimiento por objeto, robo de vida, etc.).
