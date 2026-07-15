package com.tuio.juegoxturnos.combate;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.items.Item;

/**
 * Acción que un combatiente realiza en su turno: atacar con uno de sus ataques
 * o usar un objeto del inventario.
 */
public sealed interface AccionTurno {

    /** Ejecutar un ataque contra el rival. */
    record Atacar(Ataque ataque) implements AccionTurno {
    }

    /** Consumir un objeto del inventario. */
    record UsarObjeto(Item item) implements AccionTurno {
    }
}
