package com.tuio.juegoxturnos.modelo.efectos;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.PersonajeDePrueba;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DanioPorTurnoTest {

    private Personaje personaje(int vida) {
        return new PersonajeDePrueba("Prueba", vida, 100, 60, 25, 0.0,
                List.of(new Ataque("Golpe", 10, 10, 0, false)));
    }

    @Test
    @DisplayName("aplica daño al portador y devuelve el daño infligido")
    void aplicaDanio() {
        Personaje portador = personaje(50);
        DanioPorTurno veneno = new DanioPorTurno("Veneno", 6, 3);
        assertEquals(6, veneno.aplicarPorTurno(portador));
        assertEquals(44, portador.getVida());
    }

    @Test
    @DisplayName("no impide actuar")
    void noAturde() {
        assertFalse(new DanioPorTurno("Veneno", 6, 3).impideActuar());
    }

    @Test
    @DisplayName("expira tras agotar su duración")
    void expiraTrasSuDuracion() {
        DanioPorTurno sangrado = new DanioPorTurno("Sangrado", 5, 2);
        assertTrue(sangrado.activo());
        sangrado.reducirDuracion();
        assertTrue(sangrado.activo());
        sangrado.reducirDuracion();
        assertFalse(sangrado.activo());
    }

    @Test
    @DisplayName("rechaza daño y duración no positivos")
    void validaParametros() {
        assertThrows(IllegalArgumentException.class, () -> new DanioPorTurno("X", 0, 3));
        assertThrows(IllegalArgumentException.class, () -> new DanioPorTurno("X", 5, 0));
    }
}
