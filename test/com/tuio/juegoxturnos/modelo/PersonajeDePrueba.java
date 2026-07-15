package com.tuio.juegoxturnos.modelo;

import java.util.List;

/**
 * Personaje concreto mínimo para las pruebas: permite fijar cada parámetro
 * (vida, maná, probabilidad de crítico y ataques) sin depender de las clases
 * reales del juego.
 */
public class PersonajeDePrueba extends Personaje {

    public PersonajeDePrueba(String nombre, int vidaMaxima, int manaMaximo, int manaInicial,
                             int regeneracionMana, double probabilidadCritico, List<Ataque> ataques) {
        super(nombre, vidaMaxima, manaMaximo, manaInicial, regeneracionMana, probabilidadCritico, ataques);
    }

    public PersonajeDePrueba(String nombre, int vidaMaxima, int manaMaximo, int manaInicial,
                             int regeneracionMana, double probabilidadCritico,
                             double probabilidadEvasion, List<Ataque> ataques) {
        super(nombre, vidaMaxima, manaMaximo, manaInicial, regeneracionMana,
                probabilidadCritico, probabilidadEvasion, ataques);
    }
}
