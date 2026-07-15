package com.tuio.juegoxturnos.combate;

import com.tuio.juegoxturnos.ia.EstrategiaCPU;
import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.ResultadoAtaque;
import com.tuio.juegoxturnos.ui.Colores;
import com.tuio.juegoxturnos.ui.Consola;
import com.tuio.juegoxturnos.util.Aleatorio;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Orquesta un combate por turnos entre el jugador y la CPU hasta que uno de los
 * dos se queda sin vida.
 *
 * <p>El jugador siempre abre el turno; a continuación responde la CPU. Al final
 * de cada acción, el personaje que atacó regenera algo de maná, lo que regula el
 * uso de los ataques especiales.
 */
@RequiredArgsConstructor
public final class Combate {

    private final Personaje jugador;
    private final Personaje cpu;
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
            if (!cpu.estaVivo()) {
                break;
            }

            turnoCpu();
            numeroTurno++;
        }

        return anunciarGanador();
    }

    /** Pide al jugador un ataque válido y lo ejecuta contra la CPU. */
    private void turnoJugador() {
        List<Ataque> ataques = jugador.getAtaques();
        mostrarMenuAtaques(ataques);

        Ataque elegido;
        while (true) {
            int opcion = consola.leerOpcion("Elige tu ataque:", 1, ataques.size());
            elegido = ataques.get(opcion - 1);
            if (jugador.puedeUsar(elegido)) {
                break;
            }
            consola.linea(Colores.pintar(
                    "No tienes maná suficiente para " + elegido + ". Elige otro ataque.", Colores.ROJO));
        }

        ResultadoAtaque resultado = jugador.atacar(elegido, cpu, aleatorio);
        narrarAtaque(jugador, cpu, resultado);
    }

    /** La CPU decide su ataque mediante la IA y lo ejecuta contra el jugador. */
    private void turnoCpu() {
        consola.linea();
        Ataque elegido = ia.elegirAtaque(cpu, jugador);
        ResultadoAtaque resultado = cpu.atacar(elegido, jugador, aleatorio);
        narrarAtaque(cpu, jugador, resultado);
    }

    /** Muestra los ataques disponibles del jugador con su daño y costo. */
    private void mostrarMenuAtaques(List<Ataque> ataques) {
        consola.linea();
        consola.linea(Colores.pintar("Tus ataques:", Colores.NEGRITA));
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
    }

    /** Narra el resultado de un ataque, resaltando críticos y especiales. */
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

        // Regenera el maná del atacante al terminar su acción.
        atacante.regenerarMana();
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
