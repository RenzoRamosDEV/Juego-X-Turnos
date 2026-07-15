package com.tuio.juegoxturnos.ia;

import com.tuio.juegoxturnos.combate.AccionTurno;
import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.items.Antidoto;
import com.tuio.juegoxturnos.modelo.items.Inventario;
import com.tuio.juegoxturnos.modelo.items.Item;
import com.tuio.juegoxturnos.modelo.items.PocionVida;
import com.tuio.juegoxturnos.util.Aleatorio;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

/**
 * IA sencilla que decide la acción de la CPU en cada turno.
 *
 * <p>La estrategia busca ser competitiva sin resultar predecible:
 * <ul>
 *   <li>Si está malherida y tiene una poción, se cura.</li>
 *   <li>Si está debilitada, arrastra efectos de estado y tiene un antídoto, lo usa.</li>
 *   <li>Si el especial está disponible y el rival está débil, remata con él.</li>
 *   <li>Si el especial está disponible, lo usa la mayoría de las veces por su
 *       alto daño.</li>
 *   <li>En caso contrario elige un ataque básico, favoreciendo el de mayor daño
 *       medio pero dejando margen a la variedad.</li>
 * </ul>
 */
@RequiredArgsConstructor
public final class EstrategiaCPU {

    /** Probabilidad de usar el especial cuando está disponible (rival no crítico). */
    private static final double PROB_USAR_ESPECIAL = 0.75;

    /** Umbral de vida del rival (en %) por debajo del cual se intenta rematar. */
    private static final double UMBRAL_REMATE = 0.35;

    /** Umbral de vida propia (en %) por debajo del cual la CPU intenta curarse. */
    private static final double UMBRAL_CURACION = 0.30;

    /** Umbral de vida propia (en %) por debajo del cual usa el antídoto si está afectada. */
    private static final double UMBRAL_ANTIDOTO = 0.50;

    private final Aleatorio aleatorio;

    /**
     * Decide la acción de la CPU este turno: usar un objeto si conviene o atacar.
     *
     * @param cpu        personaje controlado por la máquina
     * @param rival      personaje del jugador
     * @param inventario objetos disponibles de la CPU
     * @return la acción escogida
     */
    public AccionTurno decidir(Personaje cpu, Personaje rival, Inventario inventario) {
        double vidaRatio = (double) cpu.getVida() / cpu.getVidaMaxima();

        if (vidaRatio <= UMBRAL_CURACION) {
            Item pocion = buscarItem(inventario, PocionVida.class);
            if (pocion != null) {
                return new AccionTurno.UsarObjeto(pocion);
            }
        }
        if (cpu.tieneEfectos() && vidaRatio <= UMBRAL_ANTIDOTO) {
            Item antidoto = buscarItem(inventario, Antidoto.class);
            if (antidoto != null) {
                return new AccionTurno.UsarObjeto(antidoto);
            }
        }

        return new AccionTurno.Atacar(elegirAtaque(cpu, rival));
    }

    /** Devuelve el primer objeto disponible del tipo pedido, o {@code null}. */
    private Item buscarItem(Inventario inventario, Class<? extends Item> tipo) {
        return inventario.disponibles().stream()
                .filter(tipo::isInstance)
                .findFirst()
                .orElse(null);
    }

    /**
     * Elige el ataque que ejecutará la CPU este turno.
     *
     * @param cpu   personaje controlado por la máquina
     * @param rival personaje del jugador
     * @return el ataque escogido (siempre uno que la CPU puede pagar)
     */
    public Ataque elegirAtaque(Personaje cpu, Personaje rival) {
        Ataque especial = cpu.getAtaqueEspecial();
        boolean especialDisponible = cpu.puedeUsar(especial);

        if (especialDisponible) {
            boolean rivalDebil = rival.getVida() <= rival.getVidaMaxima() * UMBRAL_REMATE;
            if (rivalDebil || aleatorio.ocurre(PROB_USAR_ESPECIAL)) {
                return especial;
            }
        }

        return elegirBasico(cpu);
    }

    /**
     * Escoge un ataque básico. Normalmente el de mayor daño medio, pero de vez en
     * cuando otro cualquiera para que la CPU no sea totalmente predecible.
     */
    private Ataque elegirBasico(Personaje cpu) {
        List<Ataque> basicos = cpu.getAtaques().stream()
                .filter(ataque -> !ataque.isEspecial())
                .filter(cpu::puedeUsar)
                .toList();

        if (basicos.isEmpty()) {
            // Situación teórica: sin básicos disponibles, se recurre a cualquier ataque pagable.
            return cpu.getAtaques().stream()
                    .filter(cpu::puedeUsar)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("La CPU no tiene ataques disponibles"));
        }

        if (aleatorio.ocurre(0.30)) {
            return aleatorio.elemento(basicos);
        }
        return basicos.stream()
                .max(Comparator.comparingDouble(Ataque::danioPromedio))
                .orElseThrow();
    }
}
