package com.tuio.juegoxturnos.util;

import java.util.List;
import java.util.Random;

/**
 * Encapsula la generación de números aleatorios del juego.
 *
 * <p>Centralizar la aleatoriedad en una sola clase facilita las pruebas
 * (se puede fijar una semilla) y evita crear instancias de {@link Random}
 * dispersas por todo el código.
 */
public final class Aleatorio {

    private final Random random;

    /** Crea un generador con semilla arbitraria (partidas no reproducibles). */
    public Aleatorio() {
        this.random = new Random();
    }

    /** Crea un generador con semilla fija, útil para pruebas deterministas. */
    public Aleatorio(long semilla) {
        this.random = new Random(semilla);
    }

    /**
     * Devuelve un entero aleatorio dentro del rango indicado, ambos incluidos.
     *
     * @param min valor mínimo (incluido)
     * @param max valor máximo (incluido)
     * @return entero en el intervalo [min, max]
     */
    public int entreInclusive(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min no puede ser mayor que max");
        }
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Lanza una probabilidad.
     *
     * @param probabilidad valor entre 0.0 y 1.0
     * @return {@code true} con la probabilidad indicada
     */
    public boolean ocurre(double probabilidad) {
        return random.nextDouble() < probabilidad;
    }

    /** Devuelve un elemento aleatorio de la lista. */
    public <T> T elemento(List<T> elementos) {
        return elementos.get(random.nextInt(elementos.size()));
    }
}
