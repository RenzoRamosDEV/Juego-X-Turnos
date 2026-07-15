package com.tuio.juegoxturnos.combate;

import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.items.Inventario;

/**
 * Decide la acción que un combatiente realiza en su turno. Permite que un lado
 * del combate lo controle un humano o la CPU sin que el motor de combate
 * conozca la diferencia.
 */
public interface Controlador {

    /**
     * Decide la acción del turno.
     *
     * @param nombreActor nombre visible del actor (para los menús)
     * @param actor       personaje que actúa
     * @param rival       personaje contrario
     * @param inventario  objetos disponibles del actor
     * @return la acción elegida
     */
    AccionTurno decidir(String nombreActor, Personaje actor, Personaje rival, Inventario inventario);
}
