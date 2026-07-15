package com.tuio.juegoxturnos;

import com.tuio.juegoxturnos.combate.Juego;
import com.tuio.juegoxturnos.ui.Consola;
import com.tuio.juegoxturnos.util.Aleatorio;

import java.util.Scanner;

/**
 * Punto de entrada de la aplicación. Se encarga únicamente de construir las
 * dependencias (consola, aleatoriedad) y arrancar el {@link Juego}.
 */
public final class Main {

    /** Pausa entre mensajes de combate, en milisegundos, para dar ritmo. */
    private static final long PAUSA_MS = 600;

    private Main() {
        // No instanciable.
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Consola consola = new Consola(scanner, System.out, PAUSA_MS);
            Aleatorio aleatorio = new Aleatorio();
            new Juego(consola, aleatorio).iniciar();
        }
    }
}
