package com.tuio.juegoxturnos.modelo;

import com.tuio.juegoxturnos.modelo.efectos.EfectoEstado;

/**
 * Resultado inmutable de ejecutar un {@link Ataque}.
 *
 * <p>Reúne toda la información de un golpe para que la capa de interfaz pueda
 * narrarlo sin tener que recalcular nada.
 *
 * @param ataque         ataque que se ejecutó
 * @param danio          daño final infligido (ya incluye el bono por crítico)
 * @param critico        {@code true} si el golpe fue crítico
 * @param efectoAplicado efecto de estado infligido al objetivo, o {@code null}
 */
public record ResultadoAtaque(Ataque ataque, int danio, boolean critico, EfectoEstado efectoAplicado) {

    /** @return {@code true} si el ataque aplicó un efecto de estado. */
    public boolean aplicoEfecto() {
        return efectoAplicado != null;
    }
}
