package com.tuio.juegoxturnos.modelo;

import com.tuio.juegoxturnos.util.Aleatorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonajeTest {

    private static final Ataque BASICO = new Ataque("Golpe", 10, 10, 0, false);
    private static final Ataque ESPECIAL = new Ataque("Especial", 10, 10, 50, true);

    private Personaje nuevoPersonaje(int vida, int manaInicial, double probCritico) {
        return new PersonajeDePrueba("Prueba", vida, 100, manaInicial, 25, probCritico,
                List.of(BASICO, ESPECIAL));
    }

    @Test
    @DisplayName("un personaje nace con la vida al máximo y vivo")
    void estadoInicial() {
        Personaje p = nuevoPersonaje(120, 60, 0.0);
        assertEquals(120, p.getVida());
        assertEquals(120, p.getVidaMaxima());
        assertTrue(p.estaVivo());
    }

    @Test
    @DisplayName("recibirDanio nunca deja la vida por debajo de cero")
    void danioNoBajaDeCero() {
        Personaje p = nuevoPersonaje(30, 60, 0.0);
        p.recibirDanio(1000);
        assertEquals(0, p.getVida());
        assertFalse(p.estaVivo());
    }

    @Test
    @DisplayName("atacar sin crítico aplica el daño base y consume el maná")
    void ataqueSinCritico() {
        Personaje atacante = nuevoPersonaje(100, 60, 0.0);
        Personaje objetivo = nuevoPersonaje(100, 60, 0.0);

        ResultadoAtaque resultado = atacante.atacar(ESPECIAL, objetivo, new Aleatorio(1));

        assertFalse(resultado.critico());
        assertEquals(10, resultado.danio());
        assertEquals(90, objetivo.getVida());
        assertEquals(10, atacante.getMana(), "60 - 50 de costo");
    }

    @Test
    @DisplayName("un golpe crítico multiplica el daño por 1.5")
    void ataqueConCritico() {
        Personaje atacante = nuevoPersonaje(100, 60, 1.0); // crítico garantizado
        Personaje objetivo = nuevoPersonaje(100, 60, 0.0);

        ResultadoAtaque resultado = atacante.atacar(BASICO, objetivo, new Aleatorio(1));

        assertTrue(resultado.critico());
        assertEquals(15, resultado.danio()); // round(10 * 1.5)
        assertEquals(85, objetivo.getVida());
    }

    @Test
    @DisplayName("no se puede atacar con un especial sin maná suficiente")
    void ataqueSinMana() {
        Personaje atacante = nuevoPersonaje(100, 40, 0.0); // 40 < costo 50
        Personaje objetivo = nuevoPersonaje(100, 60, 0.0);

        assertFalse(atacante.puedeUsar(ESPECIAL));
        assertThrows(IllegalStateException.class,
                () -> atacante.atacar(ESPECIAL, objetivo, new Aleatorio(1)));
    }

    @Test
    @DisplayName("la regeneración de maná no supera el máximo")
    void regeneracionTopeMaximo() {
        Personaje p = nuevoPersonaje(100, 90, 0.0);
        p.regenerarMana(); // 90 + 25 = 115 -> se limita a 100
        assertEquals(100, p.getMana());
    }

    @Test
    @DisplayName("getAtaqueEspecial devuelve el ataque marcado como especial")
    void obtieneEspecial() {
        Personaje p = nuevoPersonaje(100, 60, 0.0);
        assertEquals(ESPECIAL, p.getAtaqueEspecial());
        assertTrue(p.getAtaqueEspecial().isEspecial());
    }

    @Test
    @DisplayName("la lista de ataques es inmutable")
    void ataquesInmutables() {
        Personaje p = nuevoPersonaje(100, 60, 0.0);
        assertThrows(UnsupportedOperationException.class, () -> p.getAtaques().add(BASICO));
    }
}
