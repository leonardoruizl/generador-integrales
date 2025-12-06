package com.model;

import java.util.List;
import java.util.Objects;

public class Integral {
    private final IntegralEstrategia estrategia;
    private final double limiteInferior, limiteSuperior;
    private final Dificultad dificultad;
    private final OpcionGenerator opcionGenerator;
    private final int cantidadOpciones;

    private double resultado;
    private double[] opciones;
    private int opcionCorrecta;
    private String latex;

    public Integral(IntegralEstrategia estrategia, double limiteInf, double limiteSup, Dificultad dificultad,
                    OpcionGenerator opcionGenerator, int cantidadOpciones) {
        this.limiteInferior = limiteInf;
        this.limiteSuperior = limiteSup;
        this.dificultad = dificultad == null ? Dificultad.MEDIA : dificultad;
        this.opcionGenerator = Objects.requireNonNull(opcionGenerator, "opcionGenerator no puede ser null");
        if (cantidadOpciones < 2) {
            throw new IllegalArgumentException("cantidadOpciones debe ser al menos 2");
        }
        this.cantidadOpciones = cantidadOpciones;

        this.estrategia = Objects.requireNonNull(estrategia, "estrategia no puede ser null");
        estrategia.generarParametros(this.dificultad);

        resolver();
        generarOpciones();
    }

    private void resolver() {
        final int maxIntentos = 3;
        for (int i = 0; i < maxIntentos; i++) {
            try {
                resultado = estrategia.calcularResultado(limiteInferior, limiteSuperior);
                break;
            } catch (ArithmeticException e) {
                estrategia.generarParametros(dificultad);
                if (i == maxIntentos - 1) {
                    throw new IllegalStateException("No se pudo generar una integral válida tras varios intentos", e);
                }
            }
        }

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
        OpcionGenerator.OpcionesGeneradas generadas = opcionGenerator.generarOpciones(resultado, cantidadOpciones);
        this.opciones = generadas.opciones();
        this.opcionCorrecta = generadas.opcionCorrecta();
        this.resultado = generadas.resultadoNormalizado();
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

    public List<String> getPasos() {
        return estrategia.getPasos();
    }

    public double evaluarIntegrando(double x) {
        return estrategia.evaluarIntegrando(x);
    }

    public double getLimiteInferior() {
        return limiteInferior;
    }

    public double getLimiteSuperior() {
        return limiteSuperior;
    }
}
