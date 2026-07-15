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
     * Muestra el estado de ambos combatientes: el arte ASCII de cada uno con su
     * barra de vida y maná justo debajo.
     *
     * @param uno       primer combatiente
     * @param colorUno  color con el que se pinta su arte
     * @param dos       segundo combatiente
     * @param colorDos  color con el que se pinta su arte
     */
    public void mostrarEstado(Personaje uno, String colorUno, Personaje dos, String colorDos) {
        separador();
        mostrarArte(uno, colorUno);
        linea(estadoPersonaje(uno));
        linea();
        mostrarArte(dos, colorDos);
        linea(estadoPersonaje(dos));
        separador();
    }

    /** Pinta el arte ASCII de un personaje con el color indicado. */
    public void mostrarArte(Personaje personaje, String color) {
        for (String fila : ArteAscii.lineas(personaje.getNombre())) {
            linea(Colores.pintar(fila, color));
        }
    }

    private String estadoPersonaje(Personaje personaje) {
        String vida = barra(personaje.getVida(), personaje.getVidaMaxima(), colorSegunVida(personaje));
        String mana = barra(personaje.getMana(), personaje.getManaMaximo(), Colores.AZUL);
        StringBuilder linea = new StringBuilder(String.format("%-9s  VIDA %s %3d/%-3d   MANA %s %3d/%-3d",
                personaje.getNombre(),
                vida, personaje.getVida(), personaje.getVidaMaxima(),
                mana, personaje.getMana(), personaje.getManaMaximo()));

        if (personaje.getEscudo() > 0) {
            linea.append(Colores.pintar("   ESC " + personaje.getEscudo(), Colores.CIAN));
        }
        if (personaje.isDefendiendo()) {
            linea.append(Colores.pintar("   DEF", Colores.AZUL));
        }
        if (personaje.tieneEfectos()) {
            linea.append(Colores.pintar("   {" + String.join(", ", personaje.nombresEfectosActivos()) + "}",
                    Colores.MAGENTA));
        }
        return linea.toString();
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
