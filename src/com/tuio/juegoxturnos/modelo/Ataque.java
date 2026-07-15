package com.tuio.juegoxturnos.modelo;

import com.tuio.juegoxturnos.modelo.efectos.EfectoEstado;
import com.tuio.juegoxturnos.util.Aleatorio;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * Representa un ataque que un {@link Personaje} puede ejecutar.
 *
 * <p>El daño no es fijo: cada golpe produce un valor aleatorio dentro del
 * rango [{@code danioMin}, {@code danioMax}], lo que hace que cada combate sea
 * distinto. Los ataques especiales consumen maná, suelen tener el rango de daño
 * más alto del personaje y pueden aplicar un {@link EfectoEstado} al objetivo.
 */
@Getter
public final class Ataque {

    private final String nombre;
    private final int danioMin;
    private final int danioMax;
    private final int costoMana;
    /** {@code true} si es el ataque especial del personaje. Getter: {@code isEspecial()}. */
    private final boolean especial;

    /** Fábrica del efecto que aplica el ataque, o {@code null} si no aplica ninguno. */
    private final Supplier<EfectoEstado> generadorEfecto;
    /** Probabilidad (0.0-1.0) de que el efecto se aplique al impactar. */
    private final double probabilidadEfecto;

    /**
     * Crea un ataque sin efecto de estado (ataque básico).
     */
    public Ataque(String nombre, int danioMin, int danioMax, int costoMana, boolean especial) {
        this(nombre, danioMin, danioMax, costoMana, especial, null, 0.0);
    }

    /**
     * Crea un ataque que, al impactar, puede aplicar un efecto de estado.
     *
     * @param nombre             nombre visible del ataque
     * @param danioMin           daño mínimo posible (incluido)
     * @param danioMax           daño máximo posible (incluido)
     * @param costoMana          maná necesario para usarlo (0 en ataques básicos)
     * @param especial           {@code true} si es el ataque especial del personaje
     * @param generadorEfecto    fábrica del efecto a aplicar, o {@code null}
     * @param probabilidadEfecto probabilidad (0.0-1.0) de aplicar el efecto
     */
    public Ataque(String nombre, int danioMin, int danioMax, int costoMana, boolean especial,
                  Supplier<EfectoEstado> generadorEfecto, double probabilidadEfecto) {
        if (danioMin < 0 || danioMax < danioMin) {
            throw new IllegalArgumentException("Rango de daño inválido para " + nombre);
        }
        if (costoMana < 0) {
            throw new IllegalArgumentException("El costo de maná no puede ser negativo");
        }
        if (probabilidadEfecto < 0.0 || probabilidadEfecto > 1.0) {
            throw new IllegalArgumentException("La probabilidad de efecto debe estar entre 0.0 y 1.0");
        }
        this.nombre = nombre;
        this.danioMin = danioMin;
        this.danioMax = danioMax;
        this.costoMana = costoMana;
        this.especial = especial;
        this.generadorEfecto = generadorEfecto;
        this.probabilidadEfecto = probabilidadEfecto;
    }

    /** Calcula el daño base de este golpe (aleatorio, sin considerar críticos). */
    public int calcularDanioBase(Aleatorio aleatorio) {
        return aleatorio.entreInclusive(danioMin, danioMax);
    }

    /** Daño medio del ataque, útil para que la IA decida qué conviene usar. */
    public double danioPromedio() {
        return (danioMin + danioMax) / 2.0;
    }

    /** @return {@code true} si el ataque puede aplicar un efecto de estado. */
    public boolean aplicaEfecto() {
        return generadorEfecto != null;
    }

    /**
     * Crea una nueva instancia del efecto de estado de este ataque.
     *
     * @throws IllegalStateException si el ataque no aplica ningún efecto
     */
    public EfectoEstado nuevoEfecto() {
        if (generadorEfecto == null) {
            throw new IllegalStateException(nombre + " no aplica ningún efecto");
        }
        return generadorEfecto.get();
    }
}
