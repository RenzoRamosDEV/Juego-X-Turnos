package com.tuio.juegoxturnos.modelo.personajes;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;

import java.util.List;

/**
 * Asesino: especialista en ataques críticos y velocidad.
 *
 * <p>Tiene poca vida, pero la probabilidad de crítico más alta del juego, lo
 * que convierte cualquier golpe (y sobre todo su especial "Golpe Mortal") en
 * una amenaza constante.
 */
public final class Asesino extends Personaje {

    private static final int VIDA = 105;
    private static final int MANA_MAXIMO = 100;
    private static final int MANA_INICIAL = 60;
    private static final int REGENERACION_MANA = 25;
    private static final double PROB_CRITICO = 0.25;

    public Asesino() {
        super("Asesino", VIDA, MANA_MAXIMO, MANA_INICIAL, REGENERACION_MANA, PROB_CRITICO,
                List.of(
                        new Ataque("Cuchillada", 18, 22, 0, false),
                        new Ataque("Doble Corte", 20, 26, 0, false),
                        new Ataque("Golpe Mortal", 30, 37, 50, true)
                ));
    }
}
