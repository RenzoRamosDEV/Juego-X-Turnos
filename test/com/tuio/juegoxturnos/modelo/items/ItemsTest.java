package com.tuio.juegoxturnos.modelo.items;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.PersonajeDePrueba;
import com.tuio.juegoxturnos.modelo.efectos.Efectos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ItemsTest {

    private Personaje personaje(int vida) {
        return new PersonajeDePrueba("Prueba", vida == 0 ? 100 : vida, 100, 60, 25, 0.0,
                List.of(new Ataque("Golpe", 10, 10, 0, false)));
    }

    @Test
    @DisplayName("la poción cura sin superar la vida máxima")
    void pocionCuraConTope() {
        Personaje p = personaje(100);
        p.recibirDanio(50); // vida 50
        new PocionVida().usar(p);
        assertEquals(85, p.getVida()); // 50 + 35

        p.curar(1000);
        assertEquals(100, p.getVida()); // no supera el máximo
    }

    @Test
    @DisplayName("el escudo absorbe daño antes que la vida")
    void escudoAbsorbeDanio() {
        Personaje p = personaje(100);
        new Escudo().usar(p); // +30 de escudo
        assertEquals(30, p.getEscudo());

        p.recibirDanio(20); // absorbido por el escudo
        assertEquals(100, p.getVida());
        assertEquals(10, p.getEscudo());

        p.recibirDanio(25); // 10 al escudo, 15 a la vida
        assertEquals(0, p.getEscudo());
        assertEquals(85, p.getVida());
    }

    @Test
    @DisplayName("el antídoto elimina todos los efectos de estado")
    void antidotoLimpiaEfectos() {
        Personaje p = personaje(100);
        p.aplicarEfecto(Efectos.veneno());
        p.aplicarEfecto(Efectos.quemadura());

        new Antidoto().usar(p);
        assertFalse(p.tieneEfectos());
    }
}
