package com.tuio.juegoxturnos.ui;

import java.util.List;
import java.util.Map;

/**
 * Arte ASCII de cada personaje, para darle cara a los combatientes en la
 * terminal. Se guarda como líneas sueltas para poder pintarlas con color sin
 * pelearse con saltos de línea ni sangrías.
 */
public final class ArteAscii {

    private static final Map<String, List<String>> ARTE = Map.of(
            "Guerrero", List.of(
                    "   ___   ",
                    "  |o o|  ",
                    "  |_H_|  ",
                    " /|   |\\ ",
                    "  |___|  ",
                    "  /   \\  "),
            "Mago", List.of(
                    "    /\\    ",
                    "   /  \\   ",
                    "  /_oo_\\  ",
                    "   |  |   ",
                    "   |--|   ",
                    "  /____\\  "),
            "Arquero", List.of(
                    "   ___   ",
                    "  |o o|  ",
                    "  |_-_|) ",
                    "  /|  |) ",
                    "   |  |) ",
                    "  /|__|\\ "),
            "Asesino", List.of(
                    "   ___   ",
                    "  /^^^\\  ",
                    " |x   x| ",
                    "  \\_v_/  ",
                    "  /|  |\\ ",
                    "   |__|  "));

    private ArteAscii() {
        // Clase de utilidad: no se instancia.
    }

    /**
     * Devuelve las líneas del arte del personaje indicado.
     *
     * @param nombre nombre del personaje
     * @return las líneas del dibujo; si no hay arte, una única línea con el nombre
     */
    public static List<String> lineas(String nombre) {
        return ARTE.getOrDefault(nombre, List.of(nombre));
    }
}
