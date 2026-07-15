package com.tuio.juegoxturnos.modelo;

import com.tuio.juegoxturnos.util.Aleatorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtaqueTest {

    @Test
    @DisplayName("el daño base cae siempre dentro del rango declarado")
    void danioBaseDentroDelRango() {
        Ataque ataque = new Ataque("Espadazo", 20, 25, 0, false);
        Aleatorio aleatorio = new Aleatorio(7);
        for (int i = 0; i < 500; i++) {
            int danio = ataque.calcularDanioBase(aleatorio);
            assertTrue(danio >= 20 && danio <= 25, "daño fuera de rango: " + danio);
        }
    }

    @Test
    @DisplayName("danioPromedio es la media del rango")
    void danioPromedioEsLaMedia() {
        assertEquals(22.5, new Ataque("Corte", 20, 25, 0, false).danioPromedio());
    }

    @Test
    @DisplayName("isEspecial refleja el valor recibido")
    void marcaEspecial() {
        assertTrue(new Ataque("Meteorito", 32, 40, 50, true).isEspecial());
        assertFalse(new Ataque("Bola", 18, 24, 0, false).isEspecial());
    }

    @Test
    @DisplayName("un rango de daño inválido lanza excepción")
    void rangoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Ataque("Malo", 25, 20, 0, false));
        assertThrows(IllegalArgumentException.class, () -> new Ataque("Malo", -1, 5, 0, false));
    }

    @Test
    @DisplayName("un costo de maná negativo lanza excepción")
    void costoManaNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Ataque("Malo", 10, 15, -5, false));
    }
}
