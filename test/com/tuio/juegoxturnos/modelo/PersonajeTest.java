package com.tuio.juegoxturnos.modelo;

import com.tuio.juegoxturnos.modelo.efectos.EfectoAplicado;
import com.tuio.juegoxturnos.modelo.efectos.Efectos;
import com.tuio.juegoxturnos.util.Aleatorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonajeTest {

    private static final Ataque BASICO = new Ataque("Golpe", 10, 10, 0, false);
    private static final Ataque ESPECIAL = new Ataque("Especial", 10, 10, 50, true);
    private static final Ataque ATAQUE_CON_VENENO =
            new Ataque("Envenenar", 10, 10, 0, false, Efectos::veneno, 1.0);

    private Personaje nuevoPersonaje(int vida, int manaInicial, double probCritico) {
        return new PersonajeDePrueba("Prueba", vida, 100, manaInicial, 25, probCritico,
                List.of(BASICO, ESPECIAL));
    }

    @Test
    @DisplayName("un personaje nace con la vida al máximo y vivo")
    void estadoInicial() {
        Personaje p = nuevoPersonaje(120, 60, 0.0);
        assertEquals(120, p.getVida());
        assertEquals(120, p.getVidaMaxima());
        assertTrue(p.estaVivo());
    }

    @Test
    @DisplayName("recibirDanio nunca deja la vida por debajo de cero")
    void danioNoBajaDeCero() {
        Personaje p = nuevoPersonaje(30, 60, 0.0);
        p.recibirDanio(1000);
        assertEquals(0, p.getVida());
        assertFalse(p.estaVivo());
    }

    @Test
    @DisplayName("atacar sin crítico aplica el daño base y consume el maná")
    void ataqueSinCritico() {
        Personaje atacante = nuevoPersonaje(100, 60, 0.0);
        Personaje objetivo = nuevoPersonaje(100, 60, 0.0);

        ResultadoAtaque resultado = atacante.atacar(ESPECIAL, objetivo, new Aleatorio(1));

        assertFalse(resultado.critico());
        assertEquals(10, resultado.danio());
        assertEquals(90, objetivo.getVida());
        assertEquals(10, atacante.getMana(), "60 - 50 de costo");
    }

    @Test
    @DisplayName("un golpe crítico multiplica el daño por 1.5")
    void ataqueConCritico() {
        Personaje atacante = nuevoPersonaje(100, 60, 1.0); // crítico garantizado
        Personaje objetivo = nuevoPersonaje(100, 60, 0.0);

        ResultadoAtaque resultado = atacante.atacar(BASICO, objetivo, new Aleatorio(1));

        assertTrue(resultado.critico());
        assertEquals(15, resultado.danio()); // round(10 * 1.5)
        assertEquals(85, objetivo.getVida());
    }

    @Test
    @DisplayName("no se puede atacar con un especial sin maná suficiente")
    void ataqueSinMana() {
        Personaje atacante = nuevoPersonaje(100, 40, 0.0); // 40 < costo 50
        Personaje objetivo = nuevoPersonaje(100, 60, 0.0);

        assertFalse(atacante.puedeUsar(ESPECIAL));
        assertThrows(IllegalStateException.class,
                () -> atacante.atacar(ESPECIAL, objetivo, new Aleatorio(1)));
    }

    @Test
    @DisplayName("la regeneración de maná no supera el máximo")
    void regeneracionTopeMaximo() {
        Personaje p = nuevoPersonaje(100, 90, 0.0);
        p.regenerarMana(); // 90 + 25 = 115 -> se limita a 100
        assertEquals(100, p.getMana());
    }

    @Test
    @DisplayName("getAtaqueEspecial devuelve el ataque marcado como especial")
    void obtieneEspecial() {
        Personaje p = nuevoPersonaje(100, 60, 0.0);
        assertEquals(ESPECIAL, p.getAtaqueEspecial());
        assertTrue(p.getAtaqueEspecial().isEspecial());
    }

    @Test
    @DisplayName("la lista de ataques es inmutable")
    void ataquesInmutables() {
        Personaje p = nuevoPersonaje(100, 60, 0.0);
        assertThrows(UnsupportedOperationException.class, () -> p.getAtaques().add(BASICO));
    }

    @Test
    @DisplayName("un ataque con efecto lo aplica al objetivo")
    void ataqueAplicaEfecto() {
        Personaje atacante = nuevoPersonaje(100, 60, 0.0);
        Personaje objetivo = nuevoPersonaje(100, 60, 0.0);

        ResultadoAtaque resultado = atacante.atacar(ATAQUE_CON_VENENO, objetivo, new Aleatorio(1));

        assertTrue(resultado.aplicoEfecto());
        assertTrue(objetivo.tieneEfectos());
        assertEquals(List.of("Veneno"), objetivo.nombresEfectosActivos());
    }

    @Test
    @DisplayName("procesarInicioTurno aplica el daño del efecto y lo expira a su tiempo")
    void procesaEfectosPorTurno() {
        Personaje p = nuevoPersonaje(100, 60, 0.0);
        p.aplicarEfecto(Efectos.veneno()); // 6 de daño durante 3 turnos

        for (int turno = 1; turno <= 3; turno++) {
            List<EfectoAplicado> aplicados = p.procesarInicioTurno();
            assertEquals(1, aplicados.size());
            assertEquals(6, aplicados.get(0).danio());
        }
        assertEquals(100 - 18, p.getVida());
        assertFalse(p.tieneEfectos(), "el veneno debe haber expirado tras 3 turnos");
    }

    @Test
    @DisplayName("el aturdimiento se reporta como impedimento de actuar")
    void procesaAturdimiento() {
        Personaje p = nuevoPersonaje(100, 60, 0.0);
        p.aplicarEfecto(Efectos.aturdimiento());

        List<EfectoAplicado> aplicados = p.procesarInicioTurno();
        assertTrue(aplicados.get(0).aturde());
        assertFalse(p.tieneEfectos(), "el aturdimiento de 1 turno debe expirar tras procesarse");
    }

    @Test
    @DisplayName("un objetivo con evasión total esquiva el ataque sin recibir daño")
    void objetivoEsquiva() {
        Personaje atacante = nuevoPersonaje(100, 60, 0.0);
        Personaje objetivo = new PersonajeDePrueba("Evasivo", 100, 100, 60, 25, 0.0, 1.0, List.of(BASICO));

        ResultadoAtaque resultado = atacante.atacar(BASICO, objetivo, new Aleatorio(1));

        assertTrue(resultado.fallado());
        assertEquals(0, resultado.danio());
        assertEquals(100, objetivo.getVida());
    }

    @Test
    @DisplayName("defender reduce a la mitad el daño recibido hasta el siguiente turno")
    void defenderReduceDanio() {
        Personaje p = nuevoPersonaje(100, 60, 0.0);
        p.defender();
        p.recibirDanio(20); // 20 * 0.5 = 10
        assertEquals(90, p.getVida());

        p.finalizarDefensa();
        p.recibirDanio(20); // sin defensa, daño completo
        assertEquals(70, p.getVida());
    }

    @Test
    @DisplayName("reiniciarParaRonda cura, restaura el maná y limpia estados")
    void reinicioDeRonda() {
        Personaje p = nuevoPersonaje(100, 60, 0.0);
        p.recibirDanio(70);          // vida 30
        p.atacar(ESPECIAL, nuevoPersonaje(100, 60, 0.0), new Aleatorio(1)); // maná 60 -> 10
        p.aplicarEfecto(Efectos.veneno());
        p.anadirEscudo(20);
        p.defender();

        p.reiniciarParaRonda(40);

        assertEquals(70, p.getVida());          // 30 + 40
        assertEquals(60, p.getMana());           // maná inicial restaurado
        assertEquals(0, p.getEscudo());
        assertFalse(p.isDefendiendo());
        assertFalse(p.tieneEfectos());
    }
}
