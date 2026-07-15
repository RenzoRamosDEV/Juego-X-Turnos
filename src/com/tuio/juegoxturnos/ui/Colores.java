package com.tuio.juegoxturnos.ui;

/**
 * Códigos de escape ANSI para dar color a la salida en la terminal.
 *
 * <p>Se agrupan aquí como constantes para no repetir secuencias crípticas por
 * todo el código y poder cambiar la paleta en un solo sitio. Se usan escapes
 * unicode (\\u001b) para representar el carácter ESC de forma legible.
 */
public final class Colores {

    private static final String ESC = "[";

    public static final String RESET = ESC + "0m";
    public static final String NEGRITA = ESC + "1m";
    public static final String TENUE = ESC + "2m";

    public static final String ROJO = ESC + "31m";
    public static final String VERDE = ESC + "32m";
    public static final String AMARILLO = ESC + "33m";
    public static final String AZUL = ESC + "34m";
    public static final String MAGENTA = ESC + "35m";
    public static final String CIAN = ESC + "36m";
    public static final String GRIS = ESC + "90m";

    // Variantes vivas (brillantes), para acentos y elementos destacados.
    public static final String ROJO_CLARO = ESC + "91m";
    public static final String VERDE_CLARO = ESC + "92m";
    public static final String AMARILLO_CLARO = ESC + "93m";
    public static final String AZUL_CLARO = ESC + "94m";
    public static final String MAGENTA_CLARO = ESC + "95m";
    public static final String CIAN_CLARO = ESC + "96m";
    public static final String BLANCO = ESC + "97m";

    private Colores() {
        // Clase de utilidad: no se instancia.
    }

    /** Envuelve un texto entre un color y el reset. */
    public static String pintar(String texto, String color) {
        return color + texto + RESET;
    }
}
