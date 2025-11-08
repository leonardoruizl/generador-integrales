package com.model;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Integral {
    private final IntegralEstrategia estrategia;
    private final double limiteInferior, limiteSuperior;
    private final boolean mostrarPasos;

    private double resultado;
    private double[] opciones;
    private int opcionCorrecta;
    private String latex;

    public Integral(String tipo, double limiteInf, double limiteSup, boolean pasos) {
        this.limiteInferior = limiteInf;
        this.limiteSuperior = limiteSup;
        this.mostrarPasos = pasos;

        this.estrategia = crearEstrategia(tipo);
        estrategia.generarParametros();

        resolver();
        generarOpciones();
    }

    private IntegralEstrategia crearEstrategia(String tipo) {
        Objects.requireNonNull(tipo, "tipo no puede ser null");
        String key = tipo.toLowerCase(Locale.ROOT);

        return switch (key) {
            case "raiz" -> new IntegralRaiz();
            case "fraccion" -> new IntegralFraccion();
            case "trig" -> new IntegralTrig();

            case "aleatoria" -> {
                String[] pool = {"raiz", "fraccion", "trig"};
                yield crearEstrategia(pool[new Random().nextInt(pool.length)]);
            }

            default -> throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        };
    }

    private void resolver() {
        resultado = estrategia.calcularResultado(limiteInferior, limiteSuperior);

        latex = String.format(
                "\\int_{%s}^{%s} %s \\, dx",
                fmt(limiteInferior),
                fmt(limiteSuperior),
                estrategia.getIntegrandoLatex()
        );
    }

    private String fmt(double x) {
        if (x == (long) x) return String.format("%d", (long) x);
        return String.valueOf(x);
    }

    private void generarOpciones() {
        opciones = new double[5];
        Random r = new Random();
        opcionCorrecta = r.nextInt(opciones.length);

        // Resultado pequeño → evita ruido numérico
        double base = Math.abs(resultado) < 1e-5 ? 0.0 : resultado;

        // Generar opciones cercanas
        for (int i = 0; i < opciones.length; i++) {
            opciones[i] = base + variacion(base, r);
        }

        // Asignar la opción correcta
        opciones[opcionCorrecta] = base;
        resultado = base;
    }

    private double variacion(double base, Random r) {
        if (base == 0) {
            return (r.nextDouble() - 0.5) * 0.2;
        }
        // Variación hasta ±30% aprox
        double factor = 0.3 * (r.nextDouble() - 0.5);
        return base * factor;
    }

    public double[] getOpciones() {
        return opciones;
    }

    public int getOpcionCorrecta() {
        return opcionCorrecta;
    }

    public double getResultado() {
        return resultado;
    }

    public String getLatex() {
        return latex;
    }

    public String getPasos() {
        return mostrarPasos ? estrategia.getPasos() : "";
    }
}
