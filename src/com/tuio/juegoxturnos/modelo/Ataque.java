package com.tuio.juegoxturnos.modelo;

import com.tuio.juegoxturnos.util.Aleatorio;
import lombok.Getter;

/**
 * Representa un ataque que un {@link Personaje} puede ejecutar.
 *
 * <p>El daño no es fijo: cada golpe produce un valor aleatorio dentro del
 * rango [{@code danioMin}, {@code danioMax}], lo que hace que cada combate sea
 * distinto. Los ataques especiales consumen maná y suelen tener el rango de
 * daño más alto del personaje.
 */
@Getter
public final class Ataque {

    private final String nombre;
    private final int danioMin;
    private final int danioMax;
    private final int costoMana;
    /** {@code true} si es el ataque especial del personaje. Getter: {@code isEspecial()}. */
    private final boolean especial;

    /**
     * @param nombre    nombre visible del ataque
     * @param danioMin  daño mínimo posible (incluido)
     * @param danioMax  daño máximo posible (incluido)
     * @param costoMana maná necesario para usarlo (0 en ataques básicos)
     * @param especial  {@code true} si es el ataque especial del personaje
     */
    public Ataque(String nombre, int danioMin, int danioMax, int costoMana, boolean especial) {
        if (danioMin < 0 || danioMax < danioMin) {
            throw new IllegalArgumentException("Rango de daño inválido para " + nombre);
        }
        if (costoMana < 0) {
            throw new IllegalArgumentException("El costo de maná no puede ser negativo");
        }
        this.nombre = nombre;
        this.danioMin = danioMin;
        this.danioMax = danioMax;
        this.costoMana = costoMana;
        this.especial = especial;
    }

    /** Calcula el daño base de este golpe (aleatorio, sin considerar críticos). */
    public int calcularDanioBase(Aleatorio aleatorio) {
        return aleatorio.entreInclusive(danioMin, danioMax);
    }

    /** Daño medio del ataque, útil para que la IA decida qué conviene usar. */
    public double danioPromedio() {
        return (danioMin + danioMax) / 2.0;
    }
}
