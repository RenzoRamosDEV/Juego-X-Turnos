package com.tuio.juegoxturnos.combate;

import com.tuio.juegoxturnos.ia.EstrategiaCPU;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.items.Inventario;
import lombok.RequiredArgsConstructor;

/**
 * Controlador que delega la decisión del turno en la {@link EstrategiaCPU}.
 */
@RequiredArgsConstructor
public final class ControladorCpu implements Controlador {

    private final EstrategiaCPU estrategia;

    @Override
    public AccionTurno decidir(String nombreActor, Personaje actor, Personaje rival, Inventario inventario) {
        return estrategia.decidir(actor, rival, inventario);
    }
}
