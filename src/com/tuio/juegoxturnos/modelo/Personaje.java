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
 * probabilidad de crítico y de evasión son propias de cada clase (por ejemplo,
 * el Asesino esquiva y hace críticos más a menudo), lo que refuerza su
 * especialidad sin romper el equilibrio. Además puede arrastrar
 * {@link EfectoEstado efectos de estado} (veneno, quemadura, aturdimiento...)
 * que se procesan al inicio de cada turno, y puede defenderse para reducir el
 * daño recibido hasta su siguiente turno.
 */
@Getter
public abstract class Personaje {

    /** Multiplicador de daño aplicado cuando un golpe es crítico. */
    public static final double MULTIPLICADOR_CRITICO = 1.5;

    /** Fracción de daño que se recibe mientras el personaje está defendiendo. */
    public static final double REDUCCION_DEFENSA = 0.5;

    private final String nombre;
    private final int vidaMaxima;
    private int vida;

    private final int manaMaximo;
    private final int manaInicial;
    private int mana;
    private final int regeneracionMana;

    private final double probabilidadCritico;
    /** Probabilidad (0.0-1.0) de esquivar un ataque enemigo. */
    private final double probabilidadEvasion;
    /** Lista inmutable de los ataques del personaje (el especial incluido). */
    private final List<Ataque> ataques;

    /** Puntos de escudo que absorben daño antes que la vida (los otorgan los ítems). */
    private int escudo;
    /** {@code true} si el personaje está defendiendo y recibe daño reducido. */
    private boolean defendiendo;

    /** Efectos de estado activos. Se expone como lista de solo lectura. */
    @Getter(AccessLevel.NONE)
    private final List<EfectoEstado> efectos = new ArrayList<>();

    /**
     * Constructor sin evasión (equivale a evasión 0.0).
     */
    protected Personaje(String nombre, int vidaMaxima, int manaMaximo, int manaInicial,
                        int regeneracionMana, double probabilidadCritico, List<Ataque> ataques) {
        this(nombre, vidaMaxima, manaMaximo, manaInicial, regeneracionMana,
                probabilidadCritico, 0.0, ataques);
    }

    /**
     * @param nombre               nombre de la clase de personaje
     * @param vidaMaxima           puntos de vida iniciales y máximos
     * @param manaMaximo           maná máximo acumulable
     * @param manaInicial          maná con el que arranca el combate
     * @param regeneracionMana     maná que recupera al inicio de cada turno
     * @param probabilidadCritico  probabilidad (0.0-1.0) de asestar un crítico
     * @param probabilidadEvasion  probabilidad (0.0-1.0) de esquivar un ataque
     * @param ataques              los tres ataques del personaje (el especial incluido)
     */
    protected Personaje(String nombre, int vidaMaxima, int manaMaximo, int manaInicial,
                        int regeneracionMana, double probabilidadCritico,
                        double probabilidadEvasion, List<Ataque> ataques) {
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
        this.manaMaximo = manaMaximo;
        this.manaInicial = Math.min(manaInicial, manaMaximo);
        this.mana = this.manaInicial;
        this.regeneracionMana = regeneracionMana;
        this.probabilidadCritico = probabilidadCritico;
        this.probabilidadEvasion = probabilidadEvasion;
        this.ataques = List.copyOf(ataques);
    }

    /**
     * Ejecuta un ataque contra un objetivo: consume el maná y, si el objetivo no
     * lo esquiva, calcula el daño (con posibilidad de crítico), se lo aplica y,
     * si procede, le inflige un efecto de estado.
     *
     * @param ataque    ataque a ejecutar; debe ser uno de {@link #getAtaques()}
     * @param objetivo  personaje que recibe el golpe
     * @param aleatorio fuente de aleatoriedad
     * @return el resultado del golpe (daño, crítico, efecto aplicado y si falló)
     * @throws IllegalStateException si no hay maná suficiente para el ataque
     */
    public ResultadoAtaque atacar(Ataque ataque, Personaje objetivo, Aleatorio aleatorio) {
        if (!puedeUsar(ataque)) {
            throw new IllegalStateException("Maná insuficiente para " + ataque.getNombre());
        }
        mana -= ataque.getCostoMana();

        if (objetivo.probabilidadEvasion > 0 && aleatorio.ocurre(objetivo.probabilidadEvasion)) {
            return new ResultadoAtaque(ataque, 0, false, null, true);
        }

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

        return new ResultadoAtaque(ataque, danio, critico, efectoAplicado, false);
    }

    /**
     * Aplica daño a este personaje. Si está defendiendo, el daño se reduce; el
     * escudo absorbe lo que quede antes que la vida, sin dejar que baje de cero.
     *
     * @param danio daño entrante (nunca negativo)
     */
    public void recibirDanio(int danio) {
        int efectivo = defendiendo ? (int) Math.round(danio * REDUCCION_DEFENSA) : danio;
        int absorbido = Math.min(escudo, efectivo);
        escudo -= absorbido;
        vida = Math.max(0, vida - (efectivo - absorbido));
    }

    /** Restaura vida al personaje sin superar su máximo. */
    public void curar(int cantidad) {
        vida = Math.min(vidaMaxima, vida + Math.max(0, cantidad));
    }

    /** Añade puntos de escudo, que absorberán daño antes que la vida. */
    public void anadirEscudo(int puntos) {
        escudo += Math.max(0, puntos);
    }

    /** Activa la defensa: hasta el próximo turno propio, el daño recibido se reduce. */
    public void defender() {
        defendiendo = true;
    }

    /** Desactiva la defensa (al comenzar el siguiente turno propio). */
    public void finalizarDefensa() {
        defendiendo = false;
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

    /**
     * Prepara al personaje para una nueva ronda de torneo: conserva la vida
     * actual (más una curación), restablece el maná inicial y limpia escudo,
     * defensa y efectos de estado.
     *
     * @param vidaCurada puntos de vida que se recuperan entre rondas
     */
    public void reiniciarParaRonda(int vidaCurada) {
        curar(vidaCurada);
        this.mana = manaInicial;
        this.escudo = 0;
        this.defendiendo = false;
        limpiarEfectos();
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
