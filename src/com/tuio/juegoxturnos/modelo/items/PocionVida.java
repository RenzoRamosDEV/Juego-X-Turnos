package com.tuio.juegoxturnos.modelo.items;

import com.tuio.juegoxturnos.modelo.Personaje;

/**
 * Poción que restaura una cantidad fija de vida al personaje que la consume.
 */
public final class PocionVida implements Item {

    /** Vida que restaura la poción. */
    public static final int CURACION = 35;

    @Override
    public String getNombre() {
        return "Poción de Vida";
    }

    @Override
    public String getDescripcion() {
        return "Restaura " + CURACION + " puntos de vida";
    }

    @Override
    public String usar(Personaje usuario) {
        int antes = usuario.getVida();
        usuario.curar(CURACION);
        int recuperado = usuario.getVida() - antes;
        return usuario.getNombre() + " bebe una Poción de Vida y recupera " + recuperado + " de vida.";
    }
}
