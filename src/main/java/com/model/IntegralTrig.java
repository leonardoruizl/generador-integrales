package com.model;

import java.util.Random;

public class IntegralTrig implements IntegralEstrategia {

    private int k;

    @Override
    public void generarParametros() {
        k = new Random().nextInt(5) + 1;
    }

    @Override
    public double calcularResultado(int limiteInferior, int limiteSuperior) {
        return F(limiteSuperior) - F(limiteInferior);
    }

    private double F(double x) {
        return -Math.cos(k * x) / k;
    }

    @Override
    public String getLatex() {
        return "\\int \\sin(" + k + "x) \\, dx";
    }

    @Override
    public String getPasos() {
        return """
        Sea u = kx → du = k dx
        ∫sin(kx) dx = -cos(kx)/k
        """;
    }
}
