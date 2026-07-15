package com.tuio.juegoxturnos.modelo.efectos;

/**
 * Efecto que impide actuar al portador mientras esté activo. No hace daño: su
 * coste para el rival es la pérdida de turnos.
 */
public final class Aturdimiento extends EfectoEstado {

    public Aturdimiento(int turnosRestantes) {
        super("Aturdimiento", turnosRestantes);
    }

    @Override
    public boolean impideActuar() {
        return activo();
    }
}
