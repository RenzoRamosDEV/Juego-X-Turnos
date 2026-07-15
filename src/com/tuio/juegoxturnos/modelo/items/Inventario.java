package com.tuio.juegoxturnos.modelo.items;

import com.tuio.juegoxturnos.modelo.Personaje;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Inventario de objetos consumibles de un combatiente. Mantiene la cantidad
 * disponible de cada objeto y conserva el orden de inserción para mostrarlos de
 * forma estable en el menú.
 */
public final class Inventario {

    private final Map<Item, Integer> cantidades = new LinkedHashMap<>();

    /** Crea el inventario estándar con el que empieza cada combatiente. */
    public static Inventario porDefecto() {
        Inventario inventario = new Inventario();
        inventario.agregar(new PocionVida(), 2);
        inventario.agregar(new Escudo(), 1);
        inventario.agregar(new Antidoto(), 1);
        return inventario;
    }

    /** Añade una cantidad de un objeto al inventario. */
    public void agregar(Item item, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        cantidades.merge(item, cantidad, Integer::sum);
    }

    /** @return los objetos con al menos una unidad disponible, en orden estable. */
    public List<Item> disponibles() {
        return cantidades.entrySet().stream()
                .filter(entrada -> entrada.getValue() > 0)
                .map(Map.Entry::getKey)
                .toList();
    }

    /** @return unidades restantes del objeto indicado. */
    public int cantidad(Item item) {
        return cantidades.getOrDefault(item, 0);
    }

    /** @return {@code true} si no queda ningún objeto por usar. */
    public boolean estaVacio() {
        return disponibles().isEmpty();
    }

    /**
     * Usa un objeto sobre el personaje indicado y descuenta una unidad.
     *
     * @param item    objeto a usar; debe quedar al menos una unidad
     * @param usuario personaje que lo consume
     * @return el mensaje que describe el efecto del objeto
     * @throws IllegalStateException si no quedan unidades del objeto
     */
    public String usar(Item item, Personaje usuario) {
        if (cantidad(item) <= 0) {
            throw new IllegalStateException("No quedan unidades de " + item.getNombre());
        }
        cantidades.merge(item, -1, Integer::sum);
        return item.usar(usuario);
    }
}
