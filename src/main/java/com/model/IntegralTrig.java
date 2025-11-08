package com.model;

import java.util.Random;

public class IntegralTrig implements IntegralEstrategia {
    private static final Random RAND = new Random();
    private int k;

    @Override
    public void generarParametros() {
        k = RAND.nextInt(5) + 1;
    }

    @Override
    public double calcularResultado(double limiteInf, double limiteSup) {
        return F(limiteSup) - F(limiteInf);
    }

    /**
     * F(x) = -cos(kx) / k
     */
    private double F(double x) {
        return -Math.cos(k * x) / k;
    }

    @Override
    public String getIntegrandoLatex() {
        return String.format("\\sin(%dx)", k);
    }

    @Override
    public String getPasos() {
        return """
                Sustitución:
                Sea u = kx
                ⇒ du = k dx → dx = du / k
                
                ∫ sin(kx) dx
                = (1/k) ∫ sin(u) du
                = -(1/k) cos(u)
                = -cos(kx)/k
                """;
    }
}
