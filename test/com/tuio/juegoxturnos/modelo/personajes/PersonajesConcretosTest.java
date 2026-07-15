package com.tuio.juegoxturnos.modelo.personajes;

import com.tuio.juegoxturnos.modelo.Ataque;
import com.tuio.juegoxturnos.modelo.Personaje;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonajesConcretosTest {

    static Stream<Supplier<Personaje>> personajes() {
        return Stream.of(Guerrero::new, Mago::new, Arquero::new, Asesino::new);
    }

    @ParameterizedTest
    @MethodSource("personajes")
    @DisplayName("cada personaje tiene exactamente 3 ataques y uno solo especial")
    void estructuraDeAtaques(Supplier<Personaje> fabrica) {
        Personaje p = fabrica.get();
        assertEquals(3, p.getAtaques().size());
        long especiales = p.getAtaques().stream().filter(Ataque::isEspecial).count();
        assertEquals(1, especiales, p.getNombre() + " debe tener un único ataque especial");
    }

    @ParameterizedTest
    @MethodSource("personajes")
    @DisplayName("el especial pega, de media, más que cualquier ataque básico")
    void especialEsElMasFuerte(Supplier<Personaje> fabrica) {
        Personaje p = fabrica.get();
        double promedioEspecial = p.getAtaqueEspecial().danioPromedio();
        double maxBasico = p.getAtaques().stream()
                .filter(a -> !a.isEspecial())
                .mapToDouble(Ataque::danioPromedio)
                .max()
                .orElseThrow();
        assertTrue(promedioEspecial > maxBasico,
                p.getNombre() + ": especial " + promedioEspecial + " vs básico " + maxBasico);
    }

    @Test
    @DisplayName("las estadísticas coinciden con el diseño del juego")
    void estadisticasDeDiseno() {
        assertEquals(140, new Guerrero().getVidaMaxima());
        assertEquals(100, new Mago().getVidaMaxima());
        assertEquals(110, new Arquero().getVidaMaxima());
        assertEquals(105, new Asesino().getVidaMaxima());

        // El Asesino es quien más críticos asesta.
        double critAsesino = new Asesino().getProbabilidadCritico();
        List<Double> otros = List.of(
                new Guerrero().getProbabilidadCritico(),
                new Mago().getProbabilidadCritico(),
                new Arquero().getProbabilidadCritico());
        assertTrue(otros.stream().allMatch(c -> critAsesino > c),
                "el Asesino debe tener la mayor probabilidad de crítico");
    }

    @Test
    @DisplayName("el Mago posee el ataque de mayor daño potencial del juego")
    void magoTieneElMayorDanio() {
        int maxMago = new Mago().getAtaqueEspecial().getDanioMax();
        Stream<Personaje> otros = Stream.of(new Guerrero(), new Arquero(), new Asesino());
        assertTrue(otros.allMatch(p -> maxMago >= p.getAtaqueEspecial().getDanioMax()));
    }
}
