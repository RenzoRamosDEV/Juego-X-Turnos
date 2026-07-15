package com.tuio.juegoxturnos.ui;

/**
 * Códigos de escape ANSI para dar color a la salida en la terminal.
 *
 * <p>Se agrupan aquí como constantes para no repetir secuencias crípticas por
 * todo el código y poder cambiar la paleta en un solo sitio.
 */
public final class Colores {

    public static final String RESET = "[0m";
    public static final String NEGRITA = "[1m";

    public static final String ROJO = "[31m";
    public static final String VERDE = "[32m";
    public static final String AMARILLO = "[33m";
    public static final String AZUL = "[34m";
    public static final String MAGENTA = "[35m";
    public static final String CIAN = "[36m";
    public static final String GRIS = "[90m";

    private Colores() {
        // Clase de utilidad: no se instancia.
    }

    /** Envuelve un texto entre un color y el reset. */
    public static String pintar(String texto, String color) {
        return color + texto + RESET;
    }
}
