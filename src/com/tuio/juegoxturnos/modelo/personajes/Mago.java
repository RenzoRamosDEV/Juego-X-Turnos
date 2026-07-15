package com.tuio.juegoxturnos.modelo.personajes;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.efectos.Efectos;

import java.util.List;

/**
 * Mago: especialista en magia ofensiva.
 *
 * <p>Tiene la vida más baja, pero su especial "Meteorito Mágico" es el ataque
 * con mayor daño potencial del juego y prende al enemigo con una quemadura que
 * lo daña durante varios turnos.
 */
public final class Mago extends Personaje {

    private static final int VIDA = 100;
    private static final int MANA_MAXIMO = 100;
    private static final int MANA_INICIAL = 60;
    private static final int REGENERACION_MANA = 25;
    private static final double PROB_CRITICO = 0.10;

    public Mago() {
        super("Mago", VIDA, MANA_MAXIMO, MANA_INICIAL, REGENERACION_MANA, PROB_CRITICO,
                List.of(
                        new Ataque("Bola de Fuego", 18, 24, 0, false),
                        new Ataque("Rayo Arcano", 22, 28, 0, false),
                        new Ataque("Meteorito Mágico", 32, 40, 50, true, Efectos::quemadura, 1.0)
                ));
    }
}
