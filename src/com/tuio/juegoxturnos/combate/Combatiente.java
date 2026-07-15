package com.tuio.juegoxturnos.combate;

import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.items.Inventario;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Un contendiente del combate: su personaje, su inventario, quién lo controla
 * (humano o CPU), el color con el que se narran sus acciones y el nombre visible
 * (el del jugador en el modo dos jugadores, o el de la clase en los demás).
 */
@Getter
@RequiredArgsConstructor
public final class Combatiente {

    private final Personaje personaje;
    private final Inventario inventario;
    private final Controlador controlador;
    private final String color;
    private final String nombre;

    /** Pide al controlador la acción de este turno contra el rival indicado. */
    public AccionTurno decidirAccion(Personaje rival) {
        return controlador.decidir(nombre, personaje, rival, inventario);
    }
}
