package com.tuio.juegoxturnos.combate;

import com.tuio.juegoxturnos.ia.EstrategiaCPU;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.items.Inventario;
import com.tuio.juegoxturnos.modelo.personajes.Arquero;
import com.tuio.juegoxturnos.modelo.personajes.Asesino;
import com.tuio.juegoxturnos.modelo.personajes.Guerrero;
import com.tuio.juegoxturnos.modelo.personajes.Mago;
import com.tuio.juegoxturnos.ui.Colores;
import com.tuio.juegoxturnos.ui.Consola;
import com.tuio.juegoxturnos.util.Aleatorio;

import java.util.List;
import java.util.function.Supplier;

/**
 * Punto de entrada de la lógica del juego: da la bienvenida, permite elegir el
 * modo (contra la CPU, dos jugadores o torneo), gestiona la selección de
 * personaje y lanza los combates.
 *
 * <p>Los personajes se crean mediante fábricas ({@link Supplier}) para obtener
 * siempre una instancia nueva y con la vida al máximo en cada combate.
 */
public final class Juego {

    /** Porcentaje de vida máxima que se recupera entre rondas del torneo. */
    private static final double CURACION_ENTRE_RONDAS = 0.40;

    /** Catálogo de personajes seleccionables, cada uno con su fábrica. */
    private static final List<Supplier<Personaje>> CATALOGO = List.of(
            Guerrero::new,
            Mago::new,
            Arquero::new,
            Asesino::new
    );

    private final Consola consola;
    private final Aleatorio aleatorio;
    private final Controlador controladorHumano;
    private final Controlador controladorCpu;

    public Juego(Consola consola, Aleatorio aleatorio) {
        this.consola = consola;
        this.aleatorio = aleatorio;
        this.controladorHumano = new ControladorHumano(consola);
        this.controladorCpu = new ControladorCpu(new EstrategiaCPU(aleatorio));
    }

    /** Ejecuta el bucle principal del juego hasta que el jugador decide salir. */
    public void iniciar() {
        mostrarBienvenida();

        boolean seguirJugando = true;
        while (seguirJugando) {
            switch (elegirModo()) {
                case 1 -> jugarContraCpu();
                case 2 -> jugarDosJugadores();
                case 3 -> jugarTorneo();
                default -> { /* imposible: leerOpcion valida el rango */ }
            }
            seguirJugando = preguntarSiJugarDeNuevo();
        }

        consola.titulo("¡Gracias por jugar!");
    }

    private void mostrarBienvenida() {
        consola.titulo("JUEGO POR TURNOS");
        consola.linea("Elige a tu personaje y vence en un duelo por turnos.");
        consola.linea(Colores.pintar(
                "Cada personaje tiene 3 ataques; el especial gasta maná, pega más fuerte y aplica un efecto.", Colores.GRIS));
        consola.linea(Colores.pintar(
                "En tu turno puedes atacar, defenderte o usar un objeto (pociones, escudo, antídoto).", Colores.GRIS));
    }

    private int elegirModo() {
        consola.titulo("Elige el modo de juego");
        consola.linea("  1) Un jugador contra la CPU");
        consola.linea("  2) Dos jugadores");
        consola.linea("  3) Torneo (sobrevive a todos los rivales)");
        return consola.leerOpcion("Tu elección:", 1, 3);
    }

    /** Modo clásico: el jugador contra un personaje aleatorio de la CPU. */
    private void jugarContraCpu() {
        Personaje jugador = elegirPersonaje("tu personaje");
        Personaje cpu = elegirRivalAleatorio();
        consola.linea();
        consola.linea("Tu rival será: " + Colores.pintar(cpu.getNombre(), Colores.ROJO));

        Combatiente combatienteJugador = combatiente(jugador, controladorHumano, Colores.VERDE, jugador.getNombre());
        Combatiente combatienteCpu = combatiente(cpu, controladorCpu, Colores.ROJO, cpu.getNombre());

        Combatiente ganador = new Combate(combatienteJugador, combatienteCpu, consola, aleatorio).iniciar();
        boolean gana = ganador == combatienteJugador;
        consola.linea(Colores.pintar(
                gana ? "¡Victoria! Tu estrategia funcionó." : "Derrota... ¡inténtalo de nuevo!",
                gana ? Colores.VERDE : Colores.ROJO));
    }

    /** Modo dos jugadores: ambos lados los controla una persona con su propio nombre. */
    private void jugarDosJugadores() {
        String nombre1 = consola.leerTexto("Jugador 1, escribe tu nombre:");
        Personaje jugador1 = elegirPersonaje("el personaje de " + nombre1);
        String nombre2 = consola.leerTexto("Jugador 2, escribe tu nombre:");
        Personaje jugador2 = elegirPersonaje("el personaje de " + nombre2);

        Combatiente combatiente1 = combatiente(jugador1, controladorHumano, Colores.VERDE, nombre1);
        Combatiente combatiente2 = combatiente(jugador2, controladorHumano, Colores.ROJO, nombre2);

        Combatiente ganador = new Combate(combatiente1, combatiente2, consola, aleatorio).iniciar();
        consola.linea(Colores.pintar(
                "¡" + ganador.getNombre() + " se lleva la victoria!", Colores.VERDE));
    }

    /** Modo torneo: el jugador se enfrenta en cadena al resto de personajes. */
    private void jugarTorneo() {
        Personaje jugador = elegirPersonaje("tu personaje");
        List<Supplier<Personaje>> rivales = CATALOGO.stream()
                .filter(fabrica -> !fabrica.get().getNombre().equals(jugador.getNombre()))
                .toList();

        int rondasGanadas = 0;
        for (int ronda = 0; ronda < rivales.size(); ronda++) {
            Personaje rival = rivales.get(ronda).get();
            consola.titulo("Ronda " + (ronda + 1) + " de " + rivales.size() + ": " + rival.getNombre());

            Combatiente combatienteJugador = combatiente(jugador, controladorHumano, Colores.VERDE, jugador.getNombre());
            Combatiente combatienteRival = combatiente(rival, controladorCpu, Colores.ROJO, rival.getNombre());
            Combatiente ganador = new Combate(combatienteJugador, combatienteRival, consola, aleatorio).iniciar();

            if (ganador != combatienteJugador) {
                consola.linea(Colores.pintar(
                        "Caíste en la ronda " + (ronda + 1) + ". Rondas superadas: " + rondasGanadas + ".", Colores.ROJO));
                return;
            }

            rondasGanadas++;
            if (ronda < rivales.size() - 1) {
                jugador.reiniciarParaRonda((int) Math.round(jugador.getVidaMaxima() * CURACION_ENTRE_RONDAS));
                consola.linea(Colores.pintar("¡Ronda superada! Recuperas fuerzas para el siguiente rival.", Colores.VERDE));
            }
        }

        consola.titulo("¡CAMPEÓN DEL TORNEO!");
        consola.linea(Colores.pintar(
                jugador.getNombre() + " ha derrotado a los " + rondasGanadas + " rivales.", Colores.VERDE));
    }

    /** Crea un combatiente con inventario por defecto para el personaje dado. */
    private Combatiente combatiente(Personaje personaje, Controlador controlador, String color, String nombre) {
        return new Combatiente(personaje, Inventario.porDefecto(), controlador, color, nombre);
    }

    /** Muestra el catálogo y devuelve una instancia nueva del personaje elegido. */
    private Personaje elegirPersonaje(String queElegir) {
        consola.titulo("Elige " + queElegir);
        List<Personaje> muestra = CATALOGO.stream().map(Supplier::get).toList();
        for (int i = 0; i < muestra.size(); i++) {
            Personaje p = muestra.get(i);
            consola.linea(String.format("  %d) %-9s vida %3d  crítico %2.0f%%  evasión %2.0f%%  especial: %s",
                    i + 1, p.getNombre(), p.getVidaMaxima(),
                    p.getProbabilidadCritico() * 100, p.getProbabilidadEvasion() * 100,
                    p.getAtaqueEspecial().getNombre()));
        }
        int opcion = consola.leerOpcion("Tu elección:", 1, CATALOGO.size());
        Personaje elegido = CATALOGO.get(opcion - 1).get();
        consola.linea();
        consola.mostrarArte(elegido, Colores.CIAN);
        consola.linea("Has elegido: " + Colores.pintar(elegido.getNombre(), Colores.CIAN));
        return elegido;
    }

    /** La CPU recibe un personaje al azar del catálogo. */
    private Personaje elegirRivalAleatorio() {
        return aleatorio.elemento(CATALOGO).get();
    }

    private boolean preguntarSiJugarDeNuevo() {
        consola.linea();
        int opcion = consola.leerOpcion("¿Jugar otra vez? (1 = Sí, 2 = No):", 1, 2);
        return opcion == 1;
    }
}
