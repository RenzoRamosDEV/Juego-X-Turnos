package com.tuio.juegoxturnos.modelo.efectos;

/**
 * Fábrica de los efectos de estado del juego. Centraliza los valores (daño y
 * duración) de cada efecto para poder afinar el balance en un solo sitio.
 */
public final class Efectos {

    private Efectos() {
        // Clase de utilidad: no se instancia.
    }

    /** Veneno del Asesino: daño moderado durante 3 turnos. */
    public static EfectoEstado veneno() {
        return new DanioPorTurno("Veneno", 6, 3);
    }

    /** Quemadura del Mago: el daño por turno más alto, durante 3 turnos. */
    public static EfectoEstado quemadura() {
        return new DanioPorTurno("Quemadura", 7, 3);
    }

    /** Sangrado del Arquero: daño durante 2 turnos. */
    public static EfectoEstado sangrado() {
        return new DanioPorTurno("Sangrado", 6, 2);
    }

    /** Aturdimiento del Guerrero: hace perder 1 turno. */
    public static EfectoEstado aturdimiento() {
        return new Aturdimiento(1);
    }
}
