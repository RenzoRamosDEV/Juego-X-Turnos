package com.tuio.juegoxturnos.ui;

import com.tuio.juegoxturnos.modelo.Personaje;
import lombok.RequiredArgsConstructor;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Capa de presentación en la terminal: menús, barras de vida/maná, narración
 * del combate y lectura de entrada del usuario.
 *
 * <p>Concentrar aquí toda la entrada/salida mantiene la lógica del juego libre
 * de detalles de formato y facilita cambiar la apariencia sin tocar el modelo.
 */
@RequiredArgsConstructor
public final class Consola {

    private static final int ANCHO_BARRA = 20;

    /** Fuente de entrada (normalmente {@code System.in}). */
    private final Scanner entrada;
    /** Destino de salida (normalmente {@code System.out}). */
    private final PrintStream salida;
    /** Milisegundos de pausa entre mensajes para dar ritmo al combate. */
    private final long pausaMs;

    public void linea(String texto) {
        salida.println(texto);
    }

    public void linea() {
        salida.println();
    }

    /** Escribe un mensaje y espera la pausa configurada para dar ritmo. */
    public void narrar(String texto) {
        salida.println(texto);
        pausa();
    }

    public void titulo(String texto) {
        linea();
        linea(Colores.pintar(Colores.NEGRITA + "=== " + texto + " ===", Colores.CIAN));
    }

    public void separador() {
        linea(Colores.pintar("--------------------------------------------------", Colores.GRIS));
    }

    /** Duerme el hilo la pausa configurada (ignora interrupciones). */
    public void pausa() {
        if (pausaMs <= 0) {
            return;
        }
        try {
            Thread.sleep(pausaMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Muestra el estado de ambos combatientes: para cada uno, su arte ASCII y,
     * debajo, su nombre, su barra de vida y su barra de maná.
     *
     * @param nombreUno nombre visible del primer combatiente
     * @param uno       personaje del primer combatiente
     * @param colorUno  color con el que se pinta
     * @param nombreDos nombre visible del segundo combatiente
     * @param dos       personaje del segundo combatiente
     * @param colorDos  color con el que se pinta
     */
    public void mostrarEstado(String nombreUno, Personaje uno, String colorUno,
                              String nombreDos, Personaje dos, String colorDos) {
        separador();
        mostrarCombatiente(nombreUno, uno, colorUno);
        linea();
        mostrarCombatiente(nombreDos, dos, colorDos);
        separador();
    }

    /** Pinta el arte ASCII de un personaje con el color indicado. */
    public void mostrarArte(Personaje personaje, String color) {
        for (String fila : ArteAscii.lineas(personaje.getNombre())) {
            linea(Colores.pintar(fila, color));
        }
    }

    /** Muestra el arte del combatiente y, debajo, su nombre, vida y maná. */
    private void mostrarCombatiente(String nombre, Personaje personaje, String color) {
        mostrarArte(personaje, color);
        linea(Colores.pintar(nombre, color + Colores.NEGRITA));
        linea(String.format("  VIDA %s %3d/%-3d",
                barra(personaje.getVida(), personaje.getVidaMaxima(), colorSegunVida(personaje)),
                personaje.getVida(), personaje.getVidaMaxima()));

        StringBuilder mana = new StringBuilder(String.format("  MANA %s %3d/%-3d",
                barra(personaje.getMana(), personaje.getManaMaximo(), Colores.AZUL),
                personaje.getMana(), personaje.getManaMaximo()));
        if (personaje.getEscudo() > 0) {
            mana.append(Colores.pintar("   ESC " + personaje.getEscudo(), Colores.CIAN));
        }
        if (personaje.isDefendiendo()) {
            mana.append(Colores.pintar("   DEF", Colores.AZUL));
        }
        if (personaje.tieneEfectos()) {
            mana.append(Colores.pintar("   {" + String.join(", ", personaje.nombresEfectosActivos()) + "}",
                    Colores.MAGENTA));
        }
        linea(mana.toString());
    }

    /** Construye una barra ASCII coloreada del tipo {@code [■■■■■□□□□□]}. */
    private String barra(int actual, int maximo, String color) {
        int llenas = maximo == 0 ? 0 : (int) Math.round((double) actual / maximo * ANCHO_BARRA);
        llenas = Math.max(0, Math.min(ANCHO_BARRA, llenas));
        String relleno = "■".repeat(llenas) + "□".repeat(ANCHO_BARRA - llenas);
        return "[" + Colores.pintar(relleno, color) + "]";
    }

    private String colorSegunVida(Personaje personaje) {
        double ratio = (double) personaje.getVida() / personaje.getVidaMaxima();
        if (ratio > 0.5) {
            return Colores.VERDE;
        }
        if (ratio > 0.25) {
            return Colores.AMARILLO;
        }
        return Colores.ROJO;
    }

    /**
     * Lee un entero dentro del rango indicado, reintentando ante entradas
     * inválidas.
     *
     * @param prompt texto a mostrar antes de leer
     * @param min    valor mínimo aceptado (incluido)
     * @param max    valor máximo aceptado (incluido)
     * @return el entero válido introducido por el usuario
     */
    /**
     * Lee una línea de texto no vacía.
     *
     * @param prompt texto a mostrar antes de leer
     * @return el texto introducido, sin espacios sobrantes
     */
    public String leerTexto(String prompt) {
        while (true) {
            salida.print(prompt + " ");
            if (!entrada.hasNextLine()) {
                throw new IllegalStateException("Se agotó la entrada mientras se esperaba un texto");
            }
            String texto = entrada.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            linea(Colores.pintar("El texto no puede estar vacío.", Colores.ROJO));
        }
    }

    public int leerOpcion(String prompt, int min, int max) {
        while (true) {
            salida.print(prompt + " ");
            if (!entrada.hasNextLine()) {
                throw new IllegalStateException("Se agotó la entrada mientras se esperaba una opción");
            }
            String texto = entrada.nextLine().trim();
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= min && valor <= max) {
                    return valor;
                }
            } catch (NumberFormatException ignorado) {
                // Se reintenta abajo.
            }
            linea(Colores.pintar("Opción inválida. Escribe un número entre " + min + " y " + max + ".", Colores.ROJO));
        }
    }
}
