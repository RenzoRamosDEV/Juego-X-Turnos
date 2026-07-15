package com.tuio.juegoxturnos.modelo;

import com.tuio.juegoxturnos.modelo.efectos.EfectoAplicado;
import com.tuio.juegoxturnos.modelo.efectos.EfectoEstado;
import com.tuio.juegoxturnos.util.Aleatorio;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Personaje jugable del juego. Clase base común a Guerrero, Mago, Arquero y
 * Asesino.
 *
 * <p>Cada personaje mantiene su vida, su maná y su lista de ataques. La
 * probabilidad de crítico es propia de cada clase (por ejemplo, el Asesino la
 * tiene más alta), lo que refuerza su especialidad sin romper el equilibrio.
 * Además puede arrastrar {@link EfectoEstado efectos de estado} (veneno,
 * quemadura, aturdimiento...) que se procesan al inicio de cada turno.
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

    /** Puntos de escudo que absorben daño antes que la vida (los otorgan los ítems). */
    private int escudo;

    /** Efectos de estado activos. Se expone como lista de solo lectura. */
    @Getter(AccessLevel.NONE)
    private final List<EfectoEstado> efectos = new ArrayList<>();

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
     * (con posibilidad de crítico), se lo aplica al objetivo y, si procede, le
     * inflige un efecto de estado.
     *
     * @param ataque    ataque a ejecutar; debe ser uno de {@link #getAtaques()}
     * @param objetivo  personaje que recibe el golpe
     * @param aleatorio fuente de aleatoriedad
     * @return el resultado del golpe (daño, crítico y efecto aplicado)
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

        EfectoEstado efectoAplicado = null;
        if (objetivo.estaVivo() && ataque.aplicaEfecto() && aleatorio.ocurre(ataque.getProbabilidadEfecto())) {
            efectoAplicado = ataque.nuevoEfecto();
            objetivo.aplicarEfecto(efectoAplicado);
        }

        return new ResultadoAtaque(ataque, danio, critico, efectoAplicado);
    }

    /**
     * Aplica daño a este personaje. El escudo absorbe el daño primero; el resto
     * se descuenta de la vida, sin dejar que baje de cero.
     *
     * @param danio daño entrante (nunca negativo)
     */
    public void recibirDanio(int danio) {
        int absorbido = Math.min(escudo, danio);
        escudo -= absorbido;
        vida = Math.max(0, vida - (danio - absorbido));
    }

    /** Restaura vida al personaje sin superar su máximo. */
    public void curar(int cantidad) {
        vida = Math.min(vidaMaxima, vida + Math.max(0, cantidad));
    }

    /** Añade puntos de escudo, que absorberán daño antes que la vida. */
    public void anadirEscudo(int puntos) {
        escudo += Math.max(0, puntos);
    }

    /** Regenera el maná del turno, sin superar el máximo. */
    public void regenerarMana() {
        mana = Math.min(manaMaximo, mana + regeneracionMana);
    }

    /** Añade un efecto de estado a este personaje. */
    public void aplicarEfecto(EfectoEstado efecto) {
        efectos.add(efecto);
    }

    /** Elimina todos los efectos de estado activos (p. ej. al usar un antídoto). */
    public void limpiarEfectos() {
        efectos.clear();
    }

    /** @return {@code true} si el personaje tiene algún efecto de estado activo. */
    public boolean tieneEfectos() {
        return !efectos.isEmpty();
    }

    /** @return los nombres de los efectos de estado activos, para mostrarlos. */
    public List<String> nombresEfectosActivos() {
        return efectos.stream().map(EfectoEstado::getNombre).toList();
    }

    /**
     * Procesa los efectos de estado al inicio del turno: aplica su impacto,
     * descuenta su duración y descarta los que hayan expirado.
     *
     * @return la lista de efectos aplicados este turno, para poder narrarlos
     */
    public List<EfectoAplicado> procesarInicioTurno() {
        List<EfectoAplicado> aplicados = new ArrayList<>();
        for (EfectoEstado efecto : efectos) {
            int danio = efecto.aplicarPorTurno(this);
            aplicados.add(new EfectoAplicado(efecto.getNombre(), danio, efecto.impideActuar()));
            efecto.reducirDuracion();
        }
        efectos.removeIf(efecto -> !efecto.activo());
        return aplicados;
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
