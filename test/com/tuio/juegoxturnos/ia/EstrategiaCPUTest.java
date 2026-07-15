package com.tuio.juegoxturnos.ia;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;
import com.tuio.juegoxturnos.modelo.PersonajeDePrueba;
import com.tuio.juegoxturnos.util.Aleatorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstrategiaCPUTest {

    private static final Ataque BASICO = new Ataque("Básico", 10, 10, 0, false);
    private static final Ataque ESPECIAL = new Ataque("Especial", 30, 30, 50, true);

    private Personaje cpuConMana(int manaInicial) {
        return new PersonajeDePrueba("CPU", 100, 100, manaInicial, 25, 0.0, List.of(BASICO, ESPECIAL));
    }

    private Personaje rivalConVida(int vida) {
        Personaje rival = new PersonajeDePrueba("Rival", 100, 100, 100, 25, 0.0, List.of(BASICO, ESPECIAL));
        rival.recibirDanio(100 - vida);
        return rival;
    }

    @Test
    @DisplayName("remata con el especial si está disponible y el rival está débil")
    void remataAlRivalDebil() {
        EstrategiaCPU ia = new EstrategiaCPU(new Aleatorio(1));
        Personaje cpu = cpuConMana(60);      // puede pagar el especial (50)
        Personaje rival = rivalConVida(20);  // 20% de vida -> por debajo del umbral

        assertSame(ESPECIAL, ia.elegirAtaque(cpu, rival));
    }

    @Test
    @DisplayName("si no hay maná para el especial, elige un ataque básico")
    void sinManaUsaBasico() {
        EstrategiaCPU ia = new EstrategiaCPU(new Aleatorio(1));
        Personaje cpu = cpuConMana(40);      // 40 < 50, no alcanza para el especial
        Personaje rival = rivalConVida(20);

        Ataque elegido = ia.elegirAtaque(cpu, rival);
        assertFalse(elegido.isEspecial());
    }

    @Test
    @DisplayName("la IA nunca elige un ataque que no pueda pagar")
    void siempreElijeAtaquePagable() {
        Personaje rival = rivalConVida(100);
        for (int semilla = 0; semilla < 200; semilla++) {
            EstrategiaCPU ia = new EstrategiaCPU(new Aleatorio(semilla));
            Personaje cpu = cpuConMana(semilla % 101); // maná variado entre 0 y 100
            Ataque elegido = ia.elegirAtaque(cpu, rival);
            assertTrue(cpu.puedeUsar(elegido),
                    "IA eligió un ataque impagable con maná " + cpu.getMana());
        }
    }
}
