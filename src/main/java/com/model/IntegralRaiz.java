package com.model;

import java.util.List;
import java.util.Random;

import com.model.Dificultad;

public class IntegralRaiz implements IntegralEstrategia {
    private static final Random RAND = new Random();
    private int a, b;

    @Override
    public void generarParametros(Dificultad dificultad) {
        switch (dificultad) {
            case FACIL -> asignarParametros(1, 2, 5, 7);
            case DIFICIL -> asignarParametros(3, 6, 8, 15);
            default -> asignarParametros(2, 4, 6, 10);
        }
    }

    private void asignarParametros(int minA, int maxA, int minFactorB, int maxFactorB) {
        a = RAND.nextInt(maxA - minA + 1) + minA;
        int factor = RAND.nextInt(maxFactorB - minFactorB + 1) + minFactorB;
        b = factor * a; // Garantiza dominio no negativo en [-5, 5]
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
    public double evaluarIntegrando(double x) {
        double inside = a * x + b;
        if (inside < 0) {
            return Double.NaN;
        }
        return Math.sqrt(inside);
    }

    @Override
    public List<String> getPasos() {
        return List.of(
                "Cambio de variable d y du para linealizar la raíz.",
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
