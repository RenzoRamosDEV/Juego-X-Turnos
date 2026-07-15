package com.tuio.juegoxturnos.combate;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.items.Inventario;
import com.tuio.juegoxturnos.modelo.items.Item;
import com.tuio.juegoxturnos.ui.Colores;
import com.tuio.juegoxturnos.ui.Consola;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Controlador que pide la acción del turno a una persona a través de la consola:
 * elegir un ataque, defenderse o usar un objeto.
 */
@RequiredArgsConstructor
public final class ControladorHumano implements Controlador {

    private final Consola consola;

    @Override
    public AccionTurno decidir(String nombreActor, Personaje actor, Personaje rival, Inventario inventario) {
        List<Ataque> ataques = actor.getAtaques();
        boolean hayObjetos = !inventario.estaVacio();
        int opcionDefender = ataques.size() + 1;
        int opcionObjetos = ataques.size() + 2;
        int maxOpcion = hayObjetos ? opcionObjetos : opcionDefender;

        while (true) {
            mostrarMenu(nombreActor, ataques, actor, hayObjetos, opcionDefender, opcionObjetos);
            int opcion = consola.leerOpcion("Elige la acción de " + nombreActor + ":", 1, maxOpcion);

            if (opcion <= ataques.size()) {
                Ataque elegido = ataques.get(opcion - 1);
                if (actor.puedeUsar(elegido)) {
                    return new AccionTurno.Atacar(elegido);
                }
                consola.linea(Colores.pintar(
                        "No hay maná suficiente para " + elegido.getNombre() + ".", Colores.ROJO));
            } else if (opcion == opcionDefender) {
                return new AccionTurno.Defender();
            } else {
                Item objeto = pedirObjeto(inventario);
                if (objeto != null) {
                    return new AccionTurno.UsarObjeto(objeto);
                }
            }
        }
    }

    private void mostrarMenu(String nombreActor, List<Ataque> ataques, Personaje actor, boolean hayObjetos,
                             int opcionDefender, int opcionObjetos) {
        consola.linea();
        consola.linea(Colores.pintar("Acciones de " + nombreActor + ":", Colores.NEGRITA));
        for (int i = 0; i < ataques.size(); i++) {
            Ataque ataque = ataques.get(i);
            String etiqueta = String.format("  %d) %-18s daño %2d-%2d",
                    i + 1, ataque.getNombre(), ataque.getDanioMin(), ataque.getDanioMax());
            if (ataque.isEspecial()) {
                etiqueta += Colores.pintar("  [ESPECIAL, maná " + ataque.getCostoMana() + "]", Colores.MAGENTA);
            }
            if (!actor.puedeUsar(ataque)) {
                etiqueta += Colores.pintar("  (sin maná)", Colores.ROJO);
            }
            consola.linea(etiqueta);
        }
        consola.linea(String.format("  %d) %s", opcionDefender,
                Colores.pintar("Defender (reduce el daño recibido)", Colores.AZUL)));
        if (hayObjetos) {
            consola.linea(String.format("  %d) %s", opcionObjetos,
                    Colores.pintar("Usar un objeto", Colores.CIAN)));
        }
    }

    /** Muestra los objetos y devuelve el elegido, o {@code null} si se cancela. */
    private Item pedirObjeto(Inventario inventario) {
        List<Item> objetos = inventario.disponibles();
        consola.linea();
        consola.linea(Colores.pintar("Objetos:", Colores.NEGRITA));
        for (int i = 0; i < objetos.size(); i++) {
            Item objeto = objetos.get(i);
            consola.linea(String.format("  %d) %-16s x%d  %s",
                    i + 1, objeto.getNombre(), inventario.cantidad(objeto), objeto.getDescripcion()));
        }
        int opcion = consola.leerOpcion("Elige un objeto (0 = volver):", 0, objetos.size());
        return opcion == 0 ? null : objetos.get(opcion - 1);
    }
}
