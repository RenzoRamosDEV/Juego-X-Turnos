package com.tuio.juegoxturnos.modelo.efectos;

import com.tuio.juegoxturnos.modelo.Personaje;

/**
 * Efecto que inflige una cantidad fija de daño al portador en cada uno de sus
 * turnos. Sirve para venenos, quemaduras y sangrados.
 */
public final class DanioPorTurno extends EfectoEstado {

    private final int danioPorTurno;

    public DanioPorTurno(String nombre, int danioPorTurno, int turnosRestantes) {
        super(nombre, turnosRestantes);
        if (danioPorTurno <= 0) {
            throw new IllegalArgumentException("El daño por turno debe ser positivo");
        }
        this.danioPorTurno = danioPorTurno;
    }

    @Override
    public int aplicarPorTurno(Personaje portador) {
        portador.recibirDanio(danioPorTurno);
        return danioPorTurno;
    }
}
