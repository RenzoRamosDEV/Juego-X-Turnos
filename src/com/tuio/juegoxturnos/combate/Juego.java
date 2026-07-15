package com.tuio.juegoxturnos.combate;

import com.tuio.juegoxturnos.ia.EstrategiaCPU;
import com.tuio.juegoxturnos.modelo.Personaje;
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
 * Punto de entrada de la lógica del juego: da la bienvenida, gestiona la
 * selección de personaje, lanza los combates contra la CPU y permite volver a
 * jugar.
 *
 * <p>Los personajes se crean mediante fábricas ({@link Supplier}) para obtener
 * siempre una instancia nueva y con la vida al máximo en cada combate.
 */
public final class Juego {

    /** Catálogo de personajes seleccionables, cada uno con su fábrica. */
    private static final List<Supplier<Personaje>> CATALOGO = List.of(
            Guerrero::new,
            Mago::new,
            Arquero::new,
            Asesino::new
    );

    private final Consola consola;
    private final Aleatorio aleatorio;
    private final EstrategiaCPU ia;

    public Juego(Consola consola, Aleatorio aleatorio) {
        this.consola = consola;
        this.aleatorio = aleatorio;
        this.ia = new EstrategiaCPU(aleatorio);
    }

    /** Ejecuta el bucle principal del juego hasta que el jugador decide salir. */
    public void iniciar() {
        mostrarBienvenida();

        boolean seguirJugando = true;
        while (seguirJugando) {
            Personaje jugador = elegirPersonaje();
            Personaje cpu = elegirRivalCpu();

            consola.linea();
            consola.linea("Tu rival será: " + Colores.pintar(cpu.getNombre(), Colores.ROJO));

            new Combate(jugador, cpu, consola, ia, aleatorio).iniciar();

            seguirJugando = preguntarSiJugarDeNuevo();
        }

        consola.titulo("¡Gracias por jugar!");
    }

    private void mostrarBienvenida() {
        consola.titulo("JUEGO POR TURNOS");
        consola.linea("Elige a tu personaje y derrota a la CPU en un duelo por turnos.");
        consola.linea(Colores.pintar(
                "Cada personaje tiene 3 ataques; el especial gasta maná pero pega más fuerte.", Colores.GRIS));
    }

    /** Muestra el catálogo y devuelve una instancia nueva del personaje elegido. */
    private Personaje elegirPersonaje() {
        consola.titulo("Elige tu personaje");
        List<Personaje> muestra = CATALOGO.stream().map(Supplier::get).toList();
        for (int i = 0; i < muestra.size(); i++) {
            Personaje p = muestra.get(i);
            consola.linea(String.format("  %d) %-9s vida %3d   crítico %2.0f%%   especial: %s",
                    i + 1, p.getNombre(), p.getVidaMaxima(),
                    p.getProbabilidadCritico() * 100, p.getAtaqueEspecial().getNombre()));
        }

        int opcion = consola.leerOpcion("Tu elección:", 1, CATALOGO.size());
        return CATALOGO.get(opcion - 1).get();
    }

    /** La CPU recibe un personaje al azar del catálogo. */
    private Personaje elegirRivalCpu() {
        return aleatorio.elemento(CATALOGO).get();
    }

    private boolean preguntarSiJugarDeNuevo() {
        consola.linea();
        int opcion = consola.leerOpcion("¿Jugar otra vez? (1 = Sí, 2 = No):", 1, 2);
        return opcion == 1;
    }
}
