package com.model;

import java.util.Random;

public class IntegralRaiz implements IntegralEstrategia {
    private int a, b;

    @Override
    public void generarParametros() {
        Random r = new Random();
        a = r.nextInt(5) + 1;
        b = r.nextInt(5) + 1;
    }

    @Override
    public double calcularResultado(double limiteInferior, double limiteSuperior) {
        // F(x)
        return F(limiteSuperior) - F(limiteInferior);
    }

    private double F(double x) {
        return (2.0 / 3.0) * Math.pow(a * x + b, 1.5) / a;
    }

    @Override
    public String getIntegrandoLatex() {
        return "\\sqrt{" + a + "x + " + b + "}";
    }

    @Override
    public String getPasos() {
        return """
        Sea u = ax + b
        du = a dx
        ∫√(ax+b) dx = (2 / 3a)(ax+b)^{3/2}
        """;
    }
}
