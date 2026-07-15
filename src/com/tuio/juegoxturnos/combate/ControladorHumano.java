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
        consola.subtitulo("Acciones de " + nombreActor);
        for (int i = 0; i < ataques.size(); i++) {
            Ataque ataque = ataques.get(i);
            String etiqueta = opcion(i + 1) + String.format(" %-18s ", ataque.getNombre())
                    + Colores.pintar(String.format("daño %2d-%2d", ataque.getDanioMin(), ataque.getDanioMax()),
                    Colores.GRIS);
            if (ataque.isEspecial()) {
                etiqueta += Colores.pintar("  ✦ ESPECIAL (maná " + ataque.getCostoMana() + ")", Colores.MAGENTA_CLARO);
            }
            if (!actor.puedeUsar(ataque)) {
                etiqueta += Colores.pintar("  (sin maná)", Colores.ROJO);
            }
            consola.linea(etiqueta);
        }
        consola.linea(opcion(opcionDefender) + Colores.pintar(" Defender", Colores.AZUL_CLARO)
                + Colores.pintar("  (reduce el daño recibido)", Colores.GRIS));
        if (hayObjetos) {
            consola.linea(opcion(opcionObjetos) + Colores.pintar(" Usar un objeto", Colores.CIAN_CLARO));
        }
    }

    /** Formatea el número de una opción del menú, p. ej. "  [1]". */
    private String opcion(int numero) {
        return Colores.pintar(String.format("  [%d]", numero), Colores.CIAN_CLARO + Colores.NEGRITA);
    }

    /** Muestra los objetos y devuelve el elegido, o {@code null} si se cancela. */
    private Item pedirObjeto(Inventario inventario) {
        List<Item> objetos = inventario.disponibles();
        consola.subtitulo("Objetos");
        for (int i = 0; i < objetos.size(); i++) {
            Item objeto = objetos.get(i);
            consola.linea(opcion(i + 1) + String.format(" %-16s ", objeto.getNombre())
                    + Colores.pintar("x" + inventario.cantidad(objeto), Colores.AMARILLO_CLARO)
                    + Colores.pintar("  " + objeto.getDescripcion(), Colores.GRIS));
        }
        int opcion = consola.leerOpcion("Elige un objeto (0 = volver):", 0, objetos.size());
        return opcion == 0 ? null : objetos.get(opcion - 1);
    }
}
