package com.tuio.juegoxturnos.modelo.items;

import com.tuio.juegoxturnos.modelo.Personaje;

/**
 * Antídoto que elimina todos los efectos de estado activos del personaje
 * (veneno, quemadura, sangrado, aturdimiento...).
 */
public final class Antidoto implements Item {

    @Override
    public String getNombre() {
        return "Antídoto";
    }

    @Override
    public String getDescripcion() {
        return "Elimina todos los efectos de estado activos";
    }

    @Override
    public String usar(Personaje usuario) {
        usuario.limpiarEfectos();
        return usuario.getNombre() + " usa un Antídoto y se libra de todos sus efectos de estado.";
    }
}
