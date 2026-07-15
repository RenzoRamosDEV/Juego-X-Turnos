package com.tuio.juegoxturnos.modelo.efectos;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.PersonajeDePrueba;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AturdimientoTest {

    @Test
    @DisplayName("impide actuar mientras está activo")
    void impideActuar() {
        Aturdimiento aturdimiento = new Aturdimiento(1);
        assertTrue(aturdimiento.impideActuar());
        aturdimiento.reducirDuracion();
        assertFalse(aturdimiento.impideActuar());
        assertFalse(aturdimiento.activo());
    }

    @Test
    @DisplayName("no inflige daño al portador")
    void noHaceDanio() {
        Personaje portador = new PersonajeDePrueba("Prueba", 50, 100, 60, 25, 0.0,
                List.of(new Ataque("Golpe", 10, 10, 0, false)));
        assertEquals(0, new Aturdimiento(1).aplicarPorTurno(portador));
        assertEquals(50, portador.getVida());
    }
}
