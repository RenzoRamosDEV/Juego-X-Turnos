package com.tuio.juegoxturnos.modelo.personajes;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;

import java.util.List;

/**
 * Guerrero: especialista en ataques físicos de gran fuerza.
 *
 * <p>Es el personaje con más vida del juego. Su especial, "Corte Destructor",
 * pega muy fuerte a cambio de gastar todo su maná.
 */
public final class Guerrero extends Personaje {

    private static final int VIDA = 140;
    private static final int MANA_MAXIMO = 100;
    private static final int MANA_INICIAL = 60;
    private static final int REGENERACION_MANA = 25;
    private static final double PROB_CRITICO = 0.10;

    public Guerrero() {
        super("Guerrero", VIDA, MANA_MAXIMO, MANA_INICIAL, REGENERACION_MANA, PROB_CRITICO,
                List.of(
                        new Ataque("Espadazo", 20, 25, 0, false),
                        new Ataque("Golpe Giratorio", 16, 22, 0, false),
                        new Ataque("Corte Destructor", 30, 38, 50, true)
                ));
    }
}
