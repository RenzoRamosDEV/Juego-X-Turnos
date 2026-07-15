package com.tuio.juegoxturnos.combate;

import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.ResultadoAtaque;
import com.tuio.juegoxturnos.modelo.efectos.EfectoAplicado;
import com.tuio.juegoxturnos.ui.Colores;
import com.tuio.juegoxturnos.ui.Consola;
import com.tuio.juegoxturnos.util.Aleatorio;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Orquesta un combate por turnos entre dos {@link Combatiente combatientes}
 * hasta que uno se queda sin vida. Cada combatiente puede estar controlado por
 * un humano o por la CPU, de modo que el mismo motor sirve para el modo contra
 * la máquina y para dos jugadores.
 *
 * <p>El combatiente A siempre abre el turno. Cada turno comienza levantando la
 * defensa previa, procesando los efectos de estado (veneno, quemadura,
 * aturdimiento...) y, salvo que quede aturdido, ejecutando su acción (atacar,
 * defenderse o usar un objeto). Al final regenera algo de maná.
 */
@RequiredArgsConstructor
public final class Combate {

    private final Combatiente a;
    private final Combatiente b;
    private final Consola consola;
    private final Aleatorio aleatorio;

    /**
     * Ejecuta el combate completo.
     *
     * @return el combatiente ganador
     */
    public Combatiente iniciar() {
        consola.titulo(a.getPersonaje().getNombre() + "  vs  " + b.getPersonaje().getNombre());
        int numeroTurno = 1;

        while (a.getPersonaje().estaVivo() && b.getPersonaje().estaVivo()) {
            consola.linea();
            consola.linea(Colores.pintar("· Turno " + numeroTurno + " ·", Colores.GRIS));
            consola.mostrarEstado(a.getPersonaje(), a.getColor(), b.getPersonaje(), b.getColor());

            jugarTurno(a, b);
            if (!a.getPersonaje().estaVivo() || !b.getPersonaje().estaVivo()) {
                break;
            }

            jugarTurno(b, a);
            numeroTurno++;
        }

        return anunciarGanador();
    }

    /** Turno de un combatiente: levanta la defensa previa, procesa efectos y actúa. */
    private void jugarTurno(Combatiente actor, Combatiente objetivo) {
        Personaje personaje = actor.getPersonaje();
        personaje.finalizarDefensa();

        if (procesarEfectos(personaje)) {
            AccionTurno accion = actor.decidirAccion(objetivo.getPersonaje());
            ejecutar(actor, objetivo, accion);
        }
        if (personaje.estaVivo()) {
            personaje.regenerarMana();
        }
    }

    /**
     * Procesa los efectos de estado al inicio del turno.
     *
     * @return {@code true} si el personaje puede actuar; {@code false} si murió o quedó aturdido
     */
    private boolean procesarEfectos(Personaje actor) {
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

    /** Ejecuta la acción elegida (atacar, defenderse o usar un objeto). */
    private void ejecutar(Combatiente actor, Combatiente objetivo, AccionTurno accion) {
        Personaje personaje = actor.getPersonaje();
        if (accion instanceof AccionTurno.Atacar atacar) {
            ResultadoAtaque resultado = personaje.atacar(atacar.ataque(), objetivo.getPersonaje(), aleatorio);
            narrarAtaque(actor, objetivo, resultado);
        } else if (accion instanceof AccionTurno.UsarObjeto usarObjeto) {
            String mensaje = actor.getInventario().usar(usarObjeto.item(), personaje);
            consola.narrar(Colores.pintar(mensaje, Colores.CIAN));
        } else if (accion instanceof AccionTurno.Defender) {
            personaje.defender();
            consola.narrar(Colores.pintar(
                    personaje.getNombre() + " se pone en guardia y reducirá el daño del próximo golpe.", Colores.AZUL));
        }
    }

    /** Narra el resultado de un ataque, resaltando esquivas, críticos, especiales y efectos. */
    private void narrarAtaque(Combatiente actor, Combatiente objetivo, ResultadoAtaque resultado) {
        Personaje atacante = actor.getPersonaje();
        Personaje defensor = objetivo.getPersonaje();

        if (resultado.fallado()) {
            consola.narrar(Colores.pintar(
                    defensor.getNombre() + " esquiva el ataque de " + atacante.getNombre() + ".", Colores.AMARILLO));
            return;
        }

        StringBuilder mensaje = new StringBuilder();
        mensaje.append(Colores.pintar(atacante.getNombre(), actor.getColor()))
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
    private Combatiente anunciarGanador() {
        Combatiente ganador = a.getPersonaje().estaVivo() ? a : b;
        consola.mostrarEstado(a.getPersonaje(), a.getColor(), b.getPersonaje(), b.getColor());
        consola.titulo("¡" + ganador.getPersonaje().getNombre() + " gana el combate!");
        return ganador;
    }
}
