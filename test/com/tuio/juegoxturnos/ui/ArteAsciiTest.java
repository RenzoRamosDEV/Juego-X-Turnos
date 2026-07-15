package com.tuio.juegoxturnos.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArteAsciiTest {

    @ParameterizedTest
    @ValueSource(strings = {"Guerrero", "Mago", "Arquero", "Asesino"})
    @DisplayName("cada personaje tiene arte de 6 líneas no vacías")
    void cadaPersonajeTieneArte(String nombre) {
        List<String> lineas = ArteAscii.lineas(nombre);
        assertEquals(6, lineas.size(), nombre + " debe tener 6 líneas de arte");
        assertTrue(lineas.stream().allMatch(fila -> !fila.isBlank()),
                "ninguna línea del arte debe estar vacía");
    }

    @Test
    @DisplayName("un nombre desconocido devuelve el propio nombre como respaldo")
    void nombreDesconocido() {
        List<String> lineas = ArteAscii.lineas("Dragón");
        assertEquals(List.of("Dragón"), lineas);
        assertFalse(lineas.isEmpty());
    }
}
