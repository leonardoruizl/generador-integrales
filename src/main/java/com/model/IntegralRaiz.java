package com.model;

import java.util.List;
import java.util.Random;

public class IntegralRaiz implements IntegralEstrategia {
    private static final Random RAND = new Random();
    private int a, b;

    @Override
    public void generarParametros() {
        a = RAND.nextInt(5) + 1;
        b = RAND.nextInt(5) + 1;
    }

    @Override
    public double calcularResultado(double limiteInf, double limiteSup) {
        return F(limiteSup) - F(limiteInf);
    }

    /**
     * F(x) = (2 / (3a)) (ax + b)^(3/2)
     * Para la integral de √(ax + b)
     */
    private double F(double x) {
        double inside = a * x + b;

        // Dominio válido: ax + b ≥ 0
        if (inside < 0) {
            // Mejor dar información trazable
            throw new ArithmeticException(
                    "Dominio inválido en F(x): ax + b < 0 para x = " + x +
                            " (a=" + a + ", b=" + b + ", inside=" + inside + ")"
            );
        }

        // (ax + b)^(3/2)
        return (2.0 / (3.0 * a)) * Math.pow(inside, 1.5);
    }

    @Override
    public String getIntegrandoLatex() {
        return String.format("\\sqrt{%dx + %d}", a, b);
    }

    @Override
    public List<String> getPasos() {
        return List.of(
                String.format("Sea \\(u = %dx + %d\\) ⇒ \\(du = %d\\,dx\\).", a, b, a),
                """
                        \\[
                        \\int \\sqrt{ax + b} \\, dx
                        = \\frac{1}{a} \\int \\sqrt{u} \\, du
                        \\]
                        """,
                """
                        \\[
                        = \\frac{1}{a} \\cdot \\frac{2}{3} u^{3/2}
                        \\]
                        """,
                """
                        \\[
                        = \\frac{2}{3a}(ax + b)^{3/2}
                        \\]
                        """
        );
    }
}
