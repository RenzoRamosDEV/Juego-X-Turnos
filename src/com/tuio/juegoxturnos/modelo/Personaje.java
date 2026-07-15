package com.tuio.juegoxturnos.modelo;

import com.tuio.juegoxturnos.util.Aleatorio;
import lombok.Getter;

import java.util.List;

/**
 * Personaje jugable del juego. Clase base común a Guerrero, Mago, Arquero y
 * Asesino.
 *
 * <p>Cada personaje mantiene su vida, su maná y su lista de ataques. La
 * probabilidad de crítico es propia de cada clase (por ejemplo, el Asesino la
 * tiene más alta), lo que refuerza su especialidad sin romper el equilibrio.
 */
@Getter
public abstract class Personaje {

    /** Multiplicador de daño aplicado cuando un golpe es crítico. */
    public static final double MULTIPLICADOR_CRITICO = 1.5;

    private final String nombre;
    private final int vidaMaxima;
    private int vida;

    private final int manaMaximo;
    private int mana;
    private final int regeneracionMana;

    private final double probabilidadCritico;
    /** Lista inmutable de los ataques del personaje (el especial incluido). */
    private final List<Ataque> ataques;

    /**
     * @param nombre               nombre de la clase de personaje
     * @param vidaMaxima           puntos de vida iniciales y máximos
     * @param manaMaximo           maná máximo acumulable
     * @param manaInicial          maná con el que arranca el combate
     * @param regeneracionMana     maná que recupera al inicio de cada turno
     * @param probabilidadCritico  probabilidad (0.0-1.0) de asestar un crítico
     * @param ataques              los tres ataques del personaje (el especial incluido)
     */
    protected Personaje(String nombre, int vidaMaxima, int manaMaximo, int manaInicial,
                        int regeneracionMana, double probabilidadCritico, List<Ataque> ataques) {
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
        this.manaMaximo = manaMaximo;
        this.mana = Math.min(manaInicial, manaMaximo);
        this.regeneracionMana = regeneracionMana;
        this.probabilidadCritico = probabilidadCritico;
        this.ataques = List.copyOf(ataques);
    }

    /**
     * Ejecuta un ataque contra un objetivo: consume el maná, calcula el daño
     * (con posibilidad de crítico) y se lo aplica al objetivo.
     *
     * @param ataque    ataque a ejecutar; debe ser uno de {@link #getAtaques()}
     * @param objetivo  personaje que recibe el golpe
     * @param aleatorio fuente de aleatoriedad
     * @return el resultado del golpe (daño y si fue crítico)
     * @throws IllegalStateException si no hay maná suficiente para el ataque
     */
    public ResultadoAtaque atacar(Ataque ataque, Personaje objetivo, Aleatorio aleatorio) {
        if (!puedeUsar(ataque)) {
            throw new IllegalStateException("Maná insuficiente para " + ataque.getNombre());
        }
        mana -= ataque.getCostoMana();

        int danio = ataque.calcularDanioBase(aleatorio);
        boolean critico = aleatorio.ocurre(probabilidadCritico);
        if (critico) {
            danio = (int) Math.round(danio * MULTIPLICADOR_CRITICO);
        }

        objetivo.recibirDanio(danio);
        return new ResultadoAtaque(ataque, danio, critico);
    }

    /** Aplica daño a este personaje, sin dejar que la vida baje de cero. */
    public void recibirDanio(int danio) {
        vida = Math.max(0, vida - danio);
    }

    /** Regenera el maná del turno, sin superar el máximo. */
    public void regenerarMana() {
        mana = Math.min(manaMaximo, mana + regeneracionMana);
    }

    /** Indica si hay maná suficiente para usar el ataque dado. */
    public boolean puedeUsar(Ataque ataque) {
        return mana >= ataque.getCostoMana();
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    /** Devuelve el ataque especial del personaje. */
    public Ataque getAtaqueEspecial() {
        return ataques.stream()
                .filter(Ataque::isEspecial)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(nombre + " no tiene ataque especial"));
    }
}
