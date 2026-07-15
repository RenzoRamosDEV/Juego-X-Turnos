package com.tuio.juegoxturnos.modelo.efectos;

import com.tuio.juegoxturnos.modelo.Personaje;
import lombok.Getter;

/**
 * Efecto de estado que un personaje arrastra durante varios turnos (veneno,
 * quemadura, sangrado, aturdimiento, ...).
 *
 * <p>Al inicio de cada turno del personaje afectado se aplica el efecto y se
 * descuenta un turno de duración. Por defecto un efecto no hace daño ni impide
 * actuar; cada subclase redefine lo que corresponda.
 */
@Getter
public abstract class EfectoEstado {

    private final String nombre;
    protected int turnosRestantes;

    protected EfectoEstado(String nombre, int turnosRestantes) {
        if (turnosRestantes <= 0) {
            throw new IllegalArgumentException("La duración del efecto debe ser positiva");
        }
        this.nombre = nombre;
        this.turnosRestantes = turnosRestantes;
    }

    /**
     * Aplica el efecto al portador en su turno.
     *
     * @param portador personaje afectado
     * @return daño infligido por el efecto este turno (0 si no hace daño)
     */
    public int aplicarPorTurno(Personaje portador) {
        return 0;
    }

    /** @return {@code true} si el efecto impide actuar este turno (aturdimiento). */
    public boolean impideActuar() {
        return false;
    }

    /** Descuenta un turno de duración. */
    public void reducirDuracion() {
        if (turnosRestantes > 0) {
            turnosRestantes--;
        }
    }

    /** @return {@code true} mientras al efecto le queden turnos. */
    public boolean activo() {
        return turnosRestantes > 0;
    }
}
