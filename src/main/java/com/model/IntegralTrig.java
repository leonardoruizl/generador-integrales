package com.model;

import java.util.List;
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
    public List<String> getPasos() {
        return List.of(
                String.format("Sea \\(u = %dx\\), entonces \\(du = %d\\,dx\\).", k, k),
                """
                        \\[
                        \\int \\sin(kx) dx = \\,\\frac{1}{k} \\int \\sin(u) \\; du
                        \\]
                        """,
                """
                        \\[
                        = -\\frac{1}{k} \\cos(u)
                        \\]
                        """,
                """
                        \\[
                        = -\\frac{1}{k} \\cos(kx)
                        \\]
                        """
        );
    }
}
