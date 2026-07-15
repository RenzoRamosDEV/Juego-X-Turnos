package com.tuio.juegoxturnos.ui;

import com.tuio.juegoxturnos.modelo.Personaje;
import lombok.RequiredArgsConstructor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Capa de presentación en la terminal: dibuja el escenario del combate con
 * marcos, barras de vida/maná, un registro de sucesos, menús y narración, y lee
 * la entrada del usuario.
 *
 * <p>Concentrar aquí toda la entrada/salida mantiene la lógica del juego libre
 * de detalles de formato y facilita cambiar la apariencia sin tocar el modelo.
 */
@RequiredArgsConstructor
public final class Consola {

    private static final int ANCHO_BARRA = 20;
    /** Ancho interior de los paneles enmarcados. */
    private static final int ANCHO_PANEL = 50;
    /** Número máximo de sucesos recientes que se muestran. */
    private static final int MAX_SUCESOS = 6;

    private final Scanner entrada;
    private final PrintStream salida;
    /** Milisegundos de pausa entre mensajes para dar ritmo al combate. */
    private final long pausaMs;

    /** Últimos mensajes narrados, para el panel de sucesos. */
    private final List<String> sucesos = new ArrayList<>();

    public void linea(String texto) {
        salida.println(texto);
    }

    public void linea() {
        salida.println();
    }

    /** Escribe un mensaje, lo guarda en el registro de sucesos y hace una pausa. */
    public void narrar(String texto) {
        sucesos.add(texto);
        salida.println(texto);
        pausa();
    }

    /** Título destacado, centrado entre reglas. */
    public void titulo(String texto) {
        int total = ANCHO_PANEL + 4;
        int hueco = Math.max(0, total - anchoVisible(texto) - 2);
        int izquierda = hueco / 2;
        int derecha = hueco - izquierda;
        linea();
        linea(Colores.pintar("═".repeat(izquierda) + " ", Colores.CIAN_CLARO)
                + Colores.pintar(texto, Colores.CIAN_CLARO + Colores.NEGRITA)
                + Colores.pintar(" " + "═".repeat(derecha), Colores.CIAN_CLARO));
    }

    /** Encabezado de sección, más discreto que un título. */
    public void subtitulo(String texto) {
        linea();
        linea(Colores.pintar("▎ ", Colores.CIAN_CLARO) + Colores.pintar(texto, Colores.NEGRITA));
    }

    public void separador() {
        linea(Colores.pintar("┈".repeat(ANCHO_PANEL + 4), Colores.GRIS));
    }

    /** Duerme el hilo la pausa configurada (ignora interrupciones). */
    public void pausa() {
        if (pausaMs <= 0) {
            return;
        }
        try {
            Thread.sleep(pausaMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Limpia la pantalla (solo en modo interactivo, con pausa). */
    public void limpiarPantalla() {
        if (pausaMs <= 0) {
            return;
        }
        salida.print("[H[2J[3J");
        salida.flush();
    }

    /**
     * Dibuja el escenario completo: limpia la pantalla, muestra el título del
     * juego, un panel enmarcado por combatiente (arte, vida y maná) y el registro
     * de sucesos recientes.
     */
    public void mostrarEstado(String nombreUno, Personaje uno, String colorUno,
                              String nombreDos, Personaje dos, String colorDos) {
        limpiarPantalla();
        linea();
        linea(Colores.pintar(centrar(">>=  JUEGO POR TURNOS  =<<", ANCHO_PANEL + 4),
                Colores.AMARILLO_CLARO + Colores.NEGRITA));
        linea();
        panel(nombreUno, contenidoCombatiente(uno, colorUno), colorUno);
        linea();
        panel(nombreDos, contenidoCombatiente(dos, colorDos), colorDos);
        mostrarSucesos();
    }

    /** Pinta el arte ASCII de un personaje con el color indicado. */
    public void mostrarArte(Personaje personaje, String color) {
        for (String fila : ArteAscii.lineas(personaje.getNombre())) {
            linea(Colores.pintar(centrar(fila, ANCHO_PANEL + 4), color));
        }
    }

    /** Construye las filas del panel de un combatiente: arte, vida y maná. */
    private List<String> contenidoCombatiente(Personaje personaje, String color) {
        List<String> filas = new ArrayList<>();
        for (String arte : ArteAscii.lineas(personaje.getNombre())) {
            filas.add(Colores.pintar(centrar(arte, ANCHO_PANEL), color));
        }
        filas.add("");
        filas.add(lineaBarra("VIDA", personaje.getVida(), personaje.getVidaMaxima(),
                colorSegunVida(personaje)));
        filas.add(lineaBarra("MANA", personaje.getMana(), personaje.getManaMaximo(),
                Colores.AZUL_CLARO));

        List<String> extras = new ArrayList<>();
        if (personaje.getEscudo() > 0) {
            extras.add(Colores.pintar("Escudo " + personaje.getEscudo(), Colores.CIAN_CLARO));
        }
        if (personaje.isDefendiendo()) {
            extras.add(Colores.pintar("Defensa", Colores.AZUL_CLARO));
        }
        if (personaje.tieneEfectos()) {
            extras.add(Colores.pintar(String.join(", ", personaje.nombresEfectosActivos()),
                    Colores.MAGENTA_CLARO));
        }
        if (!extras.isEmpty()) {
            filas.add("EST  " + String.join(Colores.pintar(" · ", Colores.GRIS), extras));
        }
        return filas;
    }

    /** Devuelve la línea "ETIQUETA [barra] actual/máx". */
    private String lineaBarra(String etiqueta, int actual, int maximo, String color) {
        return String.format("%-4s ", etiqueta) + barra(actual, maximo, color)
                + String.format(" %3d/%-3d", actual, maximo);
    }

    /** Barra de bloques del tipo {@code ██████░░░░}, coloreada. */
    private String barra(int actual, int maximo, String color) {
        int llenas = maximo == 0 ? 0 : (int) Math.round((double) actual / maximo * ANCHO_BARRA);
        llenas = Math.max(0, Math.min(ANCHO_BARRA, llenas));
        return Colores.pintar("█".repeat(llenas), color)
                + Colores.pintar("░".repeat(ANCHO_BARRA - llenas), Colores.GRIS);
    }

    private String colorSegunVida(Personaje personaje) {
        double ratio = (double) personaje.getVida() / personaje.getVidaMaxima();
        if (ratio > 0.5) {
            return Colores.VERDE_CLARO;
        }
        if (ratio > 0.25) {
            return Colores.AMARILLO_CLARO;
        }
        return Colores.ROJO_CLARO;
    }

    /** Muestra el panel de sucesos recientes, si hay alguno. */
    private void mostrarSucesos() {
        if (sucesos.isEmpty()) {
            return;
        }
        List<String> recientes = sucesos.subList(Math.max(0, sucesos.size() - MAX_SUCESOS), sucesos.size());
        linea();
        panel("Sucesos", new ArrayList<>(recientes), Colores.GRIS);
    }

    /** Dibuja un panel enmarcado con título y contenido. */
    private void panel(String titulo, List<String> contenido, String colorBorde) {
        int ancho = ANCHO_PANEL + 2;
        String tituloRecortado = titulo.length() > ANCHO_PANEL - 4
                ? titulo.substring(0, ANCHO_PANEL - 4) : titulo;
        int relleno = Math.max(0, ancho - (3 + tituloRecortado.length()));
        linea(Colores.pintar("╭─ ", colorBorde)
                + Colores.pintar(tituloRecortado, colorBorde + Colores.NEGRITA)
                + Colores.pintar(" " + "─".repeat(relleno) + "╮", colorBorde));

        for (String fila : contenido) {
            String texto = anchoVisible(fila) > ANCHO_PANEL ? recortarVisible(fila, ANCHO_PANEL) : fila;
            int pad = Math.max(0, ANCHO_PANEL - anchoVisible(texto));
            linea(Colores.pintar("│", colorBorde) + " " + texto + " ".repeat(pad) + " "
                    + Colores.pintar("│", colorBorde));
        }

        linea(Colores.pintar("╰" + "─".repeat(ancho) + "╯", colorBorde));
    }

    /** Centra un texto en el ancho dado, añadiendo espacios a la izquierda. */
    private String centrar(String texto, int ancho) {
        int pad = Math.max(0, (ancho - anchoVisible(texto)) / 2);
        return " ".repeat(pad) + texto;
    }

    /** Longitud visible de un texto, ignorando los códigos de color ANSI. */
    private int anchoVisible(String texto) {
        return texto.replaceAll("\\e\\[[0-9;]*m", "").length();
    }

    /** Recorta un texto a un número de caracteres visibles, conservando el color. */
    private String recortarVisible(String texto, int maximo) {
        StringBuilder sb = new StringBuilder();
        int visibles = 0;
        int i = 0;
        while (i < texto.length()) {
            char c = texto.charAt(i);
            if (c == '') {
                int fin = texto.indexOf('m', i);
                if (fin < 0) {
                    break;
                }
                sb.append(texto, i, fin + 1);
                i = fin + 1;
            } else {
                if (visibles >= maximo) {
                    break;
                }
                sb.append(c);
                visibles++;
                i++;
            }
        }
        return sb.append(Colores.RESET).toString();
    }

    /**
     * Lee una línea de texto no vacía.
     *
     * @param prompt texto a mostrar antes de leer
     * @return el texto introducido, sin espacios sobrantes
     */
    public String leerTexto(String prompt) {
        while (true) {
            salida.print(prompt + " ");
            if (!entrada.hasNextLine()) {
                throw new IllegalStateException("Se agotó la entrada mientras se esperaba un texto");
            }
            String texto = entrada.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            linea(Colores.pintar("El texto no puede estar vacío.", Colores.ROJO));
        }
    }

    /**
     * Lee un entero dentro del rango indicado, reintentando ante entradas inválidas.
     *
     * @param prompt texto a mostrar antes de leer
     * @param min    valor mínimo aceptado (incluido)
     * @param max    valor máximo aceptado (incluido)
     * @return el entero válido introducido por el usuario
     */
    public int leerOpcion(String prompt, int min, int max) {
        while (true) {
            salida.print(Colores.pintar("➤ ", Colores.CIAN_CLARO) + prompt + " ");
            if (!entrada.hasNextLine()) {
                throw new IllegalStateException("Se agotó la entrada mientras se esperaba una opción");
            }
            String texto = entrada.nextLine().trim();
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= min && valor <= max) {
                    return valor;
                }
            } catch (NumberFormatException ignorado) {
                // Se reintenta abajo.
            }
            linea(Colores.pintar("Opción inválida. Escribe un número entre " + min + " y " + max + ".", Colores.ROJO));
        }
    }
}
