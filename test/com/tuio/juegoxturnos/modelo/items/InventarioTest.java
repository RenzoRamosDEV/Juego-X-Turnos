package com.tuio.juegoxturnos.modelo.items;

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

class InventarioTest {

    private Personaje personaje() {
        return new PersonajeDePrueba("Prueba", 100, 100, 60, 25, 0.0,
                List.of(new Ataque("Golpe", 10, 10, 0, false)));
    }

    @Test
    @DisplayName("el inventario por defecto trae 2 pociones, 1 escudo y 1 antídoto")
    void inventarioPorDefecto() {
        Inventario inventario = Inventario.porDefecto();
        assertEquals(3, inventario.disponibles().size());
        assertEquals(4, inventario.disponibles().stream().mapToInt(inventario::cantidad).sum());
    }

    @Test
    @DisplayName("usar un objeto descuenta una unidad")
    void usarDescuentaUnidad() {
        Inventario inventario = new Inventario();
        PocionVida pocion = new PocionVida();
        inventario.agregar(pocion, 2);

        inventario.usar(pocion, personaje());
        assertEquals(1, inventario.cantidad(pocion));
    }

    @Test
    @DisplayName("los objetos agotados dejan de estar disponibles")
    void objetoAgotado() {
        Inventario inventario = new Inventario();
        Escudo escudo = new Escudo();
        inventario.agregar(escudo, 1);

        inventario.usar(escudo, personaje());
        assertEquals(0, inventario.cantidad(escudo));
        assertFalse(inventario.disponibles().contains(escudo));
        assertTrue(inventario.estaVacio());
    }

    @Test
    @DisplayName("usar un objeto sin unidades lanza excepción")
    void usarSinUnidades() {
        Inventario inventario = new Inventario();
        assertThrows(IllegalStateException.class, () -> inventario.usar(new PocionVida(), personaje()));
    }
}
