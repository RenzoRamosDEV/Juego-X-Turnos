> 🚧 **Proyecto en mejora continua** — se siguen añadiendo funcionalidades y puliendo el juego.

# Juego por Turnos

[![CI](https://github.com/RenzoRamosDEV/Juego-X-Turnos/actions/workflows/ci.yml/badge.svg)](https://github.com/RenzoRamosDEV/Juego-X-Turnos/actions/workflows/ci.yml)

Juego de combate por turnos para la terminal. Eliges un personaje y luchas en
duelos uno contra uno: contra la CPU, contra otra persona o en un torneo.

## Modos de juego

- **Un jugador contra la CPU:** eliges personaje y te enfrentas a un rival
  aleatorio controlado por la máquina.
- **Dos jugadores:** cada persona escribe su nombre y se turnan en el mismo
  teclado; la cabecera indica de quién es el turno.
- **Torneo:** con un solo personaje debes vencer en cadena al resto de rivales,
  recuperando parte de la vida entre rondas.

## Personajes

| Personaje | Vida | Crítico | Evasión | Ataque especial   | Efecto del especial     |
|-----------|------|---------|---------|-------------------|-------------------------|
| Guerrero  | 140  | 10 %    | 5 %     | Corte Destructor  | Aturdimiento (50 %)     |
| Mago      | 100  | 10 %    | 5 %     | Meteorito Mágico  | Quemadura (7/turno, 3t) |
| Arquero   | 110  | 15 %    | 10 %    | Flecha Perforante | Sangrado (6/turno, 2t)  |
| Asesino   | 105  | 25 %    | 15 %    | Golpe Mortal      | Veneno (6/turno, 3t)    |

Cada personaje tiene 3 ataques. El daño varía de forma aleatoria en cada golpe,
existe una probabilidad de **crítico** (x1.5) y el objetivo puede **esquivar**
según su evasión. El **ataque especial** pega más fuerte pero gasta **maná**,
que se regenera poco a poco cada turno.

## Acciones por turno

En tu turno puedes:

- **Atacar** con uno de tus tres ataques.
- **Defender:** reduces a la mitad el daño que recibas hasta tu siguiente turno.
- **Usar un objeto** del inventario.

Antes de cada acción (la de cada combatiente) se muestra de quién es el turno y
el **arte ASCII** de ambos, con su nombre y sus barras de vida y maná apiladas
justo debajo del personaje, para ver de un vistazo cómo va el duelo.

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

Cada push ejecuta la misma batería en **GitHub Actions** (ver `.github/workflows/ci.yml`).

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
│   ├── Juego.java            # Modos de juego, selección de personaje y bucle
│   ├── Combate.java          # Motor de un combate por turnos (simétrico)
│   ├── Combatiente.java      # Un contendiente: personaje + inventario + control
│   ├── Controlador.java      # Quién decide la acción (humano o CPU)
│   ├── ControladorHumano.java# Pide la acción por consola
│   ├── ControladorCpu.java   # Delega en la EstrategiaCPU
│   └── AccionTurno.java      # Acción de turno: atacar, defender o usar objeto
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
│   ├── ArteAscii.java        # Arte ASCII de cada personaje
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

- Sistema de niveles / experiencia entre combates.
- Más objetos y efectos (robo de vida, buff de daño, etc.).
- Migrar el proyecto a Maven o Gradle.
