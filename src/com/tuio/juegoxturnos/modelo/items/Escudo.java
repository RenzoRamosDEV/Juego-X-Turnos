package com.tuio.juegoxturnos.modelo.items;

import com.tuio.juegoxturnos.modelo.Personaje;

/**
 * Escudo que otorga puntos de protección que absorben daño antes que la vida.
 */
public final class Escudo implements Item {

    /** Puntos de escudo que concede. */
    public static final int PUNTOS = 30;

    @Override
    public String getNombre() {
        return "Escudo";
    }

    @Override
    public String getDescripcion() {
        return "Absorbe los próximos " + PUNTOS + " puntos de daño";
    }

    @Override
    public String usar(Personaje usuario) {
        usuario.anadirEscudo(PUNTOS);
        return usuario.getNombre() + " levanta un Escudo que absorbe " + PUNTOS + " de daño.";
    }
}
