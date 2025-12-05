package com.model;

import java.util.List;
import java.util.Random;

import com.model.Dificultad;

public class IntegralTrig implements IntegralEstrategia {
    private static final Random RAND = new Random();
    private int k;

    @Override
    public void generarParametros(Dificultad dificultad) {
        int rangoMax = switch (dificultad) {
            case FACIL -> 3;
            case DIFICIL -> 10;
            default -> 6;
        };

        k = RAND.nextInt(rangoMax) + 1;
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
    public double evaluarIntegrando(double x) {
        return Math.sin(k * x);
    }

    @Override
    public List<String> getPasos() {
        return List.of(
                "Cambio de variable d/du para reducir el argumento trigonométrico.",
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
