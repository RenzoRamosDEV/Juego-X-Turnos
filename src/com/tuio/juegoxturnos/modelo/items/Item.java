package com.tuio.juegoxturnos.modelo.items;

import com.tuio.juegoxturnos.modelo.Personaje;

/**
 * Objeto consumible que un personaje puede usar en su turno en lugar de atacar.
 */
public interface Item {

    /** Nombre visible del objeto. */
    String getNombre();

    /** Descripción breve de lo que hace el objeto. */
    String getDescripcion();

    /**
     * Usa el objeto sobre su portador.
     *
     * @param usuario personaje que consume el objeto
     * @return un mensaje describiendo el efecto, para narrarlo
     */
    String usar(Personaje usuario);
}
