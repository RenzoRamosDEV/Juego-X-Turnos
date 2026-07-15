package com.tuio.juegoxturnos.modelo.efectos;

/**
 * Resultado inmutable de aplicar un efecto de estado en un turno. Permite a la
 * interfaz narrar lo ocurrido sin conocer la implementación de cada efecto.
 *
 * @param nombre nombre del efecto (p. ej. "Veneno")
 * @param danio  daño infligido este turno (0 si el efecto no hace daño)
 * @param aturde {@code true} si el efecto impidió actuar al portador
 */
public record EfectoAplicado(String nombre, int danio, boolean aturde) {
}
