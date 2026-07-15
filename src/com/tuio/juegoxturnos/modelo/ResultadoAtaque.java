package com.tuio.juegoxturnos.modelo;

/**
 * Resultado inmutable de ejecutar un {@link Ataque}.
 *
 * <p>Reúne toda la información de un golpe para que la capa de interfaz pueda
 * narrarlo sin tener que recalcular nada.
 *
 * @param ataque  ataque que se ejecutó
 * @param danio   daño final infligido (ya incluye el bono por crítico)
 * @param critico {@code true} si el golpe fue crítico
 */
public record ResultadoAtaque(Ataque ataque, int danio, boolean critico) {
}
