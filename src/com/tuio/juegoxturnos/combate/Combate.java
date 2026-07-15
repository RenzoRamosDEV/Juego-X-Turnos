package com.tuio.juegoxturnos.combate;

import com.tuio.juegoxturnos.ia.EstrategiaCPU;
import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.ResultadoAtaque;
import com.tuio.juegoxturnos.modelo.efectos.EfectoAplicado;
import com.tuio.juegoxturnos.modelo.items.Inventario;
import com.tuio.juegoxturnos.modelo.items.Item;
import com.tuio.juegoxturnos.ui.Colores;
import com.tuio.juegoxturnos.ui.Consola;
import com.tuio.juegoxturnos.util.Aleatorio;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Orquesta un combate por turnos entre el jugador y la CPU hasta que uno de los
 * dos se queda sin vida.
 *
 * <p>El jugador siempre abre el turno; a continuación responde la CPU. Cada
 * turno comienza procesando los efectos de estado del combatiente (veneno,
 * quemadura, aturdimiento...) y, salvo que quede aturdido, este puede atacar o
 * usar un objeto de su inventario. Al final del turno regenera algo de maná.
 */
@RequiredArgsConstructor
public final class Combate {

    private final Personaje jugador;
    private final Personaje cpu;
    private final Inventario inventarioJugador;
    private final Inventario inventarioCpu;
    private final Consola consola;
    private final EstrategiaCPU ia;
    private final Aleatorio aleatorio;

    /**
     * Ejecuta el combate completo.
     *
     * @return el personaje ganador
     */
    public Personaje iniciar() {
        consola.titulo(jugador.getNombre() + "  vs  " + cpu.getNombre());
        int numeroTurno = 1;

        while (jugador.estaVivo() && cpu.estaVivo()) {
            consola.linea();
            consola.linea(Colores.pintar("· Turno " + numeroTurno + " ·", Colores.GRIS));
            consola.mostrarEstado(jugador, cpu);

            turnoJugador();
            if (!jugador.estaVivo() || !cpu.estaVivo()) {
                break;
            }

            turnoCpu();
            numeroTurno++;
        }

        return anunciarGanador();
    }

    /** Turno del jugador: procesa efectos y, si puede actuar, ejecuta su acción. */
    private void turnoJugador() {
        if (iniciarTurno(jugador)) {
            AccionTurno accion = pedirAccionJugador();
            ejecutar(jugador, cpu, inventarioJugador, accion);
        }
        if (jugador.estaVivo()) {
            jugador.regenerarMana();
        }
    }

    /** Turno de la CPU: procesa efectos y, si puede actuar, decide y ejecuta. */
    private void turnoCpu() {
        if (iniciarTurno(cpu)) {
            consola.linea();
            AccionTurno accion = ia.decidir(cpu, jugador, inventarioCpu);
            ejecutar(cpu, jugador, inventarioCpu, accion);
        }
        if (cpu.estaVivo()) {
            cpu.regenerarMana();
        }
    }

    /**
     * Procesa los efectos de estado del combatiente al inicio de su turno.
     *
     * @return {@code true} si puede actuar; {@code false} si murió o quedó aturdido
     */
    private boolean iniciarTurno(Personaje actor) {
        List<EfectoAplicado> aplicados = actor.procesarInicioTurno();
        boolean aturdido = false;
        for (EfectoAplicado efecto : aplicados) {
            if (efecto.danio() > 0) {
                consola.narrar(actor.getNombre() + " sufre "
                        + Colores.pintar(efecto.danio() + " de daño", Colores.ROJO)
                        + " por " + efecto.nombre() + ".");
            }
            if (efecto.aturde()) {
                aturdido = true;
            }
        }
        if (!actor.estaVivo()) {
            consola.narrar(Colores.pintar(actor.getNombre() + " cae por sus efectos de estado.", Colores.ROJO));
            return false;
        }
        if (aturdido) {
            consola.narrar(Colores.pintar(actor.getNombre() + " está aturdido y pierde el turno.", Colores.AMARILLO));
            return false;
        }
        return true;
    }

    /** Ejecuta la acción elegida (atacar o usar un objeto). */
    private void ejecutar(Personaje actor, Personaje objetivo, Inventario inventario, AccionTurno accion) {
        if (accion instanceof AccionTurno.Atacar atacar) {
            ResultadoAtaque resultado = actor.atacar(atacar.ataque(), objetivo, aleatorio);
            narrarAtaque(actor, objetivo, resultado);
        } else if (accion instanceof AccionTurno.UsarObjeto usarObjeto) {
            String mensaje = inventario.usar(usarObjeto.item(), actor);
            consola.narrar(Colores.pintar(mensaje, Colores.CIAN));
        }
    }

    /** Pide al jugador que elija atacar o usar un objeto, validando la opción. */
    private AccionTurno pedirAccionJugador() {
        List<Ataque> ataques = jugador.getAtaques();
        boolean hayObjetos = !inventarioJugador.estaVacio();

        while (true) {
            mostrarMenuAcciones(ataques, hayObjetos);
            int maxOpcion = ataques.size() + (hayObjetos ? 1 : 0);
            int opcion = consola.leerOpcion("Elige tu acción:", 1, maxOpcion);

            if (opcion <= ataques.size()) {
                Ataque elegido = ataques.get(opcion - 1);
                if (jugador.puedeUsar(elegido)) {
                    return new AccionTurno.Atacar(elegido);
                }
                consola.linea(Colores.pintar(
                        "No tienes maná suficiente para " + elegido.getNombre() + ".", Colores.ROJO));
            } else {
                Item objeto = pedirObjeto();
                if (objeto != null) {
                    return new AccionTurno.UsarObjeto(objeto);
                }
            }
        }
    }

    /** Muestra el menú de objetos y devuelve el elegido, o {@code null} si se cancela. */
    private Item pedirObjeto() {
        List<Item> objetos = inventarioJugador.disponibles();
        consola.linea();
        consola.linea(Colores.pintar("Tus objetos:", Colores.NEGRITA));
        for (int i = 0; i < objetos.size(); i++) {
            Item objeto = objetos.get(i);
            consola.linea(String.format("  %d) %-16s x%d  %s",
                    i + 1, objeto.getNombre(), inventarioJugador.cantidad(objeto), objeto.getDescripcion()));
        }
        int opcion = consola.leerOpcion("Elige un objeto (0 = volver):", 0, objetos.size());
        return opcion == 0 ? null : objetos.get(opcion - 1);
    }

    /** Muestra los ataques del jugador y, si tiene, la opción de usar un objeto. */
    private void mostrarMenuAcciones(List<Ataque> ataques, boolean hayObjetos) {
        consola.linea();
        consola.linea(Colores.pintar("Tus acciones:", Colores.NEGRITA));
        for (int i = 0; i < ataques.size(); i++) {
            Ataque ataque = ataques.get(i);
            String etiqueta = String.format("  %d) %-18s daño %2d-%2d",
                    i + 1, ataque.getNombre(), ataque.getDanioMin(), ataque.getDanioMax());
            if (ataque.isEspecial()) {
                etiqueta += Colores.pintar("  [ESPECIAL, maná " + ataque.getCostoMana() + "]", Colores.MAGENTA);
            }
            if (!jugador.puedeUsar(ataque)) {
                etiqueta += Colores.pintar("  (sin maná)", Colores.ROJO);
            }
            consola.linea(etiqueta);
        }
        if (hayObjetos) {
            consola.linea(String.format("  %d) %s", ataques.size() + 1,
                    Colores.pintar("Usar un objeto", Colores.CIAN)));
        }
    }

    /** Narra el resultado de un ataque, resaltando críticos, especiales y efectos. */
    private void narrarAtaque(Personaje atacante, Personaje defensor, ResultadoAtaque resultado) {
        String color = atacante == jugador ? Colores.VERDE : Colores.ROJO;
        StringBuilder mensaje = new StringBuilder();
        mensaje.append(Colores.pintar(atacante.getNombre(), color))
                .append(" usa ")
                .append(Colores.pintar(resultado.ataque().getNombre(),
                        resultado.ataque().isEspecial() ? Colores.MAGENTA : Colores.CIAN));

        if (resultado.critico()) {
            mensaje.append(Colores.pintar("  ¡CRÍTICO!", Colores.AMARILLO + Colores.NEGRITA));
        }

        mensaje.append(" e inflige ")
                .append(Colores.pintar(resultado.danio() + " de daño", Colores.NEGRITA))
                .append(" a ")
                .append(defensor.getNombre())
                .append(".");

        consola.narrar(mensaje.toString());

        if (resultado.aplicoEfecto()) {
            consola.narrar(Colores.pintar(
                    "  " + defensor.getNombre() + " queda afectado por "
                            + resultado.efectoAplicado().getNombre() + ".", Colores.MAGENTA));
        }
    }

    /** Muestra el estado final y devuelve al ganador. */
    private Personaje anunciarGanador() {
        Personaje ganador = jugador.estaVivo() ? jugador : cpu;
        consola.mostrarEstado(jugador, cpu);
        consola.titulo("¡" + ganador.getNombre() + " gana el combate!");
        boolean ganaJugador = ganador == jugador;
        consola.linea(Colores.pintar(
                ganaJugador ? "¡Victoria! Tu estrategia funcionó." : "Derrota... ¡inténtalo de nuevo!",
                ganaJugador ? Colores.VERDE : Colores.ROJO));
        return ganador;
    }
}
