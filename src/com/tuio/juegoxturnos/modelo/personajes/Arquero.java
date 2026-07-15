package com.tuio.juegoxturnos.modelo.personajes;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;

import java.util.List;

/**
 * Arquero: especialista en ataques rápidos y precisos.
 *
 * <p>Ofrece el daño más constante del juego y una probabilidad de crítico algo
 * superior a la media. Su especial "Flecha Perforante" atraviesa la armadura
 * enemiga.
 */
public final class Arquero extends Personaje {

    private static final int VIDA = 110;
    private static final int MANA_MAXIMO = 100;
    private static final int MANA_INICIAL = 60;
    private static final int REGENERACION_MANA = 25;
    private static final double PROB_CRITICO = 0.15;

    public Arquero() {
        super("Arquero", VIDA, MANA_MAXIMO, MANA_INICIAL, REGENERACION_MANA, PROB_CRITICO,
                List.of(
                        new Ataque("Flecha Precisa", 18, 23, 0, false),
                        new Ataque("Triple Disparo", 20, 26, 0, false),
                        new Ataque("Flecha Perforante", 28, 35, 50, true)
                ));
    }
}
