package com.model;

import java.util.List;
import java.util.Random;

import com.model.Dificultad;

public class IntegralFraccion implements IntegralEstrategia {
    private int a, b, c, d;

    @Override
    public void generarParametros(Dificultad dificultad) {
        Random r = new Random();

        int rangoMax = switch (dificultad) {
            case FACIL -> 4;
            case DIFICIL -> 12;
            default -> 8;
        };

        a = valorConSigno(r, rangoMax, dificultad != Dificultad.FACIL);
        b = valorConSigno(r, rangoMax, dificultad == Dificultad.DIFICIL);
        c = Math.max(1, valorConSigno(r, rangoMax, true)); // evita c = 0
        d = valorConSigno(r, rangoMax, dificultad != Dificultad.FACIL);
    }

    private int valorConSigno(Random r, int maximo, boolean permitirNegativo) {
        int valor = r.nextInt(maximo) + 1;
        if (permitirNegativo && r.nextBoolean()) {
            valor *= -1;
        }
        return valor;
    }

    @Override
    public double calcularResultado(double limInf, double limiteSup) {
        double singularidad = -((double) d / c);
        double inicio = Math.min(limInf, limiteSup);
        double fin = Math.max(limInf, limiteSup);

        if (singularidad >= inicio && singularidad <= fin) {
            throw new ArithmeticException("La integral es impropia por una discontinuidad interna");
        }

        double denominadorInferior = c * limInf + d;
        double denominadorSuperior = c * limiteSup + d;
        if (denominadorInferior == 0 || denominadorSuperior == 0) {
            throw new ArithmeticException("La integral tiene un punto no definido en los límites");
        }

        return F(limiteSup) - F(limInf);
    }

    private double F(double x) {
        double k1 = (double) a / c;
        double k2 = (double) (b * c - a * d) / (c * c);
        return k1 * x + k2 * Math.log(Math.abs(c * x + d));
    }

    @Override
    public String getIntegrandoLatex() {
        return String.format("\\frac{%dx + %d}{%dx + %d}", a, b, c, d);
    }

    @Override
    public double evaluarIntegrando(double x) {
        double denominador = c * x + d;
        if (denominador == 0) {
            return Double.NaN;
        }
        return (a * x + b) / denominador;
    }

    @Override
    public List<String> getPasos() {
        return List.of(
                "Fracciones parciales: separar la constante y el término logarítmico.",
                "Reescribimos:",
                String.format("""
                        \\[
                        \\frac{%dx + %d}{%dx + %d}
                        = \\frac{%d}{%d}
                        + \\frac{%d}{%d(cx + d)}
                        \\]
                        """, a, b, c, d, a, c, (b * c - a * d), (c * c)),
                """
                        Entonces:
                        
                        \\[
                        \\int \\frac{ax + b}{cx + d} dx
                        = \\frac{a}{c}x
                        + \\frac{bc - ad}{c^2}\\ln|cx + d| + C
                        \\]
                        """
        );
    }
}
