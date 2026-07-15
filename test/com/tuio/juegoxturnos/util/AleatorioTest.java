package com.tuio.juegoxturnos.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AleatorioTest {

    @Test
    @DisplayName("entreInclusive siempre devuelve un valor dentro del rango")
    void entreInclusiveDentroDelRango() {
        Aleatorio aleatorio = new Aleatorio(123);
        for (int i = 0; i < 1000; i++) {
            int valor = aleatorio.entreInclusive(5, 10);
            assertTrue(valor >= 5 && valor <= 10, "valor fuera de rango: " + valor);
        }
    }

    @Test
    @DisplayName("entreInclusive incluye ambos extremos cuando min == max")
    void entreInclusiveExtremoUnico() {
        Aleatorio aleatorio = new Aleatorio(1);
        assertEquals(7, aleatorio.entreInclusive(7, 7));
    }

    @Test
    @DisplayName("misma semilla produce la misma secuencia")
    void mismaSemillaMismaSecuencia() {
        Aleatorio a = new Aleatorio(42);
        Aleatorio b = new Aleatorio(42);
        for (int i = 0; i < 50; i++) {
            assertEquals(a.entreInclusive(0, 100), b.entreInclusive(0, 100));
        }
    }

    @Test
    @DisplayName("ocurre respeta las probabilidades extremas 0.0 y 1.0")
    void ocurreExtremos() {
        Aleatorio aleatorio = new Aleatorio(9);
        assertAll(
                () -> assertFalse(aleatorio.ocurre(0.0), "prob 0.0 nunca debe ocurrir"),
                () -> assertTrue(aleatorio.ocurre(1.0), "prob 1.0 siempre debe ocurrir")
        );
    }

    @Test
    @DisplayName("entreInclusive rechaza min mayor que max")
    void entreInclusiveRangoInvalido() {
        Aleatorio aleatorio = new Aleatorio(0);
        assertThrows(IllegalArgumentException.class, () -> aleatorio.entreInclusive(10, 5));
    }

    @Test
    @DisplayName("elemento devuelve un elemento contenido en la lista")
    void elementoPertenece() {
        Aleatorio aleatorio = new Aleatorio(3);
        List<String> lista = List.of("a", "b", "c");
        for (int i = 0; i < 20; i++) {
            assertTrue(lista.contains(aleatorio.elemento(lista)));
        }
    }
}
